package net.torocraft.teletoro.teletory;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToroMod;
import net.torocraft.teletoro.TeleToroUtil;
import net.torocraft.teletoro.blocks.BlockEnder;

public class BlockTeletoryPortal extends BlockAbstractPortal {

	public static BlockTeletoryPortal INSTANCE;

	public static Item ITEM_INSTANCE;

	// public static final Block FRAME_BLOCK = BlockEnder.INSTANCE;

	public static final String NAME = "teletoryPortalBlock";

	public static void init() {
		INSTANCE = (BlockTeletoryPortal) new BlockTeletoryPortal().setUnlocalizedName(NAME);

		GameRegistry.registerBlock(INSTANCE, NAME);
		ITEM_INSTANCE = GameRegistry.findItem(TeleToroMod.MODID, NAME);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToroMod.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par5Entity) {

		int par2 = pos.getX();
		int par3 = pos.getY();
		int par4 = pos.getZ();

		if (par5Entity.getRidingEntity() == null && !par5Entity.isBeingRidden() && par5Entity instanceof EntityPlayerMP) {

			EntityPlayerMP thePlayer = (EntityPlayerMP) par5Entity;

			if (thePlayer.timeUntilPortal > 0) {
				thePlayer.timeUntilPortal = 10;
			} else if (thePlayer.dimension != Teletory.DIMID) {
				thePlayer.timeUntilPortal = 10;
				changeDimension(thePlayer, Teletory.DIMID);
				thePlayer.addStat(TeleToroMod.TELETORY_ACHIEVEMNT);
			} else {
				changeDimension(thePlayer, 0);
			}
		}
	}

	private void changeDimension(EntityPlayerMP player, int dimId) {
		TeleToroUtil.setInvulnerableDimensionChange(player);
		TeleToroUtil.resetStatusFields(player);
		player.timeUntilPortal = 10;
		player.mcServer.getPlayerList().transferPlayerToDimension(player, dimId, new TeletoryTeleporter(player.mcServer.worldServerForDimension(dimId)));
		player.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false));
	}

	public boolean trySpawnPortal(World worldIn, BlockPos pos) {
		Size size = new Size(worldIn, pos, EnumFacing.Axis.X);

		if (size.isValid() && size.portalBlockCount == 0) {
			size.placePortalBlocks();
			return true;
		} else {
			Size size1 = new Size(worldIn, pos, EnumFacing.Axis.Z);

			if (size1.isValid() && size1.portalBlockCount == 0) {
				size1.placePortalBlocks();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public Size getSizer(World worldIn, BlockPos p_i45694_2_, Axis p_i45694_3_) {
		return new BlockTeletoryPortal.Size(worldIn, p_i45694_2_, p_i45694_3_);
	}

	public static class Size extends BlockAbstractPortal.Size {

		public Size(World worldIn, BlockPos p_i45694_2_, Axis p_i45694_3_) {
			super(worldIn, p_i45694_2_, p_i45694_3_);
		}

		@Override
		public Block getFrameBlock() {
			return BlockEnder.INSTANCE;
		}

	}

}