package net.spectre.beartrap.event;

import net.minecraft.tileentity.TileEntity;
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
		TileEntity te = event.getEntityPlayer().world.getTileEntity(event.getEntityPlayer().getPosition().down());
		if(te != null && te instanceof TileEntityBearTrap) {
			event.getMovementInput().backKeyDown = event.getMovementInput().forwardKeyDown = event.getMovementInput().jump = event.getMovementInput().leftKeyDown = event.getMovementInput().rightKeyDown = false;
		}
	}

}
