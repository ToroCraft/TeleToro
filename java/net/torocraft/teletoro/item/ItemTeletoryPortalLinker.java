package net.torocraft.teletoro.item;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.blocks.BlockAbstractPortal.Size;
import net.torocraft.teletoro.blocks.BlockLinkedTeletoryPortal;
import net.torocraft.teletoro.blocks.BlockTeletoryPortal;
import net.torocraft.teletoro.blocks.TileEntityLinkedTeletoryPortal;

public class ItemTeletoryPortalLinker extends Item {

	public static ItemTeletoryPortalLinker INSTANCE;

	public static final String NAME = "teletory_portal_linker";

	public static void init() {
		INSTANCE = new ItemTeletoryPortalLinker();
		ResourceLocation resourceName = new ResourceLocation(TeleToro.MODID, NAME.toLowerCase());
		GameRegistry.register(INSTANCE, resourceName);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(INSTANCE, 0, model);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.getItemModelMesher().register(INSTANCE, 0, new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory"));
	}

	public ItemTeletoryPortalLinker() {
		setUnlocalizedName(NAME);
		this.maxStackSize = 16;
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

		if (stack == null || stack.getItem() != INSTANCE) {
			System.out.println("incorrect item used");
			return;
		}

		ControlBlockLocation thisPortal = findControllerBlock(world, pos, STANDARD_SIZER);

		if (thisPortal == null || thisPortal.pos == null) {
			System.out.println("unable to the location of this portal");
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
		System.out.println("attempting to link the two portals");

		ControlBlockLocation remotePortal = findControllerBlock(world, remoteInfo.pos, STANDARD_SIZER);

		stack.setTagInfo("origin", new NBTTagLong(0));
		stack.setTagInfo("dimid", new NBTTagInt(0));
		
		if (remotePortal == null) {
			System.out.println("remote portal not found, exiting");
			return;
		}

		linkPortalTo(world, thisPortal, remotePortal, remoteInfo.dimId, remoteInfo.side);
		linkPortalTo(world, remotePortal, thisPortal, player.dimension, getSide(player, thisPortal));
	}

	private void setOriginPortal(EntityPlayer player, ItemStack stack, ControlBlockLocation thisPortal) {
		stack.setTagInfo("origin", new NBTTagLong(thisPortal.pos.toLong()));
		stack.setTagInfo("dimid", new NBTTagInt(player.dimension));

		int side = getSide(player, thisPortal);

		stack.setTagInfo("side", new NBTTagInt(side));

		if (!player.world.isRemote) {
			System.out
					.println("player[" + player.getPosition() + "] portal[" + thisPortal.pos + "] axis[" + thisPortal.axis + "] side[" + side + "]");
		}

		// TODO play sound and particle effects

		// TODO linker item should be visible different

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

	private void linkPortalTo(World world, ControlBlockLocation from, ControlBlockLocation to, int remoteDimId, int remoteSide) {
		System.out.println("link " + from.pos + " to " + to.pos);

		Size size = STANDARD_SIZER.get(world, from.pos, from.axis);
		size.placePortalBlocks(BlockLinkedTeletoryPortal.INSTANCE);

		if (world.isRemote) {
			return;
		}

		System.out.println("placing title entity at " + from.pos);
		TileEntityLinkedTeletoryPortal te = new TileEntityLinkedTeletoryPortal();
		te.setDimId(remoteDimId);
		te.setDestination(to.pos);
		te.setSide(remoteSide);
		world.setTileEntity(from.pos, te);
	}

	private PortalLinkerOrigin getLinkOrigin(ItemStack stack) {
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
