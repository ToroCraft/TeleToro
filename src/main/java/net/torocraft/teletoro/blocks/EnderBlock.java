package net.torocraft.teletoro.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.torocraft.teletoro.TeleToro;

public class EnderBlock extends Block {

  public EnderBlock() {
    super(getSettings());
  }

  public static final String NAME = "ender_block";
  public static final EnderBlock INSTANCE = new EnderBlock();

  public static void init() {
    Registry.register(Registry.BLOCK, new Identifier(TeleToro.MODID, NAME), INSTANCE);
    Item item = new BlockItem(INSTANCE, new Item.Settings().group(ItemGroup.MISC));
    Registry.register(Registry.ITEM, new Identifier(TeleToro.MODID, NAME), item);
  }

  private static Block.Settings getSettings() {
    return Block.Settings.of(Material.METAL).strength(3.0F, 3.0F);
  }

  @Override
  @Environment(EnvType.CLIENT)
  public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
    super.onPlaced(world, pos, state, entity, stack);
    spawnParticles(world, pos);
  }

  private void spawnParticles(World world, BlockPos pos) {
    for (int i = 0; i < 32; ++i) {
      world.addParticle(ParticleTypes.PORTAL,
          pos.getX() + 0.5,
          pos.getY() + 0.5 + world.random.nextDouble() * 1.0D,
          pos.getZ() + 0.5, world.random.nextGaussian(), 0.0D,
          world.random.nextGaussian());
    }
  }

}
