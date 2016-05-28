package net.torocraft.teletoro.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToroMod;

public class BlockEnder extends Block {

	public BlockEnder() {
		super(Material.IRON);
		setHardness(5.0F);
		setResistance(10F);
		setSoundType(SoundType.METAL);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setLightLevel(0.2f);
		setUnlocalizedName(NAME);
	}

	public static BlockEnder INSTANCE;

	public static Item ITEM_INSTANCE;

	public static final String NAME = "enderBlock";

	public static void init() {
		INSTANCE = new BlockEnder();
		GameRegistry.registerBlock(INSTANCE, NAME);
		ITEM_INSTANCE = GameRegistry.findItem(TeleToroMod.MODID, NAME);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToroMod.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}

	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		spawnParticles(worldIn, pos);
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
	}

	private void spawnParticles(World world, BlockPos pos) {
		for (int i = 0; i < 32; ++i) {
			world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5 + world.rand.nextDouble() * 1.0D, pos.getZ() + 0.5, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian(),
					new int[0]);
		}
	}
}