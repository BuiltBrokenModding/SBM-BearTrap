package com.builtbroken.beartrap.trap;

import com.builtbroken.beartrap.BearTrap;
import com.builtbroken.beartrap.ConfigMain;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.UUID;

public class TileEntityBearTrap extends TileEntity implements ITickable
{
    private static final String NBT_CAN_TRIGGER = "can_trigger";
    private static final String NBT_COOLDOWN = "cooldown";
    private static final String NBT_TRAP_TICKS = "trap_ticks";
    private static final String NBT_ENTITY_ID = "entity_id";

    public Entity trappedEntity;
    public UUID trappedEntityUUID;
    public int trappedEntityID;

    //Toggle states
    public BlockBearTrap.State openState = BlockBearTrap.State.OPEN;
    public boolean syncPacket = false;

    //Timers
    public int cooldown = 0;
    public int trappedTicks = 0;


    @Override
    public void onLoad()
    {
        final IBlockState state = world.getBlockState(pos);
        if (state.getProperties().containsKey(BlockBearTrap.OPEN))
        {
            openState = state.getValue(BlockBearTrap.OPEN);
        }
    }

    @Override
    public void update()
    {
        //Only run if we are triggered
        if(!isOpen())
        {
            //Find entity if null
            if (trappedEntity == null)
            {
                if (!world.isRemote)
                {
                    trappedEntity = ((WorldServer) world).getEntityFromUuid(trappedEntityUUID);
                    if (trappedEntity != null)
                    {
                        trappedEntityID = trappedEntity.getEntityId();
                    }
                }
                else if (trappedEntityID >= 0)
                {
                    trappedEntity = world.getEntityByID(trappedEntityID);
                }
            }

            //Entity is invalid
            if (trappedEntityID <= 0 || trappedEntity == null)
            {
                clearEntity();
            }

            //Run trap logic if we have an entity
            if (!isEmpty())
            {
                //Allow entity to escape, leave trap triggered
                if (trappedTicks <= 0 && ConfigMain.escapeTimer >= 0)
                {
                    clearEntity();
                }
                else
                {
                    if (!trappedEntity.isDead && trappedEntity.world == getWorld())
                    {
                        //Check distance
                        if (trappedEntity.getPosition().distanceSq(this.getPos()) > 0.1)
                        {
                            //Teleportation check
                            if (!ConfigMain.allowPlayerTeleportation || trappedEntity.getPosition().distanceSq(this.getPos()) < 4)
                            {
                                teleportEntity(trappedEntity, this.getPos().getX() + 0.5, this.getPos().getY(), this.getPos().getZ() + 0.5);
                            }
                        }
                    }
                    //Null entity or dead, clear reference and leave trap triggered
                    else
                    {
                        clearEntity();
                    }
                }
            }

            //Timers
            if (getCooldown() > 0)
            {
                --this.cooldown;
            }
            if (this.trappedTicks > 0)
            {
                --this.trappedTicks;
            }
        }

        if (syncPacket)
        {
            BearTrap.NETWORK.sendToAllAround(new TrapSyncPacket(this), new NetworkRegistry.TargetPoint(world.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 64));
        }
    }

    public void clearEntity()
    {
        trappedEntityID = -1;
        trappedEntity = null;
        trappedEntityUUID = null;
        syncPacket = true;
    }

    public boolean canTrapEntity(Entity entity)
    {
        return ConfigMain.canTrap(entity) && this.openState == BlockBearTrap.State.OPEN_SET;
    }

    public void openTrap()
    {
        setOpen(BlockBearTrap.State.OPEN);
        clearEntity();
    }

    public void releaseTrap()
    {
        setOpen(BlockBearTrap.State.CLOSED);
    }

    public void setupTrap()
    {
        setOpen(BlockBearTrap.State.OPEN_SET);
    }

    public boolean isOpen()
    {
        return openState != BlockBearTrap.State.CLOSED;
    }

    /**
     * Sets the trap as open
     *
     * @param state
     */
    public void setOpen(BlockBearTrap.State state)
    {
        if (state != openState)
        {
            this.openState = state;
            this.trappedEntity = null;
            this.trappedTicks = ConfigMain.escapeTimer;
            this.cooldown = 20;

            //Update block
            world.setBlockState(this.getPos(), world.getBlockState(getPos()).withProperty(BlockBearTrap.OPEN, state));

            if (!world.isRemote)
            {
                world.playSound(null, this.getPos(), state != BlockBearTrap.State.CLOSED ? TSounds.RESET : TSounds.TRAP, SoundCategory.BLOCKS, 1F, 1F);
            }
        }
    }

    public void setTrappedEntity(Entity entity)
    {
        this.trappedEntity = entity;
        this.trappedEntityUUID = entity.getPersistentID();
        this.trappedEntityID = entity.getEntityId();
        this.setOpen(BlockBearTrap.State.CLOSED);
    }

    public boolean isEmpty()
    {
        return this.trappedEntity == null;
    }

    public int getCooldown()
    {
        return this.cooldown;
    }

    public static void teleportEntity(Entity entity, double x, double y, double z)
    {
        if (!entity.world.isRemote && entity instanceof EntityPlayer)
        {
            ((EntityPlayerMP) entity).connection.setPlayerLocation(x, y, z, entity.rotationYaw, entity.rotationPitch);
        }
        else
        {
            entity.setPositionAndUpdate(x, y, z);
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return !(newState.getBlock() instanceof BlockBearTrap);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        cooldown = compound.getInteger(NBT_COOLDOWN);
        trappedTicks = compound.getInteger(NBT_TRAP_TICKS);
        if (compound.hasKey(NBT_ENTITY_ID))
        {
            trappedEntityUUID = compound.getUniqueId(NBT_ENTITY_ID);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger(NBT_COOLDOWN, cooldown);
        compound.setInteger(NBT_TRAP_TICKS, trappedTicks);
        if (trappedEntityUUID != null)
        {
            compound.setUniqueId(NBT_ENTITY_ID, trappedEntityUUID);
        }
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }
}
