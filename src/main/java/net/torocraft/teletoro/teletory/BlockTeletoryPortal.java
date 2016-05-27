package net.torocraft.teletoro.teletory;

import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockTeletoryPortal extends BlockPortal {

	@Override
	public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par5Entity) {

		int par2 = pos.getX();
		int par3 = pos.getY();
		int par4 = pos.getZ();

		if (par5Entity.getRidingEntity() == null && !par5Entity.isBeingRidden() && par5Entity instanceof EntityPlayerMP) {

			EntityPlayerMP thePlayer = (EntityPlayerMP) par5Entity;
			if (thePlayer.timeUntilPortal > 0) {
				thePlayer.timeUntilPortal = 10;
			} else if (thePlayer.dimension != Teletory.DIMID) {
				thePlayer.timeUntilPortal = 10;
				thePlayer.mcServer.getPlayerList().transferPlayerToDimension(thePlayer, Teletory.DIMID, new TeletoryTeleporter(thePlayer.mcServer.worldServerForDimension(Teletory.DIMID)));
			} else {
				thePlayer.timeUntilPortal = 10;
				thePlayer.mcServer.getPlayerList().transferPlayerToDimension(thePlayer, 0, new TeletoryTeleporter(thePlayer.mcServer.worldServerForDimension(0)));
			}
		}
	}
}