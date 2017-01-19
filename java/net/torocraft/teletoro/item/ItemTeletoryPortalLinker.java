package net.torocraft.teletoro.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.blocks.BlockAbstractPortal.Size;
import net.torocraft.teletoro.blocks.BlockAbstractPortal;
import net.torocraft.teletoro.blocks.BlockLinkedTeletoryPortal;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;

public class ItemTeletoryPortalLinker extends Item {

	public static ItemTeletoryPortalLinker INSTANCE;

	public static final String NAME = "teletory_portal_linker";

	public static void init() {
		INSTANCE = new ItemTeletoryPortalLinker();
		ResourceLocation resourceName = new ResourceLocation(TeleToro.MODID, NAME.toLowerCase());
		GameRegistry.register(INSTANCE, resourceName);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory"));
	}

	public ItemTeletoryPortalLinker() {
		setUnlocalizedName(NAME);
		this.maxStackSize = 16;
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == BlockTeletoryPortal.INSTANCE) {
			ControlBlockLocation loc = findControllerBlock(world, pos);
			if(loc != null){
				BlockTeletoryPortal.Size size = new BlockTeletoryPortal.Size(world, loc.pos, loc.axis);
				size.placePortalBlocks(BlockLinkedTeletoryPortal.INSTANCE);
			}

			world.setTileEntity(loc.pos, null);
		}
		return EnumActionResult.PASS;
	}
	
	public static ControlBlockLocation findControllerBlock(World world, BlockPos pos) {
		Size size = new BlockTeletoryPortal.Size(world, pos, Axis.X);
		
		ControlBlockLocation loc = new ControlBlockLocation();
		
		if (size.isValid()) {
			loc.pos = size.getBottomLeft();
			loc.axis = Axis.X;
			return loc;
		}
		
		size = new BlockTeletoryPortal.Size(world, pos, Axis.Z);
		
		if (size.isValid()) {
			loc.pos = size.getBottomLeft();
			loc.axis = Axis.Z;
			return loc;
		}
		
		return null;
	}
	
	public static class ControlBlockLocation {
		public Axis axis;
		public BlockPos pos;
	}

}
