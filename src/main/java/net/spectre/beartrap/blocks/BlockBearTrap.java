package net.spectre.beartrap.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.spectre.beartrap.BearConfig;
import net.spectre.beartrap.Traps;
import net.spectre.beartrap.tileentity.TileEntityBearTrap;

public class BlockBearTrap extends Block{

	public static PropertyBool OPEN = PropertyBool.create("open");
	public static final AxisAlignedBB BB = new AxisAlignedBB(0, 0, 0, 1, 0.1, 1);
	
	public BlockBearTrap() {
		super(Material.IRON);
		this.setCreativeTab(CreativeTabs.COMBAT);
		this.setDefaultState(this.getDefaultState().withProperty(OPEN, true));
		this.setHardness(BearConfig.hardness);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return this.getDefaultState().getValue(BlockHorizontal.FACING).getHorizontalIndex();
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BlockHorizontal.FACING, OPEN);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityBearTrap();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().rotateY());
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return BB;
	}

	//So mobs will step on it.
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BB;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntityBearTrap trap = (TileEntityBearTrap)worldIn.getTileEntity(pos);
		if(trap != null && Traps.STICKS.contains(playerIn.getHeldItem(hand).getItem().getRegistryName())) {
			trap.setSet(true);
		}
		return true;
	}

	//Traps an entity when they step on it
	@Override
	public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		TileEntityBearTrap trap = (TileEntityBearTrap)worldIn.getTileEntity(pos);
		if(BearConfig.canTrap(entityIn) && trap != null && trap.isSet() && trap.getCooldown() <= 0) {
			trap.setTrappedEntity(entityIn);
		}
		super.onEntityCollision(worldIn, pos, state, entityIn);
	}

}
