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
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.item.ItemTeletoryPearl;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
		BlockTeletoryPortal.init();
		BlockEnder.init();
		BlockEnderOre.init();
		ItemTeletoryPearl.init();
		setupTheTeletory(e);
		GameRegistry.addRecipe(new ItemStack(BlockEnder.INSTANCE), "##", "##", '#', Items.ENDER_PEARL);
		GameRegistry.addSmelting(BlockEnderOre.ITEM_INSTANCE, new ItemStack(Items.ENDER_PEARL), 1);
	}

	protected void setupTheTeletory(FMLInitializationEvent e) {
		Teletory teletory = new Teletory();
		MinecraftForge.EVENT_BUS.register(teletory);
		Teletory.init(e);
	}

    public void postInit(FMLPostInitializationEvent e) {

    }
}
