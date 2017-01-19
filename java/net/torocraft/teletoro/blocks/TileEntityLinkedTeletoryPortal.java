package net.torocraft.teletoro.blocks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLinkedTeletoryPortal extends TileEntity {

	public static final String NAME = "linked_teletory_portal_tile_entity";

	private Integer dimId;
	private BlockPos destination;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityLinkedTeletoryPortal.class, NAME);
	}

	public int getDimId() {
		return dimId;
	}

	public void setDimId(int dimId) {
		this.dimId = dimId;
	}

	public BlockPos getDestination() {
		return destination;
	}

	public void setDestination(BlockPos destination) {
		this.destination = destination;
	}

}
