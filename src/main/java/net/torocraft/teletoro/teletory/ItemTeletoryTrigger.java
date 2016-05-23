package net.torocraft.teletoro.teletory;

import static net.torocraft.teletoro.TeleToroUtil.getBlock;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTeletoryTrigger extends Item {
	public ItemTeletoryTrigger() {
		super();
		this.maxStackSize = 1;
		setMaxDamage(64);
		setCreativeTab(CreativeTabs.tabTools);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9, float par10)
	// public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer
	// par2EntityPlayer, World par3World, int par4, int par5, int par6, int
	// par7, float par8, float par9, float par10)
	{

		int par4 = pos.getX();
		int par5 = pos.getY();
		int par6 = pos.getZ();

		int par7 = side.getIndex();

		if (par7 == 0) {
			par5--;
		}
		if (par7 == 1) {
			par5++;
		}
		if (par7 == 2) {
			par6--;
		}
		if (par7 == 3) {
			par6++;
		}
		if (par7 == 4) {
			par4--;
		}
		if (par7 == 5) {
			par4++;
		}
		if (!par2EntityPlayer.canPlayerEdit(new BlockPos(par4, par5, par6), side, par1ItemStack)) {
			return EnumActionResult.FAIL;
		}
		Block i1 = getBlock(par3World, par4, par5, par6);
		if (i1 == Blocks.air) {
			par3World.playSound(par2EntityPlayer, new BlockPos(par4, par5, par6), SoundEvents.item_flintandsteel_use, SoundCategory.BLOCKS, 1.0F, itemRand.nextFloat() * 0.4F + 0.8F);
			Teletory.portal.tryToCreatePortal(par3World, par4, par5, par6);
		}
		par1ItemStack.damageItem(1, par2EntityPlayer);
		return EnumActionResult.SUCCESS;
	}
}