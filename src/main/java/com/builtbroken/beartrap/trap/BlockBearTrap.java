package com.builtbroken.beartrap.trap;

import com.builtbroken.beartrap.BearTrap;
import com.builtbroken.beartrap.ConfigMain;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockBearTrap extends Block implements ITileEntityProvider
{
    public static PropertyEnum<State> OPEN = PropertyEnum.create("open", State.class);

    public BlockBearTrap()
    {
        super(Material.IRON);
        this.setRegistryName(BearTrap.PREFIX + "trap");
        this.setTranslationKey(BearTrap.PREFIX + "trap");
        this.setCreativeTab(CreativeTabs.COMBAT);
        this.setDefaultState(getDefaultState().withProperty(OPEN, State.OPEN));
        this.setHardness(ConfigMain.hardness);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        int openState = meta / 4; // 0 open, 1 open-set, 2 closed
        int facing = meta % 4; //0-3

        return this.getDefaultState()
                .withProperty(BlockHorizontal.FACING, EnumFacing.byHorizontalIndex(facing))
                .withProperty(OPEN, State.get(openState));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(BlockHorizontal.FACING).getHorizontalIndex();
        int isOpen = state.getValue(OPEN).ordinal();
        return facing + (isOpen * 4);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, BlockHorizontal.FACING, OPEN);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().rotateY());
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return new AxisAlignedBB(0, 0, 0, 1, 0.1, 1);
    }

    //So mobs will step on it.
    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return true;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return getCollisionBoundingBox(state, source, pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        final TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBearTrap)
        {
            TileEntityBearTrap trap = (TileEntityBearTrap) tileEntity;
            final ItemStack heldItem = playerIn.getHeldItem(hand);
            if (!heldItem.isEmpty() && ConfigMain.canTripTrap(heldItem))
            {
                trap.openTrap();
            }
            else if(trap.isOpen())
            {
                trap.setupTrap();
            }
        }
        return true;
    }

    //Traps an entity when they step on it
    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        final TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBearTrap)
        {
            final TileEntityBearTrap trap = (TileEntityBearTrap) tileEntity;
            if (trap.canTrapEntity(entityIn))
            {
                trap.setTrappedEntity(entityIn);
            }
        }
        super.onEntityCollision(worldIn, pos, state, entityIn);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityBearTrap();
    }

    public static enum State implements IStringSerializable
    {
        OPEN,
        OPEN_SET,
        CLOSED;

        public static State get(int meta)
        {
            if(meta > 0 && meta < values().length)
            {
                return values()[meta];
            }
            return OPEN;
        }


        @Override
        public String getName()
        {
            return super.name().toLowerCase();
        }
    }
}
