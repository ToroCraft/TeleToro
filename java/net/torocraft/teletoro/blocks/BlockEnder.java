package net.torocraft.teletoro.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
		setLightLevel(0.5f);
		setUnlocalizedName(NAME);
	}

	public static BlockEnder INSTANCE;

	public static Item ITEM_INSTANCE;

	public static final String NAME = "enderblock";

	public static void init() {
		INSTANCE = new BlockEnder();
		ResourceLocation resourceName = new ResourceLocation(TeleToroMod.MODID, NAME);
		INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(INSTANCE);

		ITEM_INSTANCE = new ItemBlock(INSTANCE);
		ITEM_INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(ITEM_INSTANCE);
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
			world.spawnParticle(EnumParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY() + 0.5 + world.rand.nextDouble() * 1.0D, pos.getZ() + 0.5, world.rand.nextGaussian(), 0.0D, world.rand.nextGaussian(), new int[0]);
		}
	}

	/**
	 * Returns the quantity of items to drop on block destruction.
	 */
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(2);
	}

	/**
	 * Get the quantity dropped based on the given fortune level
	 */
	public int quantityDroppedWithBonus(int fortune, Random random) {
		return MathHelper.clamp_int(this.quantityDropped(random) + random.nextInt(fortune + 1), 1, 4);
	}

	/**
	 * Get the Item that this Block should drop when harvested.
	 */
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Items.ENDER_PEARL;
	}

	protected boolean canSilkHarvest() {
		return true;
	}
}
