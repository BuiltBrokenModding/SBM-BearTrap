package net.spectre.beartrap.sounds;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.spectre.beartrap.Traps;

@Mod.EventBusSubscriber(modid = Traps.MODID)
public class TSounds {
	
	public static SoundEvent TRAP;
	public static SoundEvent RESET;
	
	public static SoundEvent register(String name) {
		ResourceLocation loc = new ResourceLocation(Traps.MODID, name);
		SoundEvent event = new SoundEvent(loc);
		event.setRegistryName(loc);
		return event;
	}
	
	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().register(TRAP = register("trap"));
		event.getRegistry().register(RESET = register("reset"));
	}

}
