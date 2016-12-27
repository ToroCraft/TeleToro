package net.torocraft.teletoro.material;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.EnumHelper;
import net.torocraft.teletoro.TeleToro;

public class ArmorMaterials {
	
	private static final String MODID = TeleToro.MODID;
	

	public static ArmorMaterial ENDER;

	/*
	 * LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15,
	 * SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F),
	 * 
	 * CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12,
	 * SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0F),
	 * 
	 * IRON("iron", 15, new int[]{2, 5, 6, 2}, 9,
	 * SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F),
	 * 
	 * GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25,
	 * SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 0.0F),
	 * 
	 * DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10,
	 * SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0F);
	 */

	public static void init() {
		initEnder();
	}

	protected static void initEnder() {
		int durability = 5;
		int[] reductionAmounts = { 0, 0, 0, 0 };
		int enchantability = 8;
		SoundEvent soundOnEquip = SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND;
		float toughness = 0;
		ENDER = EnumHelper.addArmorMaterial("ENDER", MODID + ":ender_armor", durability, reductionAmounts, enchantability, soundOnEquip, toughness);
	}

}
