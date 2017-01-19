package net.torocraft.teletoro;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.teletoro.blocks.BlockEnder;
import net.torocraft.teletoro.blocks.BlockEnderOre;
import net.torocraft.teletoro.blocks.BlockLinkedTeletoryPortal;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.item.ItemTeletoryPearl;
import net.torocraft.teletoro.item.ItemTeletoryPortalLinker;
import net.torocraft.teletoro.item.armor.ItemEnderArmor;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
		ItemEnderArmor.registerRenders();
		BlockTeletoryPortal.registerRenders();
		BlockEnder.registerRenders();
		BlockEnderOre.registerRenders();
		BlockLinkedTeletoryPortal.registerRenders();
		ItemTeletoryPearl.registerRenders();
		ItemTeletoryPortalLinker.registerRenders();
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

}