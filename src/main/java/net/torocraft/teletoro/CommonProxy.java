package net.torocraft.teletoro;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.blocks.BlockEnder;
import net.torocraft.teletoro.teletory.BlockTeletoryPortal;
import net.torocraft.teletoro.teletory.Teletory;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {

		BlockTeletoryPortal.init();
		BlockEnder.init();

		Teletory teletory = new Teletory();
		MinecraftForge.EVENT_BUS.register(teletory);
		Teletory.init(e);

		GameRegistry.addRecipe(new ItemStack(BlockEnder.INSTANCE), "##", "##", '#', Items.ENDER_PEARL);


    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
