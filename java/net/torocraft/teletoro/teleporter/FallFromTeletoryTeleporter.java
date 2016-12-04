package net.torocraft.teletoro.teleporter;

import java.util.Random;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.torocraft.teletoro.Teletory;

public class FallFromTeletoryTeleporter extends Teleporter {

	protected final WorldServer world;
	protected final Random random;

	public FallFromTeletoryTeleporter(WorldServer worldIn) {
		super(worldIn);
		this.world = worldIn;
		this.random = new Random(worldIn.getSeed());
	}

	public void placeInPortal(Entity entityIn, float rotationYaw) {
		this.placeInExistingPortal(entityIn, rotationYaw);
	}

	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
		PortalSearchState search = new PortalSearchState(entity, world);
		if (entity.dimension == Teletory.DIMID) {
			return fallIntoTeletory(entity, rotationYaw, search);
		} else {
			return fallIntoOverWorld(entity, rotationYaw, search);
		}
	}

	protected boolean fallIntoTeletory(Entity entity, float rotationYaw, PortalSearchState search) {

		double x = MathHelper.floor_double(search.xEntity);
		double y = 25;
		double z = MathHelper.floor_double(search.zEntity);

		entity.fallDistance = 0;
		entity.velocityChanged = true;

		entity.setLocationAndAngles(x, y, z, rotationYaw, -5);
		return true;
	}

	protected boolean fallIntoOverWorld(Entity entity, float rotationYaw, PortalSearchState search) {
		BlockPos placement = findTopOfWorld(search);

		double x = placement.getX() + 0.5D;
		double y = placement.getY() + 14 + random.nextInt(4);
		double z = placement.getZ() + 0.5D;

		double speed = 10;

		double xVel = speed * Math.sin(rotationYaw);
		double zVel = speed * Math.cos(rotationYaw);

		entity.motionX = xVel;
		entity.motionZ = zVel;
		entity.motionY = 0;
		entity.fallDistance = 0;
		entity.velocityChanged = true;

		entity.setLocationAndAngles(x, y, z, rotationYaw, -5);
		return true;
	}

	protected BlockPos findTopOfWorld(PortalSearchState search) {
		int xSearch = MathHelper.floor_double(search.xEntity);
		int zSearch = MathHelper.floor_double(search.zEntity);

		BlockPos searchPos = new BlockPos(xSearch, world.getActualHeight() - 1, zSearch);
		while (searchPos.getY() >= 0) {
			if (!world.isAirBlock(searchPos)) {
				return searchPos;
			}
			searchPos = searchPos.down();
		}
		return searchPos;
	}

	public boolean makePortal(Entity e) {
		return true;
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

}