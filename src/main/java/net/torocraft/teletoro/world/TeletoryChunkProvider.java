package net.torocraft.teletoro.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenStructure;

public class TeletoryChunkProvider implements IChunkGenerator {
	private final World world;
	private final Random random;
	private final IBlockState[] cachedBlockIDs = new IBlockState[256];
	private final FlatGeneratorInfo info;
	private final List<MapGenStructure> structureGenerators = Lists.<MapGenStructure>newArrayList();
	private final boolean hasDecoration;
	// private final boolean hasDungeons;
	private WorldGenLakes waterLakeGenerator;
	private WorldGenLakes lavaLakeGenerator;

	public TeletoryChunkProvider(World worldIn, long seed) {
		world = worldIn;
		random = new Random(seed);

		info = new FlatGeneratorInfo();
		info.setBiome(Biome.getIdForBiome(Biomes.HELL));
		info.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
		info.getFlatLayers().add(new FlatLayerInfo(1, Blocks.END_STONE));
		info.updateLayers();

		int seaLevel = 0;
		int k = 0;
		boolean flag = true;

		for (FlatLayerInfo flatlayerinfo : this.info.getFlatLayers()) {

			for (int y = flatlayerinfo.getMinY(); y < flatlayerinfo.getMinY() + flatlayerinfo.getLayerCount(); ++y) {

				IBlockState iblockstate = flatlayerinfo.getLayerMaterial();

				if (iblockstate.getBlock() != Blocks.AIR) {
					flag = false;
					this.cachedBlockIDs[y] = iblockstate;
				}
			}

			if (flatlayerinfo.getLayerMaterial().getBlock() == Blocks.AIR) {
				k += flatlayerinfo.getLayerCount();
			} else {
				seaLevel += flatlayerinfo.getLayerCount() + k;
				k = 0;
			}
		}

		worldIn.setSeaLevel(seaLevel);
		this.hasDecoration = flag && this.info.getBiome() != Biome.getIdForBiome(Biomes.VOID) ? false : this.info.getWorldFeatures().containsKey("decoration");
	}

	public Chunk provideChunk(int chunkX, int chunkZ) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		boolean onIsland = false;
		int islandX = 0;
		int islandSize = 0;

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				if (continueIsland(onIsland, islandX, x, islandSize) || random.nextInt(100) < 5) {
					for (int y = 0; y < 3; y++) {
						chunkprimer.setBlockState(x, y, z, Blocks.END_STONE.getDefaultState());
						onIsland = true;
						islandX = x;
						islandSize = 5;
					}
				}
			}
		}



		Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);

		chunk.generateSkylightMap();
		return chunk;
	}

	protected boolean continueIsland(boolean onIsland, int islandX, int x, int islandSize) {
		if (islandSize <= 0) {
			onIsland = false;
			return false;
		}
		islandSize--;
		return onIsland && islandX == x;
	}

	public void populate(int chunkX, int chunkZ) {
		net.minecraft.block.BlockFalling.fallInstantly = true;

		setChunkSeed(chunkX, chunkZ);

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(true, this, this.world, this.random, chunkX, chunkZ, false);

		net.minecraftforge.event.ForgeEventFactory.onChunkPopulate(false, this, this.world, this.random, chunkX, chunkZ, false);
		net.minecraft.block.BlockFalling.fallInstantly = false;
	}

	protected void setChunkSeed(int chunkX, int chunkZ) {
		random.setSeed(this.world.getSeed());
		long k = random.nextLong() / 2L * 2L + 1L;
		long l = random.nextLong() / 2L * 2L + 1L;
		random.setSeed((long) chunkX * k + (long) chunkZ * l ^ this.world.getSeed());
	}

	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return new ArrayList<Biome.SpawnListEntry>(0);
	}

	@Nullable
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}

	public void recreateStructures(Chunk chunkIn, int x, int z) {

	}
}