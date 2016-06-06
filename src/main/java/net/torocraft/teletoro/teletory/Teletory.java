package net.torocraft.teletoro.teletory;

import static net.torocraft.teletoro.TeleToroUtil.getBlock;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.torocraft.teletoro.TeleToroUtil;

public class Teletory {

	public static int DIMID = 16;
	public static DimensionType type = DimensionType.register("teletory", "_teletory", DIMID, TeletoryWorldProvider.class, true);

	public static void init(FMLInitializationEvent event) {
		DimensionManager.registerDimension(DIMID, type);
	}

	private boolean isRunTick(World world) {
		return world.getTotalWorldTime() % 40L == 0L;
	}

	@SubscribeEvent
	public void limitBuildHeight(PlaceEvent event) {
		if (event.getBlockSnapshot() != null && event.getBlockSnapshot().getDimId() != Teletory.DIMID) {
			return;
		}

		if (event.getPlayer() == null) {
			return;
		}

		if (event.getBlockSnapshot().getPos().getY() > 10) {
			event.getPlayer().addChatMessage(new TextComponentString("max build height"));
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void feelThePainOfTheTeletory(TickEvent.PlayerTickEvent event) {
		if (event.player.dimension != Teletory.DIMID) {
			return;
		}

		if (!(event.player instanceof EntityPlayerMP)) {
			return;
		}

		if (event.player.posY < -5) {
			changeDimension((EntityPlayerMP) event.player, 0);
		}

		if (isRunTick(event.player.worldObj)) {
			hurtPlayer(event.player);
			spawnParticles(event.player);
		}

	}

	private void hurtPlayer(Entity entity) {
		entity.fallDistance = 0.0F;
		entity.attackEntityFrom(DamageSource.fall, 3f);

		if (entity.worldObj.rand.nextFloat() < 0.015F && entity.worldObj.getGameRules().getBoolean("doMobSpawning")) {
			EntityEndermite entityendermite = new EntityEndermite(entity.worldObj);
			entityendermite.setSpawnedByPlayer(true);
			entityendermite.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
			entity.worldObj.spawnEntityInWorld(entityendermite);
		}
	}

	private void spawnParticles(Entity entity) {
		for (int i = 0; i < 32; ++i) {
			entity.worldObj.spawnParticle(EnumParticleTypes.PORTAL, entity.posX, entity.posY + entity.worldObj.rand.nextDouble() * 2.0D, entity.posZ, entity.worldObj.rand.nextGaussian(), 0.0D, entity.worldObj.rand.nextGaussian(),
					new int[0]);
		}
	}

	@SubscribeEvent
	public void useFlintAndSteel(RightClickBlock event) {
		if (event.getItemStack() == null || event.getItemStack().getItem() != Items.FLINT_AND_STEEL) {
			return;
		}


		BlockPos pos = event.getPos();

		int par4 = pos.getX();
		int par5 = pos.getY();
		int par6 = pos.getZ();

		int par7;

		if (event.getFace() == null) {
			par7 = 0;
		} else {
			par7 = event.getFace().getIndex();
		}

		if (par7 == 0) {
			par5--;
		}
		if (par7 == 1) {
			par5++;
		}
		if (par7 == 2) {
			par6--;
		}
		if (par7 == 3) {
			par6++;
		}
		if (par7 == 4) {
			par4--;
		}
		if (par7 == 5) {
			par4++;
		}

		EntityPlayer par2EntityPlayer = event.getEntityPlayer();

		Block i1 = getBlock(event.getWorld(), par4, par5, par6);
		if (i1 == Blocks.AIR) {
			boolean created = BlockTeletoryPortal.INSTANCE.trySpawnPortal(event.getWorld(), new BlockPos(par4, par5, par6));
			if (created) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void fallOutOfTeletory(LivingHurtEvent ev) {
		if (!(ev.getEntity() instanceof EntityPlayerMP)) {
			return;
		}

		if (ev.getSource() != DamageSource.outOfWorld) {
			return;
		}

		EntityPlayerMP thePlayer = (EntityPlayerMP) ev.getEntity();

		if (thePlayer.dimension != Teletory.DIMID) {
			return;
		}

		ev.setCanceled(true);

		changeDimension(thePlayer, 0);

	}


	private void changeDimension(EntityPlayerMP player, int dimId) {

		// FIXME
		if (player.worldObj.isRemote || !(player.worldObj instanceof WorldServer)) {
			return;
		}

		TeleToroUtil.setInvulnerableDimensionChange(player);
		player.timeUntilPortal = 10;
		player.mcServer.getPlayerList().transferPlayerToDimension(player, dimId, new FallFromTeletoryTeleporter(player.mcServer.worldServerForDimension(dimId)));
		player.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN, 0, false));
		TeleToroUtil.resetStatusFields(player);
	}
}
