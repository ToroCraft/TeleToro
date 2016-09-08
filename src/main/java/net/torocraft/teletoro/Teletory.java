package net.torocraft.teletoro;

import static net.torocraft.teletoro.TeleToroUtil.getBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.torocraft.teletoro.TeleToroUtil.TeleportorType;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.teleporter.FallFromTeletoryTeleporter;
import net.torocraft.teletoro.teleporter.TeletoryPearlTeleporter;
import net.torocraft.teletoro.teleporter.TeletoryTeleporter;
import net.torocraft.teletoro.world.TeletoryWorldProvider;

public class Teletory {

	public static int DIMID = 16;
	public static DimensionType TYPE = DimensionType.register("teletory", "_teletory", DIMID, TeletoryWorldProvider.class, true);

	private static ConcurrentHashMap<Runnable, Integer> runQueue = new ConcurrentHashMap<Runnable, Integer>();

	public static void changeEntityDimension(final Entity entity, final TeleportorType type) {
		runQueue.put(new TeleportRunner(entity, type), 0);
	}

	private static class TeleportRunner implements Runnable {

		private final Entity entity;
		private final TeleportorType type;

		public TeleportRunner(Entity entity, TeleportorType type) {
			this.entity = entity;
			this.type = type;
		}

		@Override
		public void run() {
			if (entity == null || type == null) {
				return;
			}

			if (entity instanceof EntityPlayerMP) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				int nextDimension = getNextDimension(player);
				changePlayerDimension(player, nextDimension, type);
				runQueue.put(new AchievementRunner((EntityPlayerMP) entity), 0);
			} else {
				// TODO: support teleporting non-players
				// wip_fakeTeletportNonPlayer(entity);
			}
		}

		private void particles() {
			World world = entity.getEntityWorld();
			Random rand = world.rand;
			for (int i = 0; i < 32; ++i) {
				world.spawnParticle(EnumParticleTypes.PORTAL, entity.posX, entity.posY + rand.nextDouble() * 2.0D, entity.posZ, rand.nextGaussian(), 0.0D, rand.nextGaussian(), new int[0]);
			}
		}
	}

	private static class AchievementRunner implements Runnable {

		private final EntityPlayerMP player;

		public AchievementRunner(EntityPlayerMP player) {
			this.player = player;
		}

		@Override
		public void run() {
			if (player == null) {
				return;
			}

			if (player.dimension == Teletory.DIMID) {
				player.addStat(TeleToroMod.TELETORY_ACHIEVEMNT);
			}
		}
	}

	public static void init(FMLInitializationEvent event) {
		DimensionManager.registerDimension(DIMID, TYPE);
	}

	private boolean isRunTick(World world) {
		return world.getTotalWorldTime() % 60L == 0L;
	}

	@SubscribeEvent
	public void limitBuildHeight(PlaceEvent event) {
		if (event.getBlockSnapshot() != null && event.getBlockSnapshot().getDimId() != Teletory.DIMID) {
			return;
		}

		if (event.getPlayer() == null) {
			return;
		}

		if (event.getBlockSnapshot().getPos().getY() > TeletoryWorldProvider.HEIGHT) {
			event.getPlayer().addChatMessage(new TextComponentString("max build height"));
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void limitEnderBlockDropsInTheTeletory(HarvestDropsEvent event) {

		if (!isEnderBlock(event)) {
			return;
		}

		EntityPlayer player = event.getHarvester();

		if (player == null || player.dimension != Teletory.DIMID) {
			return;
		}

		List<ItemStack> drops = event.getDrops();
		List<ItemStack> dropsToRemove = new ArrayList<ItemStack>(drops.size());

		Random rand = event.getWorld().rand;

		for (ItemStack drop : drops) {
			if (rand.nextInt(10) > 2) {
				dropsToRemove.add(drop);
			}
		}

		drops.removeAll(dropsToRemove);
	}

	private boolean isEnderBlock(HarvestDropsEvent event) {
		return event.getState().getBlock().getUnlocalizedName().equals("tile.enderBlock");
	}

	public static int getNextDimension(Entity entity) {
		if (entity.dimension != Teletory.DIMID) {
			return Teletory.DIMID;
		} else {
			return 0;
		}
	}

	@SubscribeEvent
	public void handleWorldTick(WorldTickEvent event) {
		if (runQueue.size() < 1) {
			return;
		}
		runNextQueueItem();
	}

	protected void runNextQueueItem() {
		Runnable nextRunnable = popRunQueue();
		if (nextRunnable != null) {
			nextRunnable.run();
		}
	}

	private Runnable popRunQueue() {
		Entry<Runnable, Integer> next = null;
		for (Entry<Runnable, Integer> n : runQueue.entrySet()) {
			next = n;
			break;
		}

		if (next == null) {
			return null;
		}

		if (next.getValue() < 1) {
			runQueue.remove(next.getKey());
			return next.getKey();
		} else {
			next.setValue(next.getValue() - 1);
			return null;
		}
	}

	@SubscribeEvent
	public void handlePlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player.worldObj.isRemote) {
			return;
		}

		if (event.player.dimension == Teletory.DIMID) {
			// TODO support damage to living entities
			feelThePainOfTheTeletory(event);
		}
	}

	protected void feelThePainOfTheTeletory(TickEvent.PlayerTickEvent event) {
		if (!(event.player instanceof EntityPlayerMP)) {
			return;
		}

		if (event.player.posY < -5) {
			changeEntityDimension(event.player, TeleportorType.FALL);
		}

		if (isRunTick(event.player.worldObj)) {
			hurtPlayer(event.player);
			spawnParticles(event.player);
		}
	}

	private void hurtPlayer(Entity entity) {
		entity.fallDistance = 0.0F;
		entity.attackEntityFrom(DamageSource.fall, 4f);

		if (entity.worldObj.rand.nextFloat() < 0.005F && entity.worldObj.getGameRules().getBoolean("doMobSpawning")) {
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
		if (ev.getEntity().getEntityWorld().isRemote || !(ev.getEntity() instanceof EntityPlayerMP)) {
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
		changeEntityDimension(thePlayer, TeleportorType.FALL);
	}

	private static boolean changePlayerDimension(EntityPlayerMP player, int dimId, TeleportorType type) {
		if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(player, dimId)) {
			return false;
		}

		if (player == null) {
			return false;
		}

		WorldServer world = player.mcServer.worldServerForDimension(dimId);

		Teleporter teleporter = getTeleporter(world, type);

		TeleToroUtil.setInvulnerableDimensionChange(player);
		player.timeUntilPortal = 10;
		player.mcServer.getPlayerList().transferPlayerToDimension(player, dimId, teleporter);
		// player.connection.sendPacket(new SPacketEffect(1032, BlockPos.ORIGIN,
		// 0, false));
		TeleToroUtil.resetStatusFields(player);
		return true;
	}

	private static Teleporter getTeleporter(WorldServer world, TeleportorType type) {
		return getCachedTeleporter(world, type);
	}

	private static Class<? extends Teleporter> getTeleporterClass(TeleportorType type) {
		switch (type) {
		case FALL:
			return FallFromTeletoryTeleporter.class;

		case PORTAL:
			return TeletoryTeleporter.class;

		case PEARL:
			return TeletoryPearlTeleporter.class;

		default:
			throw new UnsupportedOperationException("unknown teleporter [" + type + "]");
		}
	}

	private static Teleporter getNewTeleporterInstance(WorldServer world, TeleportorType type) {
		switch (type) {
		case FALL:
			return new FallFromTeletoryTeleporter(world);

		case PORTAL:
			return new TeletoryTeleporter(world);

		case PEARL:
			return new TeletoryPearlTeleporter(world);

		default:
			throw new UnsupportedOperationException("unknown teleporter [" + type + "]");
		}
	}

	private static Teleporter getCachedTeleporter(WorldServer world, TeleportorType type) {

		Class<? extends Teleporter> clazz = getTeleporterClass(type);

		for (Teleporter t : world.customTeleporters) {
			if (t.getClass().getName().equals(clazz.getName())) {
				return t;
			}
		}

		Teleporter t = getNewTeleporterInstance(world, type);
		world.customTeleporters.add(t);
		return t;
	}

}
