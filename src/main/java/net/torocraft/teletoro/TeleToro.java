package net.torocraft.teletoro;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.teletoro.blocks.BlockEnder;

@Mod(modid = TeleToro.MODID, name = TeleToro.MODNAME, version = TeleToro.VERSION)
public class TeleToro {

	public static final String MODID = "teletoro";
	public static final String VERSION = "1.12.2-26";
	public static final String MODNAME = "TeleToro";

	@SidedProxy(clientSide = "net.torocraft.teletoro.ClientProxy", serverSide = "net.torocraft.teletoro.ServerProxy")
	public static CommonProxy proxy;

	@Instance(value = TeleToro.MODID)
	public static TeleToro instance;

	//TODO
	//public static Achievement TELETORY_ACHIEVEMNT;

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
		//TODO
		//TELETORY_ACHIEVEMNT = new Achievement("teletory", "teletory_achievement", 0, 0, BlockEnder.INSTANCE, null).registerStat();
	}

}
