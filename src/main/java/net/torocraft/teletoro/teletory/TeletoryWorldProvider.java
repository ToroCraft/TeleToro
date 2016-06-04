package net.torocraft.teletoro.teletory;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeProviderSingle;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TeletoryWorldProvider extends WorldProvider {

	public TeletoryWorldProvider() {
		this.biomeProvider = new BiomeProviderSingle(Biomes.HELL);
		this.isHellWorld = true;
		this.hasNoSky = true;

	}

	@Override
	public String getWelcomeMessage() {
		return "Entering the Teletory";
	}

	@Override
	public String getDepartMessage() {
		return "Leaving the Teletory";
	}

	public DimensionType getDimensionType() {
		return Teletory.type;
	}

	public static final int HEIGHT = 10;

	@Override
	public int getActualHeight() {
		return HEIGHT;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float par1, float par2) {
		return new Vec3d(0D, 0D, 0D);
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

	@Override
	public boolean isSkyColored() {
		return false;
	}

	@Override
	public boolean getHasNoSky() {
		return true;
	}

	@Override
	public IRenderHandler getWeatherRenderer() {
		return null;
	}


	@Override
	public boolean isDaytime() {
		return false;
	}

	@Override
	public float getSunBrightness(float par1) {
		return 0;
	}

	@Override
	public float getStarBrightness(float par1) {
		return 0;
	}

	@Override
	public void updateWeather() {

	}

	@Override
	public void onWorldUpdateEntities() {

	}


}