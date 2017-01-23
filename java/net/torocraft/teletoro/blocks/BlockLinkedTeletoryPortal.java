package net.torocraft.teletoro.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.TeleToroUtil;
import net.torocraft.teletoro.Teletory;
import net.torocraft.teletoro.item.ItemTeletoryPortalLinker;
import net.torocraft.teletoro.item.ItemTeletoryPortalLinker.ControlBlockLocation;

public class BlockLinkedTeletoryPortal extends BlockAbstractPortal implements ITileEntityProvider {

	public static BlockLinkedTeletoryPortal INSTANCE;

	public static Item ITEM_INSTANCE;

	public static final String NAME = "linkedteletoryportalblock";

	public static void init() {
		INSTANCE = (BlockLinkedTeletoryPortal) new BlockLinkedTeletoryPortal().setUnlocalizedName(NAME);
		ResourceLocation resourceName = new ResourceLocation(TeleToro.MODID, NAME);
		INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(INSTANCE);

		ITEM_INSTANCE = new ItemBlock(INSTANCE);
		ITEM_INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(ITEM_INSTANCE);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	public BlockLinkedTeletoryPortal() {
		this.isBlockContainer = true;
	}

	@Override
	public Size getSizer(World worldIn, BlockPos p_i45694_2_, Axis p_i45694_3_) {
		return new BlockLinkedTeletoryPortal.Size(worldIn, p_i45694_2_, p_i45694_3_);
	}

	@Override
	protected void onPlayerEnterPortal(final EntityPlayerMP player, BlockPos thisPortalLocation) {

		ControlBlockLocation thisPortal = ItemTeletoryPortalLinker.findControllerBlock(player.world, thisPortalLocation,
				ItemTeletoryPortalLinker.LINKED_SIZER);

		if (thisPortal == null) {
			return;
		}

		TileEntityLinkedTeletoryPortal te = getTileEntity(player, thisPortal.pos);

		if (te == null) {
			breakPortal(player, thisPortalLocation);
			return;
		}

		if (te.getDimId() != player.dimension) {
			// TODO support change dimension to specific place
			// Teletory.changeEntityDimension(player, TeleportorType.PORTAL);
			breakPortal(player, thisPortalLocation);
			return;
		}

		if (te.getDestination() == null) {
			breakPortal(player, thisPortalLocation);
			return;
		}

		final ControlBlockLocation remotePortal = ItemTeletoryPortalLinker.findControllerBlock(player.world, te.getDestination(),
				ItemTeletoryPortalLinker.LINKED_SIZER);

		if (remotePortal == null) {
			breakPortal(player, thisPortalLocation);
			return;
		}

		if (player.world.getBlockState(remotePortal.pos).getBlock() != BlockLinkedTeletoryPortal.INSTANCE) {
			breakPortal(player, thisPortalLocation);
			return;
		}

		final float yaw;
		if (Axis.X.equals(remotePortal.axis)) {
			if (te.getSide() == 1) {
				yaw = 0;
			} else {
				yaw = 180;
			}
		} else {
			if (te.getSide() == 1) {
				yaw = -90;
			} else {
				yaw = 90;
			}
		}

		Vec3d transportTo;

		if (Axis.X.equals(remotePortal.axis)) {
			transportTo = new Vec3d(d(remotePortal.pos.getX()) + 0.5d, remotePortal.pos.getY() + 1, remotePortal.pos.getZ() + 0.5);
		} else {
			transportTo = new Vec3d(remotePortal.pos.getX() + 0.5, remotePortal.pos.getY() + 1, d(remotePortal.pos.getZ()) + 0.5d);
		}

		queueTeleport(player, yaw, transportTo);

		// TODO support other entities
	}

	private double d(int x) {
		return (double) x;
	}

	private void queueTeleport(final EntityPlayerMP player, final float yaw, final Vec3d transportTo) {
		Teletory.runQueue.put(new Runnable() {
			@Override
			public void run() {

				if (!player.connection.getNetworkManager().isChannelOpen() || player.isPlayerSleeping()) {
					return;
				}

				player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
						SoundCategory.PLAYERS, 1.0F, 1.0F);

				TeleToroUtil.setInvulnerableDimensionChange(player, true);

				player.connection.setPlayerLocation(transportTo.xCoord, transportTo.yCoord, transportTo.zCoord, yaw, player.rotationPitch);
				player.motionX = 0.0D;
				player.motionY = 0.0D;
				player.motionZ = 0.0D;
				// player.velocityChanged = true;

				TeleToroUtil.setInvulnerableDimensionChange(player, false);

				player.world.playSound((EntityPlayer) null, transportTo.xCoord, transportTo.yCoord, transportTo.zCoord,
						SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

				hurtPlayer(player, transportTo);
			}
		}, 1);
	}

	private void hurtPlayer(final EntityLivingBase entity, Vec3d transportTo) {
		if (Teletory.isWearingEnderBoots(entity)) {
			Teletory.damageEnderBoots(entity);
			return;
		}
		entity.fallDistance = 0.0F;
		entity.attackEntityFrom(DamageSource.FALL, 4f);
		if (entity.world.rand.nextFloat() < 0.05F && entity.world.getGameRules().getBoolean("doMobSpawning")) {
			EntityEndermite entityendermite = new EntityEndermite(entity.world);
			entityendermite.setSpawnedByPlayer(true);
			System.out.println("spawn at " + transportTo.xCoord + " " + transportTo.yCoord + " " + transportTo.zCoord);
			entityendermite.setLocationAndAngles(transportTo.xCoord, transportTo.yCoord, transportTo.zCoord, entity.rotationYaw,
					entity.rotationPitch);
			entity.world.spawnEntity(entityendermite);
		}
	}

	private void breakPortal(EntityPlayerMP player, BlockPos thisPortalLocation) {
		player.world.setBlockState(thisPortalLocation, Blocks.AIR.getDefaultState());
		player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 1.0F,
				1.0F);
	}

	private TileEntityLinkedTeletoryPortal getTileEntity(EntityPlayerMP player, BlockPos pos) {
		try {
			return (TileEntityLinkedTeletoryPortal) player.world.getTileEntity(pos);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		/*
		 * disable particles
		 */
	}

	/**
	 * Called on both Client and Server when World#addBlockEvent is called. On
	 * the Server, this may perform additional changes to the world, like
	 * pistons replacing the block with an extended base. On the client, the
	 * update may involve replacing tile entities, playing sounds, or performing
	 * other visual actions to reflect the server side changes.
	 */
	@SuppressWarnings("deprecation")
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	public static class Size extends BlockAbstractPortal.Size {

		public Size(World world, BlockPos pos, Axis axis) {
			super(world, pos, axis);
		}

		@Override
		public Block getFrameBlock() {
			return BlockEnder.INSTANCE;
		}

		@Override
		public BlockAbstractPortal getPortalBlock() {
			return INSTANCE;
		}

	}

}