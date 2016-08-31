package net.torocraft.teletoro.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

public class TeletoryChunkProvider implements IChunkGenerator {

	private final World world;
	private final Random random;
	private final NoiseGeneratorOctaves noise1;

	public TeletoryChunkProvider(World worldIn, long seed) {
		world = worldIn;
		random = new Random(seed);
		noise1 = new NoiseGeneratorOctaves(random, 8);
	}

	public Chunk provideChunk(int chunkX, int chunkZ) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		// noise1

		// createSimpleRandomIslands(chunkprimer);

		int xOffset = chunkX * 16;
		int yOffset = 0;
		int zOffset = chunkZ * 16;
		int xSize = 16;
		int ySize = 16;
		int zSize = 8;
		int xScale = 1;
		int yScale = 1;
		int zScale = 1;

		double[] noiseBuffer = null;
		noise1.generateNoiseOctaves(noiseBuffer, xOffset, yOffset, zOffset, xSize, ySize, zSize, xScale, yScale, zScale);

		return createChunk(chunkX, chunkZ, chunkprimer);
	}

	private Chunk createChunk(int chunkX, int chunkZ, ChunkPrimer chunkprimer) {
		Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;
	}

	private void createSimpleRandomIslands(ChunkPrimer chunkprimer) {
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