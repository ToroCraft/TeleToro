package net.torocraft.teletoro.teletory;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TeletoryWorldProvider extends WorldProvider {
	/*
	 * @Override public void registerWorldChunkManager() { this.worldChunkMgr =
	 * new BiomeProviderSingle(BiomeGenBase.biomeRegistry.getObject(new
	 * ResourceLocation("desert"))); this.isHellWorld = true; this.hasNoSky =
	 * false; // this.dimensionId = DIMID;
	 * 
	 * }
	 */

	public DimensionType getDimensionType() {
		return Teletory.type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float par1, float par2) {
		return new Vec3d(1.0D, 1.0D, 1.0D);
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new TeletoryChunkProvider(this.worldObj);
	}

	@Override
	public boolean isSurfaceWorld() {
		return false;
	}

	@Override
	public boolean canCoordinateBeSpawn(int par1, int par2) {
		return false;
	}

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean doesXZShowFog(int par1, int par2) {
		return false;
	}

}