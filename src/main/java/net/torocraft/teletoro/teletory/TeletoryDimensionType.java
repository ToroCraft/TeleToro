package net.torocraft.teletoro.teletory;

import net.fabricmc.fabric.api.dimension.v1.EntityPlacer;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensionType;
import net.minecraft.block.pattern.BlockPattern.TeleportTarget;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.torocraft.teletoro.TeleToro;

public class TeletoryDimensionType {

  public static FabricDimensionType TYPE;

  public static void init() {
    TYPE = FabricDimensionType
        .builder()
        .skyLight(false)
        .factory(TeletoryDimension::new)
        .defaultPlacer(new EntityPlacer() {
          @Override
          public TeleportTarget placeEntity(Entity teleported, ServerWorld destination, Direction portalDir, double horizontalOffset, double verticalOffset) {
            Vec3d pos = new Vec3d(0, 100, 0);
            Vec3d vel = Vec3d.ZERO;
            int yaw = 0;
            return new TeleportTarget(pos, vel, yaw);
          }
        })
        .buildAndRegister(new Identifier(TeleToro.MODID, "teletory"));
  }
}
