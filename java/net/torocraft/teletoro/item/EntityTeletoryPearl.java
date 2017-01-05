package net.torocraft.teletoro.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.TeleToroUtil.TeleportorType;
import net.torocraft.teletoro.Teletory;

public class EntityTeletoryPearl extends EntityEnderPearl {

	public static String NAME = "ThrownTeletorypearl";

	public static void init(int entityId) {
		EntityRegistry.registerModEntity(EntityTeletoryPearl.class, NAME, entityId, TeleToro.instance, 60, 2, true);
	}

	public EntityTeletoryPearl(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntityTeletoryPearl(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
	}

	public EntityTeletoryPearl(World worldIn) {
		super(worldIn);
	}

	protected void onImpact(RayTraceResult result) {
		EntityLivingBase thrower = this.getThrower();

		if (result.entityHit != null) {
			if (result.entityHit == this.getThrower()) {
				return;
			}
			result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, thrower), 0.0F);
		}

		for (int i = 0; i < 32; ++i) {
			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[0]);
		}

		if (!this.world.isRemote) {
			if (thrower instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP) thrower;

				if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.world == this.world && !entityplayermp.isPlayerSleeping()) {
					net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5.0F);
					if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {

						if (thrower.isRiding()) {
							thrower.dismountRidingEntity();
						}

						if (thrower.dimension == Teletory.DIMID) {
							thrower.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
							thrower.fallDistance = 0.0F;
						} else {
							teleport(thrower);
							thrower.attackEntityFrom(DamageSource.fall, event.getAttackDamage());
						}

					}
				}
			} else if (thrower != null) {
				thrower.setPositionAndUpdate(this.posX, this.posY, this.posZ);
				thrower.fallDistance = 0.0F;
			}

			this.setDead();
		}
	}

	protected void onImpactOLD(RayTraceResult result) {

		if (world.isRemote) {
			return;
		}

		EntityLivingBase thrower = this.getThrower();

		if (thrower == null) {
			return;
		}

		if (hitEntity(result)) {
			teleport(result.entityHit);
		} else if (closeImpact(result, thrower)) {
			teleport(thrower);
		}

		this.setDead();
	}

	protected boolean hitEntity(RayTraceResult result) {
		return result.entityHit != null;
	}

	protected boolean closeImpact(RayTraceResult result, EntityLivingBase thrower) {
		return result.typeOfHit == RayTraceResult.Type.BLOCK && impactDistanceFromThrower(result, thrower) <= 3d;
	}

	protected double impactDistanceFromThrower(RayTraceResult result, EntityLivingBase thrower) {
		BlockPos blockpos = result.getBlockPos();
		double distanceSq = blockpos.distanceSq(thrower.posX, thrower.posY, thrower.posZ);
		return distanceSq;
	}

	private void teleport(Entity entity) {
		particles();
		Teletory.changeEntityDimension(entity, TeleportorType.PEARL);
	}

	protected void particles() {
		for (int i = 0; i < 32; ++i) {
			this.world.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[0]);
		}
	}
}
