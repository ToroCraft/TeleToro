package net.torocraft.teletoro;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class TeleToroUtil {
	public static Block getBlock(IBlockAccess world, int i, int j, int k) {
		return world.getBlockState(new BlockPos(i, j, k)).getBlock();
	}

	public static void setInvulnerableDimensionChange(EntityPlayerMP thePlayer) {
		try {
			System.out.println("setting invulnerableDimensionChange");
			Field invulnerableDimensionChange = thePlayer.getClass().getDeclaredField("invulnerableDimensionChange");
			invulnerableDimensionChange.setAccessible(true);
			invulnerableDimensionChange.setBoolean(thePlayer, true);
		} catch (Exception e) {
			throw new RuntimeException("Unable to set invulnerableDimensionChange via reflection", e);
		}
	}
}
