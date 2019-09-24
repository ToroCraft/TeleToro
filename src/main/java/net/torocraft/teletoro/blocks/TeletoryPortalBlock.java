package net.torocraft.teletoro.blocks;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.PortalBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.torocraft.teletoro.TeleToro;

public class TeletoryPortalBlock extends PortalBlock {


  public static final String NAME = "teletory_portal_block";
  public static final TeletoryPortalBlock INSTANCE = new TeletoryPortalBlock();

  public TeletoryPortalBlock() {
    super(getSettings());
  }

  private static Settings getSettings() {
    return FabricBlockSettings.of(Material.PORTAL)
        .noCollision()
        .ticksRandomly()
        .strength(-1f, -1f)
        .sounds(BlockSoundGroup.GLASS)
        .lightLevel(11)
        .dropsNothing()
        .build();
  }

  public static void init() {
    Registry.register(Registry.BLOCK, new Identifier(TeleToro.MODID, NAME), INSTANCE);
    Item item = new BlockItem(INSTANCE, new Item.Settings().group(ItemGroup.MISC));
    Registry.register(Registry.ITEM, new Identifier(TeleToro.MODID, NAME), item);
  }
}
