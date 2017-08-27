package net.torocraft.teletoro;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.blocks.BlockEnder;
import net.torocraft.teletoro.blocks.BlockEnderOre;
import net.torocraft.teletoro.blocks.BlockLinkedTeletoryPortal;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.blocks.TileEntityLinkedTeletoryPortal;
import net.torocraft.teletoro.item.EntityTeletoryPearl;
import net.torocraft.teletoro.item.ItemTeletoryPearl;
import net.torocraft.teletoro.item.ItemTeletoryPortalLinker;
import net.torocraft.teletoro.item.armor.ItemEnderArmor;
import net.torocraft.teletoro.material.ArmorMaterials;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent e) {
		ArmorMaterials.init();
		TileEntityLinkedTeletoryPortal.init();
		EntityTeletoryPearl.init(150);
	}

	public void init(FMLInitializationEvent e) {
		setupTheTeletory(e);
		GameRegistry.addSmelting(BlockEnderOre.ITEM_INSTANCE, new ItemStack(ItemTeletoryPearl.INSTANCE), 1);
		GameRegistry.addSmelting(new ItemStack(ItemTeletoryPearl.INSTANCE), new ItemStack(Items.ENDER_PEARL), 1);
	}

	protected void setupTheTeletory(FMLInitializationEvent e) {
		Teletory teletory = new Teletory();
		MinecraftForge.EVENT_BUS.register(teletory);
		Teletory.init(e);
	}

	public void postInit(FMLPostInitializationEvent e) {

	}
}
