package net.torocraft.teletoro;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.teletoro.teletory.Teletory;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
		Teletory teletory = new Teletory();
		teletory.init(e);
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
