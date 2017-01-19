package net.torocraft.teletoro.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.blocks.BlockAbstractPortal.Size;

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
			System.out.println("used on portal");
			
			//BlockPos controlBlock = findControllerBlock(world, new BlockPos(hitX, hitY, hitZ));
			BlockPos controlBlock = findControllerBlock(world, pos);
			
			if(controlBlock != null){
				world.setBlockState(controlBlock, Blocks.COBBLESTONE.getDefaultState());
			}
			
			
			System.out.println(controlBlock);
			
		}

		return EnumActionResult.PASS;
	}
	
	public static BlockPos findControllerBlock(World world, BlockPos pos) {
		Size size = new BlockTeletoryPortal.Size(world, pos, EnumFacing.Axis.X);
		
		
		if (size.isValid()) {
			return size.getBottomLeft();
		}
		
		size = new BlockTeletoryPortal.Size(world, pos, EnumFacing.Axis.Z);
		
		if (size.isValid()) {
			return size.getBottomLeft();
		}
		
		return null;
	}

}
