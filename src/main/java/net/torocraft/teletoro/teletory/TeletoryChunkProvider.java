package net.torocraft.teletoro.teletory;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkGenerator;

public class TeletoryChunkProvider implements IChunkGenerator {
	private final World worldObj;

	public TeletoryChunkProvider(World worldIn) {
		this.worldObj = worldIn;
	}

	public Chunk provideChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

		/*
		 * for (int i = 0; i < this.cachedBlockIDs.length; ++i) { IBlockState
		 * iblockstate = this.cachedBlockIDs[i];
		 * 
		 * if (iblockstate != null) { for (int j = 0; j < 16; ++j) { for (int k
		 * = 0; k < 16; ++k) { chunkprimer.setBlockState(j, i, k, iblockstate);
		 * } } } }
		 */

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);

		/*
		 * Biome[] abiome =
		 * this.worldObj.getBiomeProvider().loadBlockGeneratorData((Biome[])
		 * null, x * 16, z * 16, 16, 16); byte[] abyte = chunk.getBiomeArray();
		 * 
		 * for (int l = 0; l < abyte.length; ++l) { abyte[l] = (byte)
		 * Biome.getIdForBiome(abiome[l]); }
		 */

		chunk.generateSkylightMap();
		return chunk;
	}

	public void populate(int x, int z) {

		/*
		 * int i = x * 16; int j = z * 16; BlockPos blockpos = new BlockPos(i,
		 * 0, j); Biome biome = this.worldObj.getBiomeGenForCoords(new
		 * BlockPos(i + 16, 0, j + 16)); boolean flag = false;
		 * this.random.setSeed(this.worldObj.getSeed()); long k =
		 * this.random.nextLong() / 2L * 2L + 1L; long l =
		 * this.random.nextLong() / 2L * 2L + 1L; this.random.setSeed((long) x *
		 * k + (long) z * l ^ this.worldObj.getSeed()); ChunkPos chunkpos = new
		 * ChunkPos(x, z);
		 * 
		 * for (MapGenStructure mapgenstructure : this.structureGenerators) {
		 * boolean flag1 = mapgenstructure.generateStructure(this.worldObj,
		 * this.random, chunkpos);
		 * 
		 * if (mapgenstructure instanceof MapGenVillage) { flag |= flag1; } }
		 */

	}

	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}

	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return Collections.<Biome.SpawnListEntry>emptyList();
	}

	@Nullable
	public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}

	public void recreateStructures(Chunk chunkIn, int x, int z) {

	}
}