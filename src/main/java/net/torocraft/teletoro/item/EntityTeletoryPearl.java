package net.torocraft.teletoro.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.teletoro.TeleToroUtil;
import net.torocraft.teletoro.TeleToroUtil.TeleportorType;
import net.torocraft.teletoro.Teletory;

public class EntityTeletoryPearl extends EntityThrowable {
	private EntityLivingBase thrower;

	public EntityTeletoryPearl(World worldIn) {
		super(worldIn);
	}

	public EntityTeletoryPearl(World worldIn, EntityLivingBase throwerIn) {
		super(worldIn, throwerIn);
		this.thrower = throwerIn;
	}

	@SideOnly(Side.CLIENT)
	public EntityTeletoryPearl(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public static void registerFixesEnderPearl(DataFixer fixer) {
		EntityThrowable.registerFixesThrowable(fixer, "ThrownEnderpearl");
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(RayTraceResult result) {
		EntityLivingBase entitylivingbase = this.getThrower();

		TeleToroUtil.changePlayerDimension((EntityPlayerMP) entitylivingbase, Teletory.DIMID, TeleportorType.FALL);

		if (result.entityHit != null) {
			if (result.entityHit == this.thrower) {
				teleportThrower();
				return;
			}
			teleportHitEntity();
		}
	}

	private void teleportHitEntity() {
		// TODO Auto-generated method stub
		particles();
	}

	private void teleportThrower() {
		// TODO Auto-generated method stub
		particles();
	}

	protected void particles() {
		for (int i = 0; i < 32; ++i) {
			this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian(), new int[0]);
		}
	}

	protected void handleTeleport(EntityLivingBase entitylivingbase) {
		if (!this.worldObj.isRemote) {
			if (entitylivingbase instanceof EntityPlayerMP) {
				EntityPlayerMP entityplayermp = (EntityPlayerMP) entitylivingbase;

				if (entityplayermp.connection.getNetworkManager().isChannelOpen() && entityplayermp.worldObj == this.worldObj && !entityplayermp.isPlayerSleeping()) {
					net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(entityplayermp, this.posX, this.posY, this.posZ, 5.0F);
					if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
						if (this.rand.nextFloat() < 0.05F && this.worldObj.getGameRules().getBoolean("doMobSpawning")) {
							EntityEndermite entityendermite = new EntityEndermite(this.worldObj);
							entityendermite.setSpawnedByPlayer(true);
							entityendermite.setLocationAndAngles(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, entitylivingbase.rotationYaw, entitylivingbase.rotationPitch);
							this.worldObj.spawnEntityInWorld(entityendermite);
						}

						if (entitylivingbase.isRiding()) {
							entitylivingbase.dismountRidingEntity();
						}

						entitylivingbase.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
						entitylivingbase.fallDistance = 0.0F;
						entitylivingbase.attackEntityFrom(DamageSource.fall, event.getAttackDamage());
					}
				}
			} else if (entitylivingbase != null) {
				entitylivingbase.setPositionAndUpdate(this.posX, this.posY, this.posZ);
				entitylivingbase.fallDistance = 0.0F;
			}

			this.setDead();
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		EntityLivingBase entitylivingbase = this.getThrower();

		if (entitylivingbase != null && entitylivingbase instanceof EntityPlayer && !entitylivingbase.isEntityAlive()) {
			this.setDead();
		} else {
			super.onUpdate();
		}
	}
}
