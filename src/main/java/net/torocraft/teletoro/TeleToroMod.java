package net.torocraft.teletoro;

import net.minecraft.stats.Achievement;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.teletoro.blocks.BlockEnder;

@Mod(modid = TeleToroMod.MODID, name = TeleToroMod.MODNAME, version = TeleToroMod.VERSION)
public class TeleToroMod {

	public static final String MODID = "teletoro";
	public static final String VERSION = "1.9.4-3";
	public static final String MODNAME = "TeleToro";

	@SidedProxy(clientSide = "net.torocraft.teletoro.ClientProxy", serverSide = "net.torocraft.teletoro.ServerProxy")
	public static CommonProxy proxy;

	@Instance(value = TeleToroMod.MODID)
	public static TeleToroMod instance;

	public static Achievement TELETORY_ACHIEVEMNT;

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
		TELETORY_ACHIEVEMNT = new Achievement("teletory", "teletory_achievement", 0, 0, BlockEnder.INSTANCE, null);
	}

}
