package net.torocraft.teletoro;

import net.fabricmc.api.ModInitializer;
import net.torocraft.teletoro.blocks.EnderBlock;
import net.torocraft.teletoro.blocks.TeletoryPortalBlock;
import net.torocraft.teletoro.teletory.TeletoryDimensionType;

public class TeleToro implements ModInitializer {

  public static final String MODID = "teletoro";

  @Override
  public void onInitialize() {
    EnderBlock.init();
    TeletoryPortalBlock.init();
    TeletoryDimensionType.init();
  }

}
