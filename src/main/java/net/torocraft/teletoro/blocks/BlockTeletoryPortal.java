package net.torocraft.teletoro.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToroMod;
import net.torocraft.teletoro.TeleToroUtil.TeleportorType;
import net.torocraft.teletoro.Teletory;

public class BlockTeletoryPortal extends BlockAbstractPortal {

	public static BlockTeletoryPortal INSTANCE;

	public static Item ITEM_INSTANCE;

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
	public Size getSizer(World worldIn, BlockPos p_i45694_2_, Axis p_i45694_3_) {
		return new BlockTeletoryPortal.Size(worldIn, p_i45694_2_, p_i45694_3_);
	}

	protected void onPlayerEnterPortal(EntityPlayerMP player) {
		Teletory.changeEntityDimension(player, TeleportorType.PORTAL);
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