package net.torocraft.teletoro.teletory;

import static net.torocraft.teletoro.TeleToroUtil.getBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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
				// thePlayer.changeDimension(Teletory.DIMID);
				// thePlayer.mcServer.getPlayerList().transferPlayerToDimension(thePlayer,
				// Teletory.DIMID, new
				// TeletoryTeleporter(thePlayer.mcServer.worldServerForDimension(Teletory.DIMID)));

				changeDimension(thePlayer, Teletory.DIMID);

			} else {
				changeDimension(thePlayer, 0);

				// thePlayer.changeDimension(Teletory.DIMID);

			}
		}
	}

	private void changeDimension(EntityPlayerMP thePlayer, int dimId) {
		// EntityPlayerMP

		TeleToroUtil.setInvulnerableDimensionChange(thePlayer);

		thePlayer.timeUntilPortal = 10;
		thePlayer.mcServer.getPlayerList().transferPlayerToDimension(thePlayer, dimId, new TeletoryTeleporter(thePlayer.mcServer.worldServerForDimension(dimId)));

	}

	public boolean tryToCreatePortal(World par1World, int par2, int par3, int par4) {
		byte b0 = 0;
		byte b1 = 0;
		if (getBlock(par1World, par2 - 1, par3, par4) == BlockEnder.INSTANCE || getBlock(par1World, par2 + 1, par3, par4) == BlockEnder.INSTANCE) {
			b0 = 1;
		}
		if (getBlock(par1World, par2, par3, par4 - 1) == BlockEnder.INSTANCE || getBlock(par1World, par2, par3, par4 + 1) == BlockEnder.INSTANCE) {
			b1 = 1;
		}

		System.out.println(getBlock(par1World, par2 - 1, par3, par4));
		System.out.println(getBlock(par1World, par2 + 1, par3, par4));
		System.out.println(getBlock(par1World, par2, par3, par4 - 1));
		System.out.println(getBlock(par1World, par2, par3, par4 + 1));

		System.out.println("b0[" + b0 + "] b1[" + b1 + "]");

		if (b0 == b1) {
			return false;
		} else {
			if (getBlock(par1World, par2 - b0, par3, par4 - b1) == Blocks.AIR) {
				par2 -= b0;
				par4 -= b1;
			}
			int l;
			int i1;
			for (l = -1; l <= 2; ++l) {
				for (i1 = -1; i1 <= 3; ++i1) {
					boolean flag = l == -1 || l == 2 || i1 == -1 || i1 == 3;
					if (l != -1 && l != 2 || i1 != -1 && i1 != 3) {
						Block j1 = getBlock(par1World, par2 + b0 * l, par3 + i1, par4 + b1 * l);
						if (flag) {
							if (j1 != BlockEnder.INSTANCE) {
								return false;
							}
						}
					}
				}
			}
			for (l = 0; l < 2; ++l) {
				for (i1 = 0; i1 < 3; ++i1) {
					IBlockState iblockstate = this.getDefaultState().withProperty(BlockPortal.AXIS, b0 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
					par1World.setBlockState(new BlockPos(par2 + b0 * l, par3 + i1, par4 + b1 * l), iblockstate, 3);
				}
			}
			return true;
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