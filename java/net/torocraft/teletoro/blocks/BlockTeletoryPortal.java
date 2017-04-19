package net.torocraft.teletoro.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.TeleToroUtil.TeleportorType;
import net.torocraft.teletoro.Teletory;

public class BlockTeletoryPortal extends BlockAbstractPortal {

	public static BlockTeletoryPortal INSTANCE;

	public static Item ITEM_INSTANCE;

	public static final String NAME = "teletoryportalblock";

	public static void init() {
		INSTANCE = (BlockTeletoryPortal) new BlockTeletoryPortal().setUnlocalizedName(NAME);
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
		Teletory.changeEntityDimension(player, TeleportorType.PORTAL);
	}

	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Blocks.AIR);
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