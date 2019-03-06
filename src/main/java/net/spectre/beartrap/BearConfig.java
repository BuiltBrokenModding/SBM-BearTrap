package net.spectre.beartrap;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Config(modid = Traps.MODID)
public class BearConfig {

	@Config.RangeDouble(min = 0D, max = 99D)
	@Config.Comment("How much damage a second to do in a trap, none if 0")
	public static double bleedDamage = 0D;
	
	
	@EventBusSubscriber(modid = Traps.MODID)
	public static class Events{
		
		public static void onConfigChaged(ConfigChangedEvent event) {
			if(event.getModID().equals(Traps.MODID)) {
				ConfigManager.sync(event.getModID(), Type.INSTANCE);
			}
		}
	}
}
