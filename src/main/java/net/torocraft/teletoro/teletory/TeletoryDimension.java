package net.torocraft.teletoro.teletory;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;

public class TeletoryDimension extends Dimension {

  public TeletoryDimension(World world, DimensionType type) {
    super(world, type);
  }

  @Override
  public ChunkGenerator<?> createChunkGenerator() {
    return ChunkGeneratorType.SURFACE.create(world, BiomeSourceType.FIXED.applyConfig(BiomeSourceType.FIXED.getConfig()), new OverworldChunkGeneratorConfig());
  }

  @Override
  public BlockPos getSpawningBlockInChunk(ChunkPos var1, boolean var2) {
    return null;
  }

  @Override
  public BlockPos getTopSpawningBlockPosition(int var1, int var2, boolean var3) {
    return null;
  }

  @Override
  public float getSkyAngle(long var1, float var3) {
    return 0;
  }

  @Override
  public boolean hasVisibleSky() {
    return false;
  }

  @Override
  public Vec3d getFogColor(float var1, float var2) {
    return new Vec3d(0D, 0D, 0D);
  }

  @Override
  public boolean canPlayersSleep() {
    return false;
  }

  @Override
  public boolean shouldRenderFog(int var1, int var2) {
    return false;
  }

  @Override
  public DimensionType getType() {
    return TeletoryDimensionType.TYPE;
  }
}
