package net.torocraft.teletoro.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLinkedTeletoryPortal extends TileEntity {

	public static final String NAME = "linked_teletory_portal_tile_entity";
	
	public static void init() {
		GameRegistry.registerTileEntity(TileEntityLinkedTeletoryPortal.class, NAME);
	}
}
