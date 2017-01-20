package net.torocraft.teletoro.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.torocraft.teletoro.TeleToro;
import net.torocraft.teletoro.item.ItemTeletoryPortalLinker;
import net.torocraft.teletoro.item.ItemTeletoryPortalLinker.ControlBlockLocation;

//TODO create linked portals, break one, the other still works

//TODO figure out player placement (point player towards the side that was clicked on when linking)

//TODO when a linked portal collapses, break the remote too

public class BlockLinkedTeletoryPortal extends BlockAbstractPortal implements ITileEntityProvider {

	public static BlockLinkedTeletoryPortal INSTANCE;

	public static Item ITEM_INSTANCE;

	public static final String NAME = "linkedteletoryportalblock";

	public static void init() {
		INSTANCE = (BlockLinkedTeletoryPortal) new BlockLinkedTeletoryPortal().setUnlocalizedName(NAME);
		ResourceLocation resourceName = new ResourceLocation(TeleToro.MODID, NAME);
		INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(INSTANCE);

		ITEM_INSTANCE = new ItemBlock(INSTANCE);
		ITEM_INSTANCE.setRegistryName(resourceName);
		GameRegistry.register(ITEM_INSTANCE);
	}

	public static void registerRenders() {
		ModelResourceLocation model = new ModelResourceLocation(TeleToro.MODID + ":" + NAME, "inventory");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ITEM_INSTANCE, 0, model);
	}
	
	public BlockLinkedTeletoryPortal () {
		 this.isBlockContainer = true;
	}

	@Override
	public Size getSizer(World worldIn, BlockPos p_i45694_2_, Axis p_i45694_3_) {
		return new BlockLinkedTeletoryPortal.Size(worldIn, p_i45694_2_, p_i45694_3_);
	}

	@Override
	protected void onPlayerEnterPortal(EntityPlayerMP player, BlockPos pos) {
		teleport(player, pos);
	}

	private void teleport(EntityPlayerMP player, BlockPos thisPortalLocation) {
		ControlBlockLocation thisPortal = ItemTeletoryPortalLinker.findControllerBlock(player.world, thisPortalLocation, ItemTeletoryPortalLinker.LINKED_SIZER);

		if (thisPortal == null) {
			//TODO break; 
			
			System.out.println("this portal is null");
			return;
		}

		TileEntityLinkedTeletoryPortal te = getTileEntity(player, thisPortal.pos);

		if (te == null) {
			breakPortal(player, thisPortalLocation);
			System.out.println("te is null");
			return;
		}

		if (te.getDimId() != player.dimension) {
			// TODO support change dimension to specific place
			// Teletory.changeEntityDimension(player, TeleportorType.PORTAL);
		
			breakPortal(player, thisPortalLocation);
			System.out.println("Handle inter dimensional links ... not currenlty supported");
			return;
		}
		
		if(te.getDestination() == null){
			
			breakPortal(player, thisPortalLocation);
			System.out.println("detination not set");
			return;
		}

		// find remote control block and verify portal

		ControlBlockLocation remotePortal = ItemTeletoryPortalLinker.findControllerBlock(player.world, te.getDestination(), ItemTeletoryPortalLinker.LINKED_SIZER);

		if (remotePortal == null) {
			System.out.println("linked remote portal not found");
			breakPortal(player, thisPortalLocation);
			return;
		}

		System.out.println("teleporting to " + remotePortal.pos);

		// TODO correct yaw based on facing of remote portal
		float yaw;
		if(Axis.X.equals( remotePortal.axis)){
			if(te.getSide() == 1){
				yaw = 0;
			}else{
				yaw = 180;
			}
		}else{
			if(te.getSide() == 1){
				yaw = -90;
			}else{
				yaw = 90;
			}
		}
		
		
		player.connection.setPlayerLocation(remotePortal.pos.getX() + 0.5, remotePortal.pos.getY(), remotePortal.pos.getZ() + 0.5, yaw,
				player.rotationPitch);

		// TODO support other entities
		// entityIn.setLocationAndAngles(d5, d6, d7, entityIn.rotationYaw,
		// entityIn.rotationPitch);

		// slap the player
	}

	private void breakPortal(EntityPlayerMP player, BlockPos thisPortalLocation) {
		player.world.setBlockState(thisPortalLocation, Blocks.AIR.getDefaultState());
	}

	private TileEntityLinkedTeletoryPortal getTileEntity(EntityPlayerMP player, BlockPos pos) {
		try {
			return (TileEntityLinkedTeletoryPortal) player.world.getTileEntity(pos);
		} catch (Exception e) {
			return null;
		}
	}


	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return null;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		collapseRemotePortal(world, world.getTileEntity(pos));
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	private void collapseRemotePortal(World world, TileEntity te) {
		
		System.out.println("attempt to collapse remote");
		
		if(te == null || !(te instanceof TileEntityLinkedTeletoryPortal)){
			System.out.println("remote te not found");
			return;
		}
		BlockPos remotePos = ((TileEntityLinkedTeletoryPortal)te).getDestination();
		if(remotePos == null){
			System.out.println("remote control block not found");
			return;
		}
		
		if(world.getBlockState(remotePos) == BlockLinkedTeletoryPortal.INSTANCE){
			System.out.println("collapse it");
			world.setBlockState(remotePos, Blocks.AIR.getDefaultState());
		}else{
			System.out.println("remote location is not a portal");
		}
		
	}

	/**
	 * Called on both Client and Server when World#addBlockEvent is called. On
	 * the Server, this may perform additional changes to the world, like
	 * pistons replacing the block with an extended base. On the client, the
	 * update may involve replacing tile entities, playing sounds, or performing
	 * other visual actions to reflect the server side changes.
	 */
	@SuppressWarnings("deprecation")
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	public static class Size extends BlockAbstractPortal.Size {

		public Size(World world, BlockPos pos, Axis axis) {
			super(world, pos, axis);
		}

		@Override
		public Block getFrameBlock() {
			return BlockEnder.INSTANCE;
		}
		
		@Override
		public BlockAbstractPortal getPortalBlock() {
			return INSTANCE;
		}

	}

}