package net.torocraft.teletoro.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;

public class BlockEnderOre extends Block {

	public static BlockEnderOre INSTANCE;

	public static Item ITEM_INSTANCE;

	public static final String NAME = "enderOre";

	public static void init() {
		INSTANCE = new BlockEnderOre();
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

	public BlockEnderOre() {
		this(Material.ROCK.getMaterialMapColor());
	}

	public BlockEnderOre(MapColor color) {
		super(Material.ROCK, color);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setUnlocalizedName(NAME);
		setHarvestLevel("pickaxe", 2);
		setHardness(4);
	}

	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	public int quantityDropped(Random random) {
		return 1;
	}

	public int quantityDroppedWithBonus(int fortune, Random random) {
		return this.quantityDropped(random);
	}

}
