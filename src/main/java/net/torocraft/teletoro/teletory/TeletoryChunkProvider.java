package net.torocraft.teletoro.teletory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class TeletoryChunkProvider implements IChunkGenerator {
	protected static final IBlockState field_185982_a = Blocks.obsidian.getDefaultState();
	private final Random rand;
	private NoiseGeneratorOctaves field_185991_j;
	private NoiseGeneratorOctaves field_185992_k;
	private NoiseGeneratorOctaves field_185993_l;
	private NoiseGeneratorPerlin field_185994_m;
	public NoiseGeneratorOctaves field_185983_b;
	public NoiseGeneratorOctaves field_185984_c;
	public NoiseGeneratorOctaves field_185985_d;
	private final World worldObj;
	private final boolean mapFeaturesEnabled;
	private final WorldType terrainType;
	private final double[] heightMap;
	private final float[] field_185999_r;
	private ChunkProviderSettings settings;
	private IBlockState oceanBlock = Blocks.water.getDefaultState();
	private double[] field_186002_u = new double[256];
	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private BiomeGenBase[] biomesForGeneration;
	double[] field_185986_e;
	double[] field_185987_f;
	double[] field_185988_g;
	double[] field_185989_h;

	public TeletoryChunkProvider(World worldIn, long seed) {
		{
			caveGenerator = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(caveGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.CAVE);
			ravineGenerator = net.minecraftforge.event.terraingen.TerrainGen.getModdedMapGen(ravineGenerator, net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE);
		}
		this.worldObj = worldIn;
		this.mapFeaturesEnabled = false;
		this.terrainType = worldIn.getWorldInfo().getTerrainType();
		this.rand = new Random(seed);
		this.field_185991_j = new NoiseGeneratorOctaves(this.rand, 16);
		this.field_185992_k = new NoiseGeneratorOctaves(this.rand, 16);
		this.field_185993_l = new NoiseGeneratorOctaves(this.rand, 8);
		this.field_185994_m = new NoiseGeneratorPerlin(this.rand, 4);
		this.field_185983_b = new NoiseGeneratorOctaves(this.rand, 10);
		this.field_185984_c = new NoiseGeneratorOctaves(this.rand, 16);
		this.field_185985_d = new NoiseGeneratorOctaves(this.rand, 8);
		this.heightMap = new double[825];
		this.field_185999_r = new float[25];

		for (int i = -2; i <= 2; ++i) {
			for (int j = -2; j <= 2; ++j) {
				float f = 10.0F / MathHelper.sqrt_float((float) (i * i + j * j) + 0.2F);
				this.field_185999_r[i + 2 + (j + 2) * 5] = f;
			}
		}

		this.settings = ChunkProviderSettings.Factory.jsonToFactory("").func_177864_b();

		net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld ctx = new net.minecraftforge.event.terraingen.InitNoiseGensEvent.ContextOverworld(field_185991_j, field_185992_k, field_185993_l, field_185994_m,
				field_185983_b, field_185984_c, field_185985_d);
		ctx = net.minecraftforge.event.terraingen.TerrainGen.getModdedNoiseGenerators(worldIn, this.rand, ctx);
		this.field_185991_j = ctx.getLPerlin1();
		this.field_185992_k = ctx.getLPerlin2();
		this.field_185993_l = ctx.getPerlin();
		this.field_185994_m = ctx.getHeight();
		this.field_185983_b = ctx.getScale();
		this.field_185984_c = ctx.getDepth();
		this.field_185985_d = ctx.getForest();
	}

	public void setBlocksInChunk(int xIn, int zIn, ChunkPrimer primer) {
		// this.biomesForGeneration =
		// this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration,
		// x * 4 - 2, z * 4 - 2, 10, 10);
		// this.generateHeightmap(x * 4, 0, z * 4);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				primer.setBlockState(x, 50, z, field_185982_a);
			}
		}

		/*
		 * for (int i = 0; i < 4; ++i) { int j = i * 5; int k = (i + 1) * 5;
		 * 
		 * for (int l = 0; l < 4; ++l) { int i1 = (j + l) * 33; int j1 = (j + l
		 * + 1) * 33; int k1 = (k + l) * 33; int l1 = (k + l + 1) * 33;
		 * 
		 * for (int i2 = 0; i2 < 32; ++i2) { double d0 = 0.125D; double d1 =
		 * this.heightMap[i1 + i2]; double d2 = this.heightMap[j1 + i2]; double
		 * d3 = this.heightMap[k1 + i2]; double d4 = this.heightMap[l1 + i2];
		 * double d5 = (this.heightMap[i1 + i2 + 1] - d1) * d0; double d6 =
		 * (this.heightMap[j1 + i2 + 1] - d2) * d0; double d7 =
		 * (this.heightMap[k1 + i2 + 1] - d3) * d0; double d8 =
		 * (this.heightMap[l1 + i2 + 1] - d4) * d0;
		 * 
		 * for (int j2 = 0; j2 < 8; ++j2) { double d9 = 0.25D; double d10 = d1;
		 * double d11 = d2; double d12 = (d3 - d1) * d9; double d13 = (d4 - d2)
		 * * d9;
		 * 
		 * for (int k2 = 0; k2 < 4; ++k2) { double d14 = 0.25D; double d16 =
		 * (d11 - d10) * d14; double lvt_45_1_ = d10 - d16;
		 * 
		 * for (int l2 = 0; l2 < 4; ++l2) {
		 * 
		 * primer.setBlockState(i * 4 + k2, i2 * 8 + j2, l * 4 + l2,
		 * field_185982_a);
		 * 
		 * }
		 * 
		 * d10 += d12; d11 += d13; }
		 * 
		 * d1 += d5; d2 += d6; d3 += d7; d4 += d8; } } } }
		 */
	}

	public void replaceBiomeBlocks(int x, int z, ChunkPrimer primer, BiomeGenBase[] biomesIn) {
		if (!net.minecraftforge.event.ForgeEventFactory.onReplaceBiomeBlocks(this, x, z, primer, this.worldObj))
			return;
		double d0 = 0.03125D;
		this.field_186002_u = this.field_185994_m.func_151599_a(this.field_186002_u, (double) (x * 16), (double) (z * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

		for (int i = 0; i < 16; ++i) {
			for (int j = 0; j < 16; ++j) {
				BiomeGenBase biomegenbase = biomesIn[j + i * 16];
				generateBiomeTerrain(this.worldObj, this.rand, primer, x * 16 + i, z * 16 + j, this.field_186002_u[j + i * 16], biomegenbase);
			}
		}
	}

	protected static final IBlockState AIR = Blocks.air.getDefaultState();
	protected static final IBlockState BEDROCK = Blocks.bedrock.getDefaultState();
	protected static final IBlockState GRAVEL = Blocks.end_stone.getDefaultState();

	public void generateBiomeTerrain(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal, BiomeGenBase biomegenbase) {
		int i = worldIn.getSeaLevel();
		IBlockState iblockstate = biomegenbase.topBlock;
		IBlockState iblockstate1 = biomegenbase.fillerBlock;
		int j = -1;
		int k = (int) (noiseVal / 3.0D + 3.0D + rand.nextDouble() * 0.25D);
		int l = x & 15;
		int i1 = z & 15;
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

		for (int j1 = 255; j1 >= 0; --j1) {
			if (j1 <= rand.nextInt(5)) {
				chunkPrimerIn.setBlockState(i1, j1, l, BEDROCK);
			} else {
				IBlockState iblockstate2 = chunkPrimerIn.getBlockState(i1, j1, l);

				if (iblockstate2.getMaterial() == Material.air) {
					j = -1;
				} else if (iblockstate2.getBlock() == Blocks.end_stone) {
					if (j == -1) {
						if (k <= 0) {
							iblockstate = AIR;
							iblockstate1 = Blocks.end_stone.getDefaultState();
						} else if (j1 >= i - 4 && j1 <= i + 1) {
							iblockstate = biomegenbase.topBlock;
							iblockstate1 = biomegenbase.fillerBlock;
						}

						if (j1 < i && (iblockstate == null || iblockstate.getMaterial() == Material.air)) {
							iblockstate1 = Blocks.water.getDefaultState();
						}

						j = k;

						if (j1 >= i - 1) {
							chunkPrimerIn.setBlockState(i1, j1, l, iblockstate);
						} else if (j1 < i - 7 - k) {
							iblockstate = AIR;
							iblockstate1 = Blocks.end_stone.getDefaultState();
							chunkPrimerIn.setBlockState(i1, j1, l, GRAVEL);
						} else {
							chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);
						}
					} else if (j > 0) {
						--j;
						chunkPrimerIn.setBlockState(i1, j1, l, iblockstate1);

						if (j == 0 && iblockstate1.getBlock() == Blocks.sand) {
							j = rand.nextInt(4) + Math.max(0, j1 - 63);
							iblockstate1 = Blocks.end_stone.getDefaultState();
						}
					}
				}
			}
		}
	}

	public Chunk provideChunk(int x, int z) {
		this.rand.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();

		this.setBlocksInChunk(x, z, chunkprimer);

		// this.biomesForGeneration =
		// this.worldObj.getBiomeProvider().loadBlockGeneratorData(this.biomesForGeneration,
		// x * 16, z * 16, 16, 16);

		// this.replaceBiomeBlocks(x, z, chunkprimer, this.biomesForGeneration);

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);

		// byte[] abyte = chunk.getBiomeArray();

		/*
		 * for (int i = 0; i < abyte.length; ++i) { abyte[i] = (byte)
		 * BiomeGenBase.getIdForBiome(this.biomesForGeneration[i]); }
		 */

		chunk.generateSkylightMap();
		return chunk;
	}

	private void generateHeightmap(int p_185978_1_, int p_185978_2_, int p_185978_3_) {
		this.field_185989_h = this.field_185984_c.generateNoiseOctaves(this.field_185989_h, p_185978_1_, p_185978_3_, 5, 5, (double) this.settings.depthNoiseScaleX, (double) this.settings.depthNoiseScaleZ,
				(double) this.settings.depthNoiseScaleExponent);
		float f = this.settings.coordinateScale;
		float f1 = this.settings.heightScale;
		this.field_185986_e = this.field_185993_l.generateNoiseOctaves(this.field_185986_e, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, (double) (f / this.settings.mainNoiseScaleX), (double) (f1 / this.settings.mainNoiseScaleY),
				(double) (f / this.settings.mainNoiseScaleZ));
		this.field_185987_f = this.field_185991_j.generateNoiseOctaves(this.field_185987_f, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, (double) f, (double) f1, (double) f);
		this.field_185988_g = this.field_185992_k.generateNoiseOctaves(this.field_185988_g, p_185978_1_, p_185978_2_, p_185978_3_, 5, 33, 5, (double) f, (double) f1, (double) f);
		p_185978_3_ = 0;
		p_185978_1_ = 0;
		int i = 0;
		int j = 0;

		for (int k = 0; k < 5; ++k) {
			for (int l = 0; l < 5; ++l) {
				float f2 = 0.0F;
				float f3 = 0.0F;
				float f4 = 0.0F;
				int i1 = 2;
				BiomeGenBase biomegenbase = this.biomesForGeneration[k + 2 + (l + 2) * 10];

				for (int j1 = -i1; j1 <= i1; ++j1) {
					for (int k1 = -i1; k1 <= i1; ++k1) {
						BiomeGenBase biomegenbase1 = this.biomesForGeneration[k + j1 + 2 + (l + k1 + 2) * 10];
						float f5 = this.settings.biomeDepthOffSet + biomegenbase1.getBaseHeight() * this.settings.biomeDepthWeight;
						float f6 = this.settings.biomeScaleOffset + biomegenbase1.getHeightVariation() * this.settings.biomeScaleWeight;

						if (this.terrainType == WorldType.AMPLIFIED && f5 > 0.0F) {
							f5 = 1.0F + f5 * 2.0F;
							f6 = 1.0F + f6 * 4.0F;
						}

						float f7 = this.field_185999_r[j1 + 2 + (k1 + 2) * 5] / (f5 + 2.0F);

						if (biomegenbase1.getBaseHeight() > biomegenbase.getBaseHeight()) {
							f7 /= 2.0F;
						}

						f2 += f6 * f7;
						f3 += f5 * f7;
						f4 += f7;
					}
				}

				f2 = f2 / f4;
				f3 = f3 / f4;
				f2 = f2 * 0.9F + 0.1F;
				f3 = (f3 * 4.0F - 1.0F) / 8.0F;
				double d7 = this.field_185989_h[j] / 8000.0D;

				if (d7 < 0.0D) {
					d7 = -d7 * 0.3D;
				}

				d7 = d7 * 3.0D - 2.0D;

				if (d7 < 0.0D) {
					d7 = d7 / 2.0D;

					if (d7 < -1.0D) {
						d7 = -1.0D;
					}

					d7 = d7 / 1.4D;
					d7 = d7 / 2.0D;
				} else {
					if (d7 > 1.0D) {
						d7 = 1.0D;
					}

					d7 = d7 / 8.0D;
				}

				++j;
				double d8 = (double) f3;
				double d9 = (double) f2;
				d8 = d8 + d7 * 0.2D;
				d8 = d8 * (double) this.settings.baseSize / 8.0D;
				double d0 = (double) this.settings.baseSize + d8 * 4.0D;

				for (int l1 = 0; l1 < 33; ++l1) {
					double d1 = ((double) l1 - d0) * (double) this.settings.stretchY * 128.0D / 256.0D / d9;

					if (d1 < 0.0D) {
						d1 *= 4.0D;
					}

					double d2 = this.field_185987_f[i] / (double) this.settings.lowerLimitScale;
					double d3 = this.field_185988_g[i] / (double) this.settings.upperLimitScale;
					double d4 = (this.field_185986_e[i] / 10.0D + 1.0D) / 2.0D;
					double d5 = MathHelper.denormalizeClamp(d2, d3, d4) - d1;

					if (l1 > 29) {
						double d6 = (double) ((float) (l1 - 29) / 3.0F);
						d5 = d5 * (1.0D - d6) + -10.0D * d6;
					}

					this.heightMap[i] = d5;
					++i;
				}
			}
		}
	}

	public void populate(int x, int z) {
		BlockFalling.fallInstantly = true;
		int i = x * 16;
		int j = z * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(blockpos.add(16, 0, 16));
		this.rand.setSeed(this.worldObj.getSeed());
		long k = this.rand.nextLong() / 2L * 2L + 1L;
		long l = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long) x * k + (long) z * l ^ this.worldObj.getSeed());
		boolean flag = false;
		ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x, z);

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, x, z, flag);

		/*
		 * if (biomegenbase != Biomes.desert && biomegenbase !=
		 * Biomes.desertHills && this.settings.useWaterLakes && !flag &&
		 * this.rand.nextInt(this.settings.waterLakeChance) == 0) if
		 * (net.minecraftforge.event.terraingen.TerrainGen.populate(this,
		 * this.worldObj, this.rand, x, z, flag,
		 * net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.
		 * EventType.LAKE)) { int i1 = this.rand.nextInt(16) + 8; int j1 =
		 * this.rand.nextInt(256); int k1 = this.rand.nextInt(16) + 8; (new
		 * WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand,
		 * blockpos.add(i1, j1, k1)); }
		 */

		/*
		 * if (!flag && this.rand.nextInt(this.settings.lavaLakeChance / 10) ==
		 * 0 && this.settings.useLavaLakes) if
		 * (net.minecraftforge.event.terraingen.TerrainGen.populate(this,
		 * this.worldObj, this.rand, x, z, flag,
		 * net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.
		 * EventType.LAVA)) { int i2 = this.rand.nextInt(16) + 8; int l2 =
		 * this.rand.nextInt(this.rand.nextInt(248) + 8); int k3 =
		 * this.rand.nextInt(16) + 8;
		 * 
		 * if (l2 < this.worldObj.getSeaLevel() ||
		 * this.rand.nextInt(this.settings.lavaLakeChance / 8) == 0) { (new
		 * WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand,
		 * blockpos.add(i2, l2, k3)); } }
		 * 
		 * if (this.settings.useDungeons) if
		 * (net.minecraftforge.event.terraingen.TerrainGen.populate(this,
		 * this.worldObj, this.rand, x, z, flag,
		 * net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.
		 * EventType.DUNGEON)) { for (int j2 = 0; j2 <
		 * this.settings.dungeonChance; ++j2) { int i3 = this.rand.nextInt(16) +
		 * 8; int l3 = this.rand.nextInt(256); int l1 = this.rand.nextInt(16) +
		 * 8; (new WorldGenDungeons()).generate(this.worldObj, this.rand,
		 * blockpos.add(i3, l3, l1)); } }
		 */
		biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(i, 0, j));
		if (net.minecraftforge.event.terraingen.TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS))
			WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biomegenbase, i + 8, j + 8, 16, 16, this.rand);
		blockpos = blockpos.add(8, 0, 8);

		/*
		 * if (net.minecraftforge.event.terraingen.TerrainGen.populate(this,
		 * this.worldObj, this.rand, x, z, flag,
		 * net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.
		 * EventType.ICE)) { for (int k2 = 0; k2 < 16; ++k2) { for (int j3 = 0;
		 * j3 < 16; ++j3) { BlockPos blockpos1 =
		 * this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
		 * BlockPos blockpos2 = blockpos1.down();
		 * 
		 * if (this.worldObj.canBlockFreezeWater(blockpos2)) {
		 * this.worldObj.setBlockState(blockpos2, Blocks.ice.getDefaultState(),
		 * 2); }
		 * 
		 * if (this.worldObj.canSnowAt(blockpos1, true)) {
		 * this.worldObj.setBlockState(blockpos1,
		 * Blocks.snow_layer.getDefaultState(), 2); } } } }
		 */

		// Forge: End ICE

		int var4 = x * 16;
		int var5 = z * 16;

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, x, z, flag);

		BlockFalling.fallInstantly = false;
	}

	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return new ArrayList<BiomeGenBase.SpawnListEntry>();
	}

	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}

	public void recreateStructures(Chunk chunkIn, int x, int z) {
	}
}