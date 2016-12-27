package net.torocraft.teletoro.item.armor;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.material.ArmorMaterials;

public class ItemEnderArmor extends ItemArmor {

	public static final String NAME = "ender";

	public static ItemEnderArmor bootsItem;

	public static void init() {
		initBoots();
		GameRegistry.addRecipe(new ItemStack(bootsItem), "   ", "# #", "# #", '#', Items.ENDER_PEARL);
		GameRegistry.addRecipe(new ItemStack(bootsItem), "# #", "# #", "   ", '#', Items.ENDER_PEARL);
	}

	public static void registerRenders() {
		registerRendersBoots();
	}

	private static void initBoots() {
		bootsItem = new ItemEnderArmor(NAME + "_boots", 1, EntityEquipmentSlot.FEET);
		ResourceLocation resourceName = new ResourceLocation(TeleToro.MODID, NAME + "_boots");
		GameRegistry.register(bootsItem, resourceName);
	}

	private static void registerRendersBoots() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(bootsItem, 0, model("boots"));
	}

	private static ModelResourceLocation model(String model) {
		return new ModelResourceLocation(TeleToro.MODID + ":" + NAME + "_" + model, "inventory");
	}

	public ItemEnderArmor(String unlocalizedName, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
		super(ArmorMaterials.ENDER, renderIndexIn, equipmentSlotIn);
		this.setUnlocalizedName(unlocalizedName);
		// setMaxDamage(80);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Items.ENDER_PEARL;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		super.addInformation(stack, playerIn, tooltip, advanced);
		tooltip.add(I18n.format("item.ender_boots.tooltip", new Object[0]));
	}

}
