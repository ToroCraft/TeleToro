package net.torocraft.teletoro.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;
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

	@Override
	public Size getSizer(World worldIn, BlockPos p_i45694_2_, Axis p_i45694_3_) {
		return new BlockTeletoryPortal.Size(worldIn, p_i45694_2_, p_i45694_3_);
	}

	@Override
	protected void onPlayerEnterPortal(EntityPlayerMP player, BlockPos pos) {
		teleport(player, pos);
	}

	private void teleport(EntityPlayerMP player, BlockPos pos) {
		ControlBlockLocation thisPortal = ItemTeletoryPortalLinker.findControllerBlock(player.world, pos);
		TileEntityLinkedTeletoryPortal te = getTileEntity(player, pos);

		if (te == null || thisPortal == null) {
			return;
		}

		if (te.getDimId() != player.dimension) {
			// TODO support change dimension to specific place
			// Teletory.changeEntityDimension(player, TeleportorType.PORTAL);
			System.out.println("Handle inter dimensional links");
			return;
		}

		// find remote control block and verify portal

		ControlBlockLocation remotePortal = ItemTeletoryPortalLinker.findControllerBlock(player.world, te.getDestination());

		if (remotePortal == null) {
			// TODO also verify if the remote portal points back here, if not
			// break
			player.world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}

		// TODO correct yaw based on facing of remote portal
		player.connection.setPlayerLocation(remotePortal.pos.getX(), remotePortal.pos.getX(), remotePortal.pos.getX(), player.rotationYaw,
				player.rotationPitch);

		// TODO support other entities
		// entityIn.setLocationAndAngles(d5, d6, d7, entityIn.rotationYaw,
		// entityIn.rotationPitch);

		// slap the player
	}

	private TileEntityLinkedTeletoryPortal getTileEntity(EntityPlayerMP player, BlockPos pos) {
		try {
			return (TileEntityLinkedTeletoryPortal) player.world.getTileEntity(pos);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public BlockAbstractPortal getPortalBlock() {
		return INSTANCE;
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

}