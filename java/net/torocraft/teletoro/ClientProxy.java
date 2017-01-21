package net.torocraft.teletoro;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
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
		registerRendersForLinker();
	}

	public static void registerRendersForLinker() {
		ModelLoader.setCustomMeshDefinition(ItemTeletoryPortalLinker.INSTANCE, new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				if (ItemTeletoryPortalLinker.isActive(stack)) {
					return ItemTeletoryPortalLinker.modelOn;
				} else {
					return ItemTeletoryPortalLinker.model;
				}
			}
		});
		ModelLoader.registerItemVariants(ItemTeletoryPortalLinker.INSTANCE,
				new ModelResourceLocation[] { ItemTeletoryPortalLinker.model, ItemTeletoryPortalLinker.modelOn });
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

	}

	@Override
	public void postInit(FMLPostInitializationEvent e) {
		super.postInit(e);
	}

}