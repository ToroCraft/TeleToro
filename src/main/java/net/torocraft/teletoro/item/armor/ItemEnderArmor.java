package net.torocraft.teletoro.item.armor;

import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.item.ItemTeletoryPearl;
import net.torocraft.teletoro.material.ArmorMaterials;

@EventBusSubscriber
public class ItemEnderArmor extends ItemArmor {

	public static final String NAME = "ender";

	public static ItemEnderArmor bootsItem;


	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		bootsItem = new ItemEnderArmor(NAME + "_boots", 1, EntityEquipmentSlot.FEET);
		bootsItem.setRegistryName(new ResourceLocation(TeleToro.MODID, NAME + "_boots"));
		event.getRegistry().register(bootsItem);
	}

	public static void registerRenders() {
		registerRendersBoots();
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
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add(I18n.format("item.ender_boots.tooltip", new Object[0]));
	}

}
