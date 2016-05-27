package net.torocraft.teletoro.teletory;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class FallFromTeletoryTeleporter extends Teleporter {

	private static final int FALL_DISTANCE = 12;

	private final WorldServer world;
	private final Random random;

	public FallFromTeletoryTeleporter(WorldServer worldIn) {
		super(worldIn);
		this.world = worldIn;
		this.random = new Random(worldIn.getSeed());
	}

	public void placeInPortal(Entity entityIn, float rotationYaw) {
		System.out.println("=================== placeInPortal  DIM[" + entityIn.dimension + "] WORLD_DIM[" + world.provider.getDimension() + "]");
		this.placeInExistingPortal(entityIn, rotationYaw);
	}


	public static class PortalSearchState {

		public double xEntity;
		public double yEntity;
		public double zEntity;

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();

			s.append("xEntity[").append(xEntity).append("] ");
			s.append("yEntity[").append(yEntity).append("] ");
			s.append("zEntity[").append(zEntity).append("] ");

			return s.toString();
		}

		public PortalSearchState(Entity entity, WorldServer world) {

			if (entity.dimension == Teletory.DIMID) {
				xEntity = entity.posX / TeletoryTeleporter.TRAVEL_FACTOR;
				yEntity = entity.posY;
				zEntity = entity.posZ / TeletoryTeleporter.TRAVEL_FACTOR;

			} else {
				xEntity = entity.posX * TeletoryTeleporter.TRAVEL_FACTOR;
				yEntity = entity.posY;
				zEntity = entity.posZ * TeletoryTeleporter.TRAVEL_FACTOR;

			}

		}

	}

	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {

		PortalSearchState search = new PortalSearchState(entity, world);

		System.out.println("searching for insert location: " + search.toString());

		BlockPos placement = findTopOfWorld(search);

		System.out.println("found insert location of  " + placement.toString());

		double x = placement.getX() + 0.5D;
		double y = placement.getY() + 0.5D;
		double z = placement.getZ() + 0.5D;

		System.out.println("placing player at x[" + x + "] y[" + y + "] z[" + z + "]");

		entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		entity.motionX = 4;

		entity.setPositionAndUpdate(x, y, z);

		/*
		 * if (entity instanceof EntityPlayerMP) {
		 * 
		 * 
		 * 
		 * 
		 * entity.setLocationAndAngles(x, y, z, yaw, pitch);
		 * entitylivingbase.fallDistance = 0.0F;
		 * 
		 * 
		 * ((EntityPlayerMP) entity).playerLocation = new BlockPos(0,0,90);
		 * ((EntityPlayerMP) entity).player
		 * 
		 * ((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(x,
		 * y, z, 90, 0);
		 * 
		 * } else { entity.setLocationAndAngles(x, y, z, 90, 0); }
		 */
		return true;
	}

	private BlockPos findTopOfWorld(PortalSearchState search) {

		int xSearch = MathHelper.floor_double(search.xEntity);
		int zSearch = MathHelper.floor_double(search.zEntity);

		BlockPos searchPos = new BlockPos(xSearch, world.getActualHeight() - 1, zSearch);

		System.out.println("starting search at " + searchPos);

		while (searchPos.getY() >= 0) {
			if (!world.isAirBlock(searchPos)) {
				System.out.println("found ground level at y=" + searchPos.getY());
				return searchPos.add(0, FALL_DISTANCE, 0);
			}
			searchPos = searchPos.down();
		}

		System.out.println("never found ground level placing at y=" + (world.getActualHeight() - 1));
		return new BlockPos(xSearch, world.getActualHeight() - 1 + FALL_DISTANCE, zSearch);
	}

	public boolean makePortal(Entity e) {
		return true;
	}

}