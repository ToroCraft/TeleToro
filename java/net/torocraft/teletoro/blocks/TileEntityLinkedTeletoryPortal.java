package net.torocraft.teletoro.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityLinkedTeletoryPortal extends TileEntity {

	public static final String NAME = "linked_teletory_portal_tile_entity";

	private Integer dimId;
	private BlockPos destination;
	private Integer side;

	public static void init() {
		GameRegistry.registerTileEntity(TileEntityLinkedTeletoryPortal.class, NAME);
	}

	public int getDimId() {
		if(dimId == null){
			return 0;
		}
		return dimId;
	}

	public void setDimId(Integer dimId) {
		this.dimId = dimId;
	}

	public BlockPos getDestination() {
		return destination;
	}

	public void setDestination(BlockPos destination) {
		this.destination = destination;
	}

	public Integer getSide() {
		return side;
	}

	public void setSide(Integer side) {
		this.side = side;
	}

	@Override
	public void readFromNBT(NBTTagCompound c) {
		super.readFromNBT(c);
		dimId = c.getInteger("dimid");
		destination = pos(c.getLong("destination"));
		side = c.getInteger("side");
	}

	private BlockPos pos(long l) {
		if(l == 0){
			return null;
		}
		return BlockPos.fromLong(l);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound c = super.writeToNBT(compound);
		c.setInteger("dimid", dimId);
		c.setLong("destination", l(destination));
		c.setInteger("side", side);
		return c;
	}

	private long l(BlockPos pos) {
		if(pos == null){
			return 0;
		}
		return pos.toLong();
	}

}
