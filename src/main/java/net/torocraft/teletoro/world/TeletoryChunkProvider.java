package net.torocraft.teletoro.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.torocraft.teletoro.blocks.BlockEnderOre;

public class TeletoryChunkProvider implements IChunkGenerator {

	private final World world;
	private final Random random;

	private final NoiseGeneratorOctaves noise1;
	private double[] noiseBuffer;

	private final int xSize = 16;
	private final int ySize = surfaceThickness;
	private final int zSize = 16;

	private final int xScale = 5;
	private final int yScale = 6;
	private final int zScale = 15;

	private final int yOffset = 0;

	private final IBlockState base = Blocks.END_STONE.getDefaultState();
	private final IBlockState dirt = Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT);
	private final IBlockState ore = BlockEnderOre.INSTANCE.getDefaultState();
	private final IBlockState bush = Blocks.DEADBUSH.getDefaultState();

	public static final int surfaceHeight = 8;
	public static final int surfaceThickness = 5;
	public static final int dirtHeight = surfaceHeight + surfaceThickness - 2;

	private IBlockState block;

	public TeletoryChunkProvider(World worldIn, long seed) {
		world = worldIn;
		random = new Random(seed);
		noise1 = new NoiseGeneratorOctaves(random, 8);
	}

	public Chunk provideChunk(int chunkX, int chunkZ) {
		ChunkPrimer chunkprimer = new ChunkPrimer();
		int xOffset = chunkX * 16;
		int zOffset = chunkZ * 16;
		noiseBuffer = noise1.generateNoiseOctaves(noiseBuffer, xOffset, yOffset, zOffset, xSize, ySize, zSize, xScale, yScale, zScale);
		drawNoise(chunkprimer);
		return createChunk(chunkX, chunkZ, chunkprimer);
	}

	private void drawNoise(ChunkPrimer chunkprimer) {
		int pointer = 0;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < surfaceThickness; y++) {
					// if (noiseBuffer[pointer] > (60d - (10 * (surfaceThickness
					// - y)))) {
					if (noiseBuffer[pointer] > (70d - (8 * (surfaceThickness - y)))) {
						setBlock(chunkprimer, x, y + surfaceHeight, z);
					}
					pointer++;
				}
			}
		}
	}

	protected void setBlock(ChunkPrimer chunkprimer, int x, int y, int z) {

		if (y >= dirtHeight) {
			if (isAir(chunkprimer, x, y - 1, z)) {
				block = base;
			} else {
				block = dirt;
			}
		} else {
			if (random.nextInt(100) > 85) {
				block = ore;
			} else {
				block = base;
			}
		}

		chunkprimer.setBlockState(x, y, z, block);

		if (block == dirt && random.nextInt(100) > 80) {
			chunkprimer.setBlockState(x, y + 1, z, bush);
		}
	}

	protected boolean isAir(ChunkPrimer chunkprimer, int x, int y, int z) {
		return chunkprimer.getBlockState(x, y, z) == null || chunkprimer.getBlockState(x, y, z) == Blocks.AIR.getDefaultState();
	}

	private Chunk createChunk(int chunkX, int chunkZ, ChunkPrimer chunkprimer) {
		Chunk chunk = new Chunk(world, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;
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