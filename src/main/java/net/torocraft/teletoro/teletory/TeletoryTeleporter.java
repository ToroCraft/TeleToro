package net.torocraft.teletoro.teletory;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeletoryTeleporter extends Teleporter {

	public static final double TRAVEL_FACTOR = 1;

	/*
	 * fix portal cache support one block off problems remove portal particles
	 * particles portal texture ender block light with flint and steel?
	 *
	 */

	private final WorldServer world;
	/** A private Random() function in Teleporter */
	private final Random random;
	/** Stores successful portal placement locations for rapid lookup. */
	private final LongHashMap destinationCoordinateCache = new LongHashMap();
	/**
	 * A list of valid keys for the destinationCoordainteCache. These are based
	 * on the X & Z of the players initial location.
	 */
	private final List destinationCoordinateKeys = com.google.common.collect.Lists.newArrayList();
	// private static final String __OBFID = "CL_00000153";

	public TeletoryTeleporter(WorldServer worldIn) {
		super(worldIn);
		this.world = worldIn;
		this.random = new Random(worldIn.getSeed());
	}

	public void placeInPortal(Entity entityIn, float rotationYaw) {
		System.out.println("=================== placeInPortal  DIM[" + entityIn.dimension + "]");

		if (!this.placeInExistingPortal(entityIn, rotationYaw)) {
			if (!this.makePortal(entityIn)) {
				System.out.println("lame, tried to make a portal but failed");
			}
			System.out.println("attempting to place player again after creating a portal");
			this.placeInExistingPortal(entityIn, rotationYaw);
		}
	}

	public static class PortalSearchState {

		public double distance;
		public BlockPos portalPos;
		public boolean notCached;

		public double xEntity;
		public double yEntity;
		public double zEntity;

		public int xSearch;
		public int zSearch;

		public long longXZPair;

		public PortalSearchState(Entity entity, WorldServer world) {
			distance = -1.0D;
			portalPos = BlockPos.ORIGIN;
			notCached = true;

			if (world.provider.getDimension() == Teletory.DIMID) {

				longXZPair = ChunkCoordIntPair.chunkXZ2Int(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posZ));

				xEntity = entity.posX / TRAVEL_FACTOR;
				yEntity = entity.posY;
				zEntity = entity.posZ / TRAVEL_FACTOR;

				xSearch = MathHelper.floor_double(xEntity);
				zSearch = MathHelper.floor_double(zEntity);

			} else {

				xEntity = entity.posX * TRAVEL_FACTOR;
				yEntity = entity.posY;
				zEntity = entity.posZ * TRAVEL_FACTOR;

				xSearch = MathHelper.floor_double(xEntity);
				zSearch = MathHelper.floor_double(zEntity);

				longXZPair = ChunkCoordIntPair.chunkXZ2Int(xSearch, zSearch);
			}

			System.out.println("search location x[" + xSearch + "] z[" + zSearch + "]");
		}

	}

	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {

		PortalSearchState search = new PortalSearchState(entity, world);

		System.out.println("search cache for portal with key [" + search.longXZPair + "]");

		if (portalIsCached(search.longXZPair)) {
			readCachedPortal(search, search.longXZPair);
		} else {
			searchForNearbyPortals(search);
		}

		if (noPortalFound(search.distance)) {
			System.out.println("No portal found");
			return false;
		}

		handleFoundPortal(entity, rotationYaw, search);
		return true;
	}

	private void readCachedPortal(PortalSearchState search, long longXZPair) {
		Teleporter.PortalPosition portalposition = (Teleporter.PortalPosition) this.destinationCoordinateCache.getValueByKey(longXZPair);
		search.distance = 0.0D;
		search.portalPos = portalposition;
		portalposition.lastUpdateTime = world.getTotalWorldTime();

		System.out.println("****** found cached portal [" + search.portalPos + "]");

		search.notCached = false;
	}

	private static final int PORTAL_SEARCH_RADIUS = 128; // was 128

	private void searchForNearbyPortals(PortalSearchState search) {
		BlockPos entityPos = new BlockPos(search.xSearch, world.getActualHeight() - 1, search.zSearch);

		System.out.println("commencing search x[" + search.xSearch + "] z[" + search.zSearch + "]");

		for (int x = -PORTAL_SEARCH_RADIUS; x <= PORTAL_SEARCH_RADIUS; ++x) {
			BlockPos blockpos1;

			for (int z = -PORTAL_SEARCH_RADIUS; z <= PORTAL_SEARCH_RADIUS; ++z) {

				for (BlockPos searchPos = entityPos.add(x, 0, z); searchPos.getY() >= 0; searchPos = blockpos1) {
					blockpos1 = searchPos.down();

					if (isPortal(searchPos)) {

						while (world.getBlockState(blockpos1 = searchPos.down()).getBlock() == Teletory.portal) {
							searchPos = blockpos1;
						}

						double distanceToFoundPortal = searchPos.distanceSq(entityPos);

						if (noPortalFound(search.distance) || distanceToFoundPortal < search.distance) {
							search.distance = distanceToFoundPortal;
							search.portalPos = searchPos;
						}

						System.out.println("found portal [" + searchPos + "]");

					}
				}
			}
		}
	}

	private boolean noPortalFound(double distance) {
		return distance < 0.0D;
	}

	private void handleFoundPortal(Entity entity, float rotationYaw, PortalSearchState search) {
		cachePortalLocation(search);

		double x = (double) (search.portalPos).getX() + 0.5D;
		double y = (double) (search.portalPos).getY() + 0.5D;
		double z = (double) (search.portalPos).getZ() + 0.5D;

		System.out.println("placing player at x[" + x + "] y[" + y + "] z[" + z + "]");

		entity.motionX = entity.motionY = entity.motionZ = 0.0D;

		entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);

		if (entity instanceof EntityPlayerMP) {
			System.out.println("EntityPlayerMP");
			((EntityPlayerMP) entity).playerNetServerHandler.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
		} else {
			System.out.println("Not EntityPlayerMP");
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
		}

	}

	private void handleFoundPortal_OriginalWithBug(Entity entity, float rotationYaw, PortalSearchState search) {
		cachePortalLocation(search);

		double x = (double) (search.portalPos).getX() + 0.5D;
		double y = (double) (search.portalPos).getY() + 0.5D;
		double z = (double) (search.portalPos).getZ() + 0.5D;

		EnumFacing portalDirection = determinePortalDirection(search);
		EnumFacing entityDirection = entity.getTeleportDirection();

		if (portalDirection != null) {

			BlockPos inFrontOfPortal = (search.portalPos).offset(portalDirection);
			boolean fontIsBlocked = isBlocked(inFrontOfPortal);

			EnumFacing rotatedPortalDirection = portalDirection.rotateYCCW();
			boolean frontLeftIsBlocked = isBlocked(inFrontOfPortal.offset(rotatedPortalDirection));

			if (frontLeftIsBlocked && fontIsBlocked) {
				search.portalPos = (search.portalPos).offset(rotatedPortalDirection);
				portalDirection = portalDirection.getOpposite();
				rotatedPortalDirection = rotatedPortalDirection.getOpposite();
				BlockPos blockpos3 = (search.portalPos).offset(portalDirection);
				fontIsBlocked = isBlocked(blockpos3);
				frontLeftIsBlocked = isBlocked(blockpos3.offset(rotatedPortalDirection));
			}

			float f6 = 0.5F;
			float f1 = 0.5F;

			if (!frontLeftIsBlocked && fontIsBlocked) {
				f6 = 1.0F;
			} else if (frontLeftIsBlocked && !fontIsBlocked) {
				f6 = 0.0F;
			} else if (frontLeftIsBlocked) {
				f1 = 0.0F;
			}

			x = (double) (search.portalPos).getX() + 0.5D;
			y = (double) (search.portalPos).getY() + 0.5D;
			z = (double) (search.portalPos).getZ() + 0.5D;

			x += (double) ((float) rotatedPortalDirection.getFrontOffsetX() * f6 + (float) portalDirection.getFrontOffsetX() * f1);
			z += (double) ((float) rotatedPortalDirection.getFrontOffsetZ() * f6 + (float) portalDirection.getFrontOffsetZ() * f1);

			float f2 = 0.0F;
			float f3 = 0.0F;
			float f4 = 0.0F;
			float f5 = 0.0F;

			if (entityDirection != null && portalDirection == entityDirection) {
				f2 = 1.0F;
				f3 = 1.0F;
			} else if (entityDirection != null && portalDirection == entityDirection.getOpposite()) {
				f2 = -1.0F;
				f3 = -1.0F;
			} else if (entityDirection != null && portalDirection == entityDirection.rotateY()) {
				f4 = 1.0F;
				f5 = -1.0F;
			} else {
				f4 = -1.0F;
				f5 = 1.0F;
			}

			double motionX = entity.motionX;
			double motionZ = entity.motionZ;

			entity.motionX = motionX * (double) f2 + motionZ * (double) f5;
			entity.motionZ = motionX * (double) f4 + motionZ * (double) f3;

			if (entityDirection != null) {
				entity.rotationYaw = rotationYaw - (float) (entityDirection.getHorizontalIndex() * 90) + (float) (portalDirection.getHorizontalIndex() * 90);
			}

		} else {
			entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		}

		entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
	}

	private EnumFacing determinePortalDirection(PortalSearchState search) {
		EnumFacing portalDirection = null;

		if (this.world.getBlockState((search.portalPos).west()).getBlock() == Teletory.portal) {
			portalDirection = EnumFacing.NORTH;
		}

		if (this.world.getBlockState((search.portalPos).east()).getBlock() == Teletory.portal) {
			portalDirection = EnumFacing.SOUTH;
		}

		if (this.world.getBlockState((search.portalPos).north()).getBlock() == Teletory.portal) {
			portalDirection = EnumFacing.EAST;
		}

		if (this.world.getBlockState((search.portalPos).south()).getBlock() == Teletory.portal) {
			portalDirection = EnumFacing.WEST;
		}
		return portalDirection;
	}

	private void cachePortalLocation(PortalSearchState search) {
		if (search.notCached) {
			this.destinationCoordinateCache.add(search.longXZPair, new Teleporter.PortalPosition(search.portalPos, this.world.getTotalWorldTime()));
			this.destinationCoordinateKeys.add(Long.valueOf(search.longXZPair));
		}
	}

	private boolean isPortal(BlockPos blockpos) {
		return this.world.getBlockState(blockpos).getBlock() == Teletory.portal;
	}

	private boolean portalIsCached(long longIJPair) {
		return this.destinationCoordinateCache.containsItem(longIJPair);
	}

	private boolean isBlocked(BlockPos pos) {
		return !world.isAirBlock(pos) || !world.isAirBlock(pos.up());
	}

	public boolean makePortal(Entity e) {

		PortalSearchState search = new PortalSearchState(e, world);

		byte b0 = 16;
		double d0 = -1.0D;

		int i = search.xSearch;
		int j = MathHelper.floor_double(e.posY);
		int k = search.zSearch;

		System.out.println("=================== makePortal   DIM[" + world.provider.getDimension() + "]  make portal x[" + i + "] z[" + k + "]   xd[" + search.xEntity + "] zd[" + search.zEntity + "] ");

		int l = i;
		int i1 = j;
		int j1 = k;
		int k1 = 0;
		int l1 = this.random.nextInt(4);
		int i2;
		double d1;
		int zPos;
		double d2;
		int i3;
		int j3;
		int k3;
		int l3;
		int i4;
		int j4;
		int k4;
		int l4;
		int i5;
		double d3;
		double d4;

		for (i2 = i - b0; i2 <= i + b0; ++i2) {
			d1 = (double) i2 + 0.5D - search.xEntity;

			for (zPos = k - b0; zPos <= k + b0; ++zPos) {
				d2 = (double) zPos + 0.5D - search.zEntity;
				label271:

				for (i3 = this.world.getActualHeight() - 1; i3 >= 0; --i3) {
					if (this.world.isAirBlock(new BlockPos(i2, i3, zPos))) {
						while (i3 > 0 && this.world.isAirBlock(new BlockPos(i2, i3 - 1, zPos))) {
							--i3;
						}

						for (j3 = l1; j3 < l1 + 4; ++j3) {
							k3 = j3 % 2;
							l3 = 1 - k3;

							if (j3 % 4 >= 2) {
								k3 = -k3;
								l3 = -l3;
							}

							for (i4 = 0; i4 < 3; ++i4) {
								for (j4 = 0; j4 < 4; ++j4) {
									for (k4 = -1; k4 < 4; ++k4) {
										l4 = i2 + (j4 - 1) * k3 + i4 * l3;
										i5 = i3 + k4;
										int j5 = zPos + (j4 - 1) * l3 - i4 * k3;
										Block tmp = this.world.getBlockState(new BlockPos(l4, i5, j5)).getBlock();
										if (k4 < 0 && !tmp.getMaterial(tmp.getDefaultState()).isSolid() || k4 >= 0 && !this.world.isAirBlock(new BlockPos(l4, i5, j5))) {
											continue label271;
										}
									}
								}
							}

							d3 = (double) i3 + 0.5D - search.yEntity;
							d4 = d1 * d1 + d3 * d3 + d2 * d2;

							if (noPortalFound(d0) || d4 < d0) {
								d0 = d4;
								l = i2;
								i1 = i3;
								j1 = zPos;
								k1 = j3 % 4;
							}
						}
					}
				}
			}
		}

		if (noPortalFound(d0)) {
			for (i2 = i - b0; i2 <= i + b0; ++i2) {
				d1 = (double) i2 + 0.5D - search.xEntity;

				for (zPos = k - b0; zPos <= k + b0; ++zPos) {
					d2 = (double) zPos + 0.5D - search.zEntity;
					label219:

					for (i3 = this.world.getActualHeight() - 1; i3 >= 0; --i3) {
						if (this.world.isAirBlock(new BlockPos(i2, i3, zPos))) {
							while (i3 > 0 && this.world.isAirBlock(new BlockPos(i2, i3 - 1, zPos))) {
								--i3;
							}

							for (j3 = l1; j3 < l1 + 2; ++j3) {
								k3 = j3 % 2;
								l3 = 1 - k3;

								for (i4 = 0; i4 < 4; ++i4) {
									for (j4 = -1; j4 < 4; ++j4) {
										k4 = i2 + (i4 - 1) * k3;
										l4 = i3 + j4;
										i5 = zPos + (i4 - 1) * l3;
										Block tmpb = this.world.getBlockState(new BlockPos(k4, l4, i5)).getBlock();
										if (j4 < 0 && !tmpb.getMaterial(tmpb.getDefaultState()).isSolid() || j4 >= 0 && !this.world.isAirBlock(new BlockPos(k4, l4, i5))) {
											continue label219;
										}
									}
								}

								d3 = (double) i3 + 0.5D - search.yEntity;
								d4 = d1 * d1 + d3 * d3 + d2 * d2;

								if (noPortalFound(d0) || d4 < d0) {
									d0 = d4;
									l = i2;
									i1 = i3;
									j1 = zPos;
									k1 = j3 % 2;
								}
							}
						}
					}
				}
			}
		}

		int xPos = l;
		int yPos = i1;
		zPos = j1;
		int l5 = k1 % 2;
		int l2 = 1 - l5;

		if (k1 % 4 >= 2) {
			l5 = -l5;
			l2 = -l2;
		}

		if (noPortalFound(d0)) {
			i1 = MathHelper.clamp_int(i1, 70, this.world.getActualHeight() - 10);
			yPos = i1;

			for (i3 = -1; i3 <= 1; ++i3) {
				for (j3 = 1; j3 < 3; ++j3) {
					for (k3 = -1; k3 < 3; ++k3) {
						l3 = xPos + (j3 - 1) * l5 + i3 * l2;
						i4 = yPos + k3;
						j4 = zPos + (j3 - 1) * l2 - i3 * l5;
						boolean flag = k3 < 0;
						this.world.setBlockState(new BlockPos(l3, i4, j4), flag ? Blocks.end_bricks.getDefaultState() : Blocks.air.getDefaultState());
					}
				}
			}
		}

		placePortalBlocks(zPos, xPos, yPos, l5, l2);

		return true;
	}

	private void placePortalBlocks(int zIn, int xIn, int yIn, int l5, int l2) {
		int j3;
		int k3;
		int l3;
		int x;
		int y;
		int z;

		System.out.println("x[" + xIn + "] y[" + yIn + "] z[" + zIn + "]  l5[" + l5 + "] l2[" + l2 + "]");

		IBlockState iblockstate = Teletory.portal.getDefaultState().withProperty(BlockPortal.AXIS, l5 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);

		for (j3 = 0; j3 < 4; ++j3) {
			for (k3 = 0; k3 < 4; ++k3) {
				for (l3 = -1; l3 < 4; ++l3) {
					x = xIn + (k3 - 1) * l5;
					y = yIn + l3;
					z = zIn + (k3 - 1) * l2;
					boolean flag1 = k3 == 0 || k3 == 3 || l3 == -1 || l3 == 3;
					this.world.setBlockState(new BlockPos(x, y, z), flag1 ? Blocks.end_bricks.getDefaultState() : iblockstate, 2);
				}
			}

			for (k3 = 0; k3 < 4; ++k3) {
				for (l3 = -1; l3 < 4; ++l3) {
					x = xIn + (k3 - 1) * l5;
					y = yIn + l3;
					z = zIn + (k3 - 1) * l2;
					this.world.notifyNeighborsOfStateChange(new BlockPos(x, y, z), this.world.getBlockState(new BlockPos(x, y, z)).getBlock());
				}
			}
		}
	}

	/**
	 * called periodically to remove out-of-date portal locations from the cache
	 * list. Argument par1 is a WorldServer.getTotalWorldTime() value.
	 */
	public void removeStalePortalLocations(long p_85189_1_) {
		if (p_85189_1_ % 100L == 0L) {
			Iterator iterator = this.destinationCoordinateKeys.iterator();
			long j = p_85189_1_ - 600L;

			while (iterator.hasNext()) {
				Long olong = (Long) iterator.next();
				Teleporter.PortalPosition portalposition = (Teleporter.PortalPosition) this.destinationCoordinateCache.getValueByKey(olong.longValue());

				if (portalposition == null || portalposition.lastUpdateTime < j) {
					iterator.remove();
					this.destinationCoordinateCache.remove(olong.longValue());
				}
			}
		}
	}

	public class PortalPosition extends BlockPos {
		/** The worldtime at which this PortalPosition was last verified */
		public long lastUpdateTime;
		private static final String __OBFID = "CL_00000154";

		public PortalPosition(BlockPos pos, long p_i45747_3_) {
			super(pos.getX(), pos.getY(), pos.getZ());
			this.lastUpdateTime = p_i45747_3_;
		}
	}

}