package net.torocraft.teletoro.teleporter;

import java.util.Random;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.torocraft.teletoro.Teletory;
import net.torocraft.teletoro.blocks.BlockEnder;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;

public class TeletoryTeleporter extends Teleporter {

	public static final int TRAVEL_FACTOR = 64;
	private static final int PORTAL_SEARCH_RADIUS = 200;

	private final WorldServer world;
	private final Random random;
	private final Long2ObjectMap<Teleporter.PortalPosition> destinationCoordinateCache = new Long2ObjectOpenHashMap(4096);

	public TeletoryTeleporter(WorldServer worldIn) {
		super(worldIn);
		this.world = worldIn;
		this.random = new Random(worldIn.getSeed());
	}

	public void placeInPortal(Entity entityIn, float rotationYaw) {
		if (!placeInExistingPortal(entityIn, rotationYaw)) {
			makePortal(entityIn);
			placeInExistingPortal(entityIn, rotationYaw);
		}
	}

	public boolean placeInExistingPortal(Entity entity, float rotationYaw) {
		PortalSearchState state = new PortalSearchState(entity, world);

		if (portalIsCached(state.longXZPair)) {
			readCachedPortal(state);
		} else {
			searchForNearbyPortals(state);
		}

		if (noPortalFound(state.distance)) {
			return false;
		}

		handleFoundPortal(entity, rotationYaw, state);
		return true;
	}

	private void readCachedPortal(PortalSearchState search) {
		Teleporter.PortalPosition portalposition = destinationCoordinateCache.get(search.longXZPair);
		search.distance = 0.0D;
		search.portalPos = portalposition;
		portalposition.lastUpdateTime = world.getTotalWorldTime();
		search.notCached = false;
	}

	private void searchForNearbyPortals(PortalSearchState search) {
		BlockPos entityPos = new BlockPos(search.xSearch, world.getActualHeight() - 1, search.zSearch);
		BlockPos nextSearch;
		int searchRadius = PORTAL_SEARCH_RADIUS;

		if (world.provider.getDimension() == Teletory.DIMID) {
			searchRadius = 4;
		}

		for (int x = -searchRadius; x <= searchRadius; ++x) {
			for (int z = -searchRadius; z <= searchRadius; ++z) {
				for (BlockPos searchPos = entityPos.add(x, 0, z - 2); searchPos.getY() >= 0; searchPos = nextSearch) {
					nextSearch = searchPos.down();
					searchForPortalAtBlock(search, entityPos, nextSearch, searchPos);
				}
			}
		}
	}

	protected void searchForPortalAtBlock(PortalSearchState search, BlockPos entityPos, BlockPos nextSearch, BlockPos searchPos) {
		if (!isPortal(searchPos)) {
			return;
		}

		while (world.getBlockState(nextSearch = searchPos.down()).getBlock() == BlockTeletoryPortal.INSTANCE) {
			searchPos = nextSearch;
		}

		double distanceToFoundPortal = searchPos.distanceSq(entityPos);

		if (noPortalFound(search.distance) || distanceToFoundPortal < search.distance) {
			search.distance = distanceToFoundPortal;
			search.portalPos = searchPos;
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
		entity.motionX = entity.motionY = entity.motionZ = 0.0D;
		// if (!world.isRemote) {
			if (entity instanceof EntityPlayerMP) {
				((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
			}
			
			entity.setLocationAndAngles(x, y, z, entity.rotationYaw, entity.rotationPitch);
			entity.setPositionAndUpdate(x, y, z);
		// }
	}

	private EnumFacing determinePortalDirection(PortalSearchState search) {
		EnumFacing portalDirection = null;

		if (world.getBlockState((search.portalPos).west()).getBlock() == BlockTeletoryPortal.INSTANCE) {
			portalDirection = EnumFacing.NORTH;
		}

		if (world.getBlockState((search.portalPos).east()).getBlock() == BlockTeletoryPortal.INSTANCE) {
			portalDirection = EnumFacing.SOUTH;
		}

		if (world.getBlockState((search.portalPos).north()).getBlock() == BlockTeletoryPortal.INSTANCE) {
			portalDirection = EnumFacing.EAST;
		}

		if (world.getBlockState((search.portalPos).south()).getBlock() == BlockTeletoryPortal.INSTANCE) {
			portalDirection = EnumFacing.WEST;
		}
		return portalDirection;
	}

	private void cachePortalLocation(PortalSearchState search) {
		if (search.notCached) {
			destinationCoordinateCache.put(search.longXZPair, new Teleporter.PortalPosition(search.portalPos, world.getTotalWorldTime()));
		}
	}

	private boolean isPortal(BlockPos blockpos) {
		return this.world.getBlockState(blockpos).getBlock() == BlockTeletoryPortal.INSTANCE;
	}

	private boolean portalIsCached(long longIJPair) {
		return this.destinationCoordinateCache.containsKey(Long.valueOf(longIJPair));
	}

	private boolean isBlocked(BlockPos pos) {
		return !world.isAirBlock(pos) || !world.isAirBlock(pos.up());
	}

	@Override
	public boolean makePortal(Entity e) {
		if (e.dimension == Teletory.DIMID) {
			return makePortalOnPlatform(e);
		}
		return makePortalOnExistingGround(e);
	}

	public boolean makePortalOnPlatform(Entity e) {
		PortalSearchState search = new PortalSearchState(e, world);
		int x = search.xSearch;
		int y = 13;
		int z = search.zSearch;
		buildPortalFloor(x, y, z);
		placePortalBlocks(z, x, y, 1, 0);
		return true;
	}

	public boolean makePortalOnExistingGround(Entity e) {
		PortalSearchState search = new PortalSearchState(e, world);

		byte b0 = 16;
		double d0 = -1.0D;

		int i = search.xSearch;
		int j = MathHelper.floor(e.posY);
		int k = search.zSearch;

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
			i1 = MathHelper.clamp(i1, 70, this.world.getActualHeight() - 10);
			yPos = i1;

			for (i3 = -1; i3 <= 1; ++i3) {
				for (j3 = 1; j3 < 3; ++j3) {
					for (k3 = -1; k3 < 3; ++k3) {
						l3 = xPos + (j3 - 1) * l5 + i3 * l2;
						i4 = yPos + k3;
						j4 = zPos + (j3 - 1) * l2 - i3 * l5;
						boolean flag = k3 < 0;
						this.world.setBlockState(new BlockPos(l3, i4, j4), flag ? BlockEnder.INSTANCE.getDefaultState() : Blocks.AIR.getDefaultState());
					}
				}
			}
		}

		placePortalBlocks(zPos, xPos, yPos, l5, l2);

		return true;
	}

	private void buildPortalFloor(int x, int y, int z) {

		int y1 = y - 1;
		BlockPos pos;

		for (int x1 = -1; x1 < 1; x1++) {
			for (int z1 = -1; z1 < 2; z1++) {
				pos = new BlockPos(x1 + x + 1, y1, z1 + z);

				if (!world.isSideSolid(pos, EnumFacing.UP)) {
					world.setBlockState(pos, Blocks.END_STONE.getDefaultState());
				}
			}
		}
	}

	private void placePortalBlocks(int zIn, int xIn, int yIn, int l5, int l2) {
		int j3;
		int k3;
		int l3;
		int x;
		int y;
		int z;

		IBlockState iblockstate = BlockTeletoryPortal.INSTANCE.getDefaultState().withProperty(BlockPortal.AXIS, l5 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);

		for (j3 = 0; j3 < 4; ++j3) {
			for (k3 = 0; k3 < 4; ++k3) {
				for (l3 = -1; l3 < 4; ++l3) {
					x = xIn + (k3 - 1) * l5;
					y = yIn + l3;
					z = zIn + (k3 - 1) * l2;
					boolean flag1 = k3 == 0 || k3 == 3 || l3 == -1 || l3 == 3;
					this.world.setBlockState(new BlockPos(x, y, z), flag1 ? BlockEnder.INSTANCE.getDefaultState() : iblockstate, 2);
				}
			}

			for (k3 = 0; k3 < 4; ++k3) {
				for (l3 = -1; l3 < 4; ++l3) {
					x = xIn + (k3 - 1) * l5;
					y = yIn + l3;
					z = zIn + (k3 - 1) * l2;
					this.world.notifyNeighborsOfStateChange(new BlockPos(x, y, z), this.world.getBlockState(new BlockPos(x, y, z)).getBlock(), false);
				}
			}
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

				longXZPair = ChunkPos.asLong(MathHelper.floor(entity.posX), MathHelper.floor(entity.posZ));

				xEntity = entity.posX / TRAVEL_FACTOR;
				yEntity = entity.posY;
				zEntity = entity.posZ / TRAVEL_FACTOR;

				xSearch = MathHelper.floor(xEntity);
				zSearch = MathHelper.floor(zEntity);

			} else {

				xEntity = entity.posX * TRAVEL_FACTOR;
				yEntity = entity.posY;
				zEntity = entity.posZ * TRAVEL_FACTOR;

				xSearch = MathHelper.floor(xEntity);
				zSearch = MathHelper.floor(zEntity);

				longXZPair = ChunkPos.asLong(xSearch, zSearch);
			}

		}

	}

}