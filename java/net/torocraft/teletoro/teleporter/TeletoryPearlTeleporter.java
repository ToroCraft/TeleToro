package net.torocraft.teletoro.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.torocraft.teletoro.world.TeletoryChunkProvider;

public class TeletoryPearlTeleporter extends FallFromTeletoryTeleporter {

	public TeletoryPearlTeleporter(WorldServer worldIn) {
		super(worldIn);
	}

	protected BlockPos addBlockUnderPlayer(BlockPos placement) {
		if (placement.getY() < 5) {
			placement = new BlockPos(placement.getX(), TeletoryChunkProvider.dirtHeight, placement.getZ());
			world.setBlockState(placement, Blocks.END_STONE.getDefaultState());
			placement.up();
		}
		return placement;
	}

	protected boolean fallIntoOverWorld(Entity entity, float rotationYaw, PortalSearchState search) {
		BlockPos placement = findTopOfWorld(search);
		placement = addBlockUnderPlayer(placement);
		standardPlacementValues(entity, rotationYaw, placement);
		return true;
	}

	protected void standardPlacementValues(Entity entity, float rotationYaw, BlockPos placement) {
		double x = placement.getX() + 0.5D;
		double y = placement.getY() + 2;
		double z = placement.getZ() + 0.5D;

		entity.motionX = 0;
		entity.motionZ = 0;
		entity.motionY = 0;
		entity.fallDistance = 0;
		entity.velocityChanged = true;

		entity.setLocationAndAngles(x, y, z, rotationYaw, -5);
	}
}