package net.torocraft.teletoro;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod (modid = TeleToroMod.MODID, name = TeleToroMod.MODNAME, version = TeleToroMod.VERSION)
public class TeleToroMod {

	
	public static final String MODID = "teletoro";
	public static final String VERSION = "1.0";
	public static final String MODNAME = "TeleToro";
	
	@SidedProxy(clientSide = "net.torocraft.teletoro.ClientProxy", serverSide = "net.torocraft.teletoro.ServerProxy")
	public static CommonProxy proxy;
	
	@Instance(value = TeleToroMod.MODID)
	public static TeleToroMod instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
	    proxy.preInit(e);
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
	    proxy.init(e);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	    proxy.postInit(e);
	}
	
	
	
	
}
