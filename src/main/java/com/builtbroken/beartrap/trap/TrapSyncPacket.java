package com.builtbroken.beartrap.trap;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class TrapSyncPacket implements IMessage
{
    private int dim;
    private BlockPos pos;

    private int trappedEntityID;

    //Toggle states
    private BlockBearTrap.State openState;

    //Timers
    private int cooldown = 0;
    private int trappedTicks = 0;

    public TrapSyncPacket()
    {
        //Needed for packet creation
    }

    public TrapSyncPacket(TileEntityBearTrap trap)
    {
        dim = trap.getWorld().provider.getDimension();
        pos = trap.getPos();

        trappedEntityID = trap.trappedEntityID;
        openState = trap.openState;
        cooldown = trap.cooldown;
        trappedTicks = trap.trappedTicks;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(dim);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());

        buf.writeInt(trappedEntityID);
        buf.writeInt(cooldown);
        buf.writeInt(trappedTicks);
        buf.writeInt(openState.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        dim = buf.readInt();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        trappedEntityID = buf.readInt();
        cooldown = buf.readInt();
        trappedTicks = buf.readInt();
        openState = BlockBearTrap.State.get(buf.readInt());
    }

    public static class Handler implements IMessageHandler<TrapSyncPacket, IMessage>
    {
        @Override
        public IMessage onMessage(TrapSyncPacket message, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final World world = Minecraft.getMinecraft().world;
                if (world.provider.getDimension() == message.dim)
                {
                    TileEntity tile = world.getTileEntity(message.pos);
                    if (tile instanceof TileEntityBearTrap)
                    {
                        ((TileEntityBearTrap) tile).trappedEntityID = message.trappedEntityID;
                        ((TileEntityBearTrap) tile).cooldown = message.cooldown;
                        ((TileEntityBearTrap) tile).trappedTicks = message.trappedTicks;
                        ((TileEntityBearTrap) tile).openState = message.openState;
                    }
                }
            });
            return null;
        }
    }
}