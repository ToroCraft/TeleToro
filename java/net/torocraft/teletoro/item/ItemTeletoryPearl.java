package net.torocraft.teletoro.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;

public class ItemTeletoryPearl extends Item {

	public static ItemTeletoryPearl INSTANCE;

	public static final String NAME = "teletorypearl";

	public static void init() {
		INSTANCE = new ItemTeletoryPearl();
		ResourceLocation resourceName = new ResourceLocation(TeleToro.MODID, NAME.toLowerCase());
		GameRegistry.register(INSTANCE, resourceName);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory"));
	}

	public ItemTeletoryPearl() {
		setUnlocalizedName(NAME);
		this.maxStackSize = 16;
		this.setCreativeTab(CreativeTabs.MISC);
	}

	public ActionResult<ItemStack> onItemRightClick(World itemStackIn, EntityPlayer worldIn, EnumHand playerIn) {
		ItemStack itemstack = worldIn.getHeldItem(playerIn);

		if (!worldIn.capabilities.isCreativeMode) {
			itemstack.shrink(1);
		}

		itemStackIn.playSound((EntityPlayer) null, worldIn.posX, worldIn.posY, worldIn.posZ, SoundEvents.ENTITY_ENDERPEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		worldIn.getCooldownTracker().setCooldown(this, 20);

		if (!itemStackIn.isRemote) {
			EntityTeletoryPearl entityenderpearl = new EntityTeletoryPearl(itemStackIn, worldIn);
			entityenderpearl.setHeadingFromThrower(worldIn, worldIn.rotationPitch, worldIn.rotationYaw, 0.0F, 1.5F, 1.0F);
			itemStackIn.spawnEntity(entityenderpearl);
		}

		worldIn.addStat(StatList.getObjectUseStats(this));
		return new ActionResult(EnumActionResult.SUCCESS, itemstack);
	}

}
