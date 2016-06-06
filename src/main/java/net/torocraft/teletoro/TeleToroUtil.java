package net.torocraft.teletoro;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.torocraft.teletoro.teletory.TeletoryTeleporter;

public class TeleToroUtil {
	public static Block getBlock(IBlockAccess world, int i, int j, int k) {
		return world.getBlockState(new BlockPos(i, j, k)).getBlock();
	}

	public static TeletoryTeleporter getTeleporter(WorldServer world) {
		for (Teleporter t : world.customTeleporters) {
			if (t instanceof TeletoryTeleporter) {
				return (TeletoryTeleporter) t;
			}
		}

		TeletoryTeleporter t = new TeletoryTeleporter(world);
		world.customTeleporters.add(t);
		return t;
	}

	public static void resetStatusFields(EntityPlayerMP player) {
		try {
			Field lastExperience = getReflectionField("field_71144_ck", "lastExperience");
			Field lastHealth = getReflectionField("field_71149_ch", "lastHealth");
			Field lastFoodLevel = getReflectionField("field_71146_ci", "lastFoodLevel");

			lastExperience.setInt(player, -1);
			lastHealth.setFloat(player, -1.0F);
			lastFoodLevel.setInt(player, -1);
		} catch (Exception e) {
			throw new RuntimeException("Unable to set reset status fields via reflection", e);
		}
	}

	public static void setInvulnerableDimensionChange(EntityPlayerMP thePlayer) {
		try {
			Field invulnerableDimensionChange = getReflectionField("field_184851_cj", "invulnerableDimensionChange");
			invulnerableDimensionChange.setBoolean(thePlayer, true);
		} catch (Exception e) {
			throw new RuntimeException("Unable to set invulnerableDimensionChange via reflection", e);
		}
	}

	public static Field getReflectionField(String... names) {
		Field f = null;
		for (String name : names) {
			f = getFieldFromPlayer(name);
			if (f != null) {
				f.setAccessible(true);
				return f;
			}
		}
		throw new RuntimeException(join(names, ", ") + " field not found in " + EntityPlayerMP.class.getName());
	}

	public static Field getFieldFromPlayer(String name) {
		try {
			return EntityPlayerMP.class.getDeclaredField(name);
		} catch (Exception e) {
			return null;
		}
	}

	public static String join(String[] aArr, String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0)
				sbStr.append(sSep);
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}

}
