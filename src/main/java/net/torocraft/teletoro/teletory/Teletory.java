package net.torocraft.teletoro.teletory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class Teletory {

	public static int DIMID = 16;
	public static DimensionType type = DimensionType.register("teletory", "_teletory", DIMID, TeletoryWorldProvider.class, false);

	public static BlockTeletoryPortal portal;
	public static ItemTeletoryTrigger trigger;

	static {

		portal = (BlockTeletoryPortal) (new BlockTeletoryPortal().setUnlocalizedName("teletory_portal"));
		trigger = (ItemTeletoryTrigger) (new ItemTeletoryTrigger().setUnlocalizedName("teletory_trigger"));
		
		
		// Item.itemRegistry.addObject(432, "teletory_trigger", block);
	}



	public void init(FMLInitializationEvent event) {

		GameRegistry.registerBlock(portal, "teletory_portal");
		GameRegistry.registerItem(trigger, "teletory_trigger");

		DimensionManager.registerDimension(DIMID, type);

		if (event.getSide() == Side.CLIENT) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(trigger, 0, new ModelResourceLocation("Teletory:teletory_trigger", "inventory"));
		}

	}

	

	


}
