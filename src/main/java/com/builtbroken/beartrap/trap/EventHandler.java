package com.builtbroken.beartrap.trap;

import com.builtbroken.beartrap.BearTrap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = BearTrap.DOMAIN, value = Side.CLIENT)
public class EventHandler
{

    @SubscribeEvent
    public static void register(InputUpdateEvent event)
    {
        final TileEntity tileEntity = event.getEntityPlayer().world.getTileEntity(event.getEntityPlayer().getPosition());
        if (tileEntity instanceof TileEntityBearTrap)
        {
            final TileEntityBearTrap trap = (TileEntityBearTrap) tileEntity;
            if (!trap.isOpen() && trap.trappedEntity == event.getEntityPlayer())
            {
                MovementInput input = event.getMovementInput();
                input.backKeyDown = false;
                input.forwardKeyDown = false;
                input.jump = false;
                input.leftKeyDown = false;
                input.rightKeyDown = false;
                input.moveForward = input.moveStrafe = 0;
            }
        }
    }

}
