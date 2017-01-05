package net.torocraft.teletoro.world;

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

public class EmptyChunkProvider implements IChunkGenerator {
	private final World worldObj;

	public EmptyChunkProvider(World worldIn) {
		this.worldObj = worldIn;
	}

	public Chunk provideChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		chunk.generateSkylightMap();
		return chunk;
	}

	public void populate(int x, int z) {

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