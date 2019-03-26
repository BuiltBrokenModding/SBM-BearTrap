package net.spectre.beartrap.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.spectre.beartrap.Traps;
import net.spectre.beartrap.tileentity.TileEntityBearTrap;

@Mod.EventBusSubscriber(modid = Traps.MODID, value = Side.CLIENT)
public class EventHandler {
	
	@SubscribeEvent
	public static void register(InputUpdateEvent event) {
		TileEntity te = event.getEntityPlayer().world.getTileEntity(event.getEntityPlayer().getPosition());
		if(te != null && te instanceof TileEntityBearTrap) {
			if(!((TileEntityBearTrap)te).isSet()) {
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
