package net.torocraft.teletoro;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.blocks.BlockEnder;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
		BlockTeletoryPortal.init();
		BlockEnder.init();
		setupTheTeletory(e);
		GameRegistry.addRecipe(new ItemStack(BlockEnder.INSTANCE), "##", "##", '#', Items.ENDER_PEARL);
	}

	protected void setupTheTeletory(FMLInitializationEvent e) {
		Teletory teletory = new Teletory();
		MinecraftForge.EVENT_BUS.register(teletory);
		Teletory.init(e);
	}

    public void postInit(FMLPostInitializationEvent e) {

    }
}
