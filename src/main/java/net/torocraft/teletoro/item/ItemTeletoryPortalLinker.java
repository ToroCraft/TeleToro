package net.torocraft.teletoro.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.Teletory;
import net.torocraft.teletoro.blocks.BlockAbstractPortal.Size;
import net.torocraft.teletoro.blocks.BlockLinkedTeletoryPortal;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.blocks.TileEntityLinkedTeletoryPortal;

@EventBusSubscriber
public class ItemTeletoryPortalLinker extends Item {

	public static ItemTeletoryPortalLinker INSTANCE;
	public static final String NAME = "teletory_portal_linker";
	public static ModelResourceLocation model = new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory");
	public static ModelResourceLocation modelOn = new ModelResourceLocation(TeleToro.MODID + ":" + NAME + "_on", "inventory");

	@SubscribeEvent
	public static void init(RegistryEvent.Register<Item> event) {
		INSTANCE = new ItemTeletoryPortalLinker();
		INSTANCE.setRegistryName(new ResourceLocation(TeleToro.MODID, NAME.toLowerCase()));
		event.getRegistry().register(INSTANCE);
	}

	public static boolean isActive(ItemStack stack) {
		PortalLinkerOrigin remoteInfo = ItemTeletoryPortalLinker.getLinkOrigin(stack);
		return remoteInfo != null && remoteInfo.pos != null;
	}

	interface MeshDefinitionFix extends ItemMeshDefinition {
		ModelResourceLocation getLocation(final ItemStack stack);

		// Helper method to easily create lambda instances of this class
		static ItemMeshDefinition create(final MeshDefinitionFix lambda) {
			return lambda;
		}

		@Override
		default ModelResourceLocation getModelLocation(final ItemStack stack) {
			return getLocation(stack);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void registerRendersForLinkerStatic() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
	}

	@SideOnly(Side.CLIENT)
	public static void registerRendersForLinker2() {
		ModelBakery.registerItemVariants(INSTANCE);

		ModelLoader.setCustomMeshDefinition(INSTANCE, MeshDefinitionFix.create(stack -> {
			if (isActive(stack)) {
				return modelOn;
			} else {
				return model;
			}
		}));

		ModelLoader.registerItemVariants(INSTANCE, new ModelResourceLocation[] { model, modelOn });

	}

	@SideOnly(Side.CLIENT)
	public static void registerRendersForLinker() {
		ModelLoader.setCustomMeshDefinition(INSTANCE, stack -> {
			if (isActive(stack)) {
				return modelOn;
			} else {
				return model;
			}
		});

		ModelLoader.registerItemVariants(INSTANCE, new ModelResourceLocation[] { model, modelOn });
	}

	public ItemTeletoryPortalLinker() {
		setUnlocalizedName(NAME);
		this.maxStackSize = 1;
		this.setMaxDamage(2);
		this.setCreativeTab(CreativeTabs.MISC);

	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,
			float hitZ) {
		Block block = world.getBlockState(pos).getBlock();
		if (block == BlockTeletoryPortal.INSTANCE) {
			onItemUsedOnPortalBlock(player, world, pos, player.getHeldItem(hand));
		}

		return EnumActionResult.PASS;
	}

	private void onItemUsedOnPortalBlock(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {

		if (world.isRemote || player.dimension != 0) {
			return;
		}

		if (stack == null || stack.getItem() != INSTANCE) {
			return;
		}

		ControlBlockLocation thisPortal = findControllerBlock(world, pos, STANDARD_SIZER);

		if (thisPortal == null || thisPortal.pos == null) {
			return;
		}

		PortalLinkerOrigin remoteInfo = getLinkOrigin(stack);

		if (remoteInfo == null || remoteInfo.pos == null) {
			setOriginPortal(player, stack, thisPortal);

		} else {
			linkPortalWithOrigin(player, world, stack, thisPortal, remoteInfo);
		}
	}

	private void linkPortalWithOrigin(EntityPlayer player, World world, ItemStack stack, ControlBlockLocation thisPortal,
			PortalLinkerOrigin remoteInfo) {

		ControlBlockLocation remotePortal = findControllerBlock(world, remoteInfo.pos, STANDARD_SIZER);

		stack.setTagInfo("origin", new NBTTagLong(0));
		stack.setTagInfo("dimid", new NBTTagInt(0));

		if (remotePortal == null) {
			return;
		}

		linkPortalTo(world, thisPortal, remotePortal, remoteInfo.dimId, remoteInfo.side);
		linkPortalTo(world, remotePortal, thisPortal, player.dimension, getSide(player, thisPortal));

		playSound(player);
		stack.damageItem(1, player);
	}

	private void setOriginPortal(EntityPlayer player, ItemStack stack, ControlBlockLocation thisPortal) {
		stack.setTagInfo("origin", new NBTTagLong(thisPortal.pos.toLong()));
		stack.setTagInfo("dimid", new NBTTagInt(player.dimension));
		int side = getSide(player, thisPortal);
		stack.setTagInfo("side", new NBTTagInt(side));
		playSound(player);
	}

	private void playSound(EntityPlayer player) {
		player.world.playSound((EntityPlayer) null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT,
				SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	private int getSide(EntityPlayer player, ControlBlockLocation thisPortal) {
		int side;
		if (Axis.X.equals(thisPortal.axis)) {
			side = player.getPosition().getZ() > thisPortal.pos.getZ() ? 1 : 0;
		} else {
			side = player.getPosition().getX() > thisPortal.pos.getX() ? 1 : 0;
		}
		return side;
	}

	private void linkPortalTo(final World world, final ControlBlockLocation from, final ControlBlockLocation to, final int remoteDimId,
			final int remoteSide) {

		final Size size = STANDARD_SIZER.get(world, from.pos, from.axis);
		Teletory.runQueue.put(new Runnable() {
			@Override
			public void run() {
				size.placePortalBlocks(BlockLinkedTeletoryPortal.INSTANCE);
				TileEntityLinkedTeletoryPortal te = new TileEntityLinkedTeletoryPortal();
				te.setDimId(remoteDimId);
				te.setDestination(to.pos);
				te.setSide(remoteSide);
				world.setTileEntity(from.pos, te);
			}
		}, 0);
	}

	private static PortalLinkerOrigin getLinkOrigin(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return null;
		}

		long serializedPos = stack.getTagCompound().getLong("origin");

		if (serializedPos == 0) {
			return null;
		}

		PortalLinkerOrigin o = new PortalLinkerOrigin();
		o.dimId = stack.getTagCompound().getInteger("dimid");
		o.pos = BlockPos.fromLong(serializedPos);
		o.side = stack.getTagCompound().getInteger("side");

		return o;

	}

	public static ControlBlockLocation findControllerBlock(World world, BlockPos pos, SizerFactory sizerFactory) {
		Size size = sizerFactory.get(world, pos, Axis.X);

		ControlBlockLocation loc = new ControlBlockLocation();

		if (size.isValid()) {
			loc.pos = size.getBottomLeft();
			loc.axis = Axis.X;
			return loc;
		}

		size = sizerFactory.get(world, pos, Axis.Z);

		if (size.isValid()) {
			loc.pos = size.getBottomLeft();
			loc.axis = Axis.Z;
			return loc;
		}

		return null;
	}

	public static interface SizerFactory {
		Size get(World world, BlockPos pos, Axis axis);
	}

	public static final SizerFactory STANDARD_SIZER = new SizerFactory() {
		@Override
		public Size get(World world, BlockPos pos, Axis axis) {
			return new BlockTeletoryPortal.Size(world, pos, axis);
		}
	};

	public static final SizerFactory LINKED_SIZER = new SizerFactory() {
		@Override
		public Size get(World world, BlockPos pos, Axis axis) {
			return new BlockLinkedTeletoryPortal.Size(world, pos, axis);
		}
	};

	public static class ControlBlockLocation {
		public Axis axis;
		public BlockPos pos;
	}

	public static class PortalLinkerOrigin {
		public BlockPos pos;
		public int dimId;
		public int side;
	}

}
