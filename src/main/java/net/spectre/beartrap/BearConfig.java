package net.spectre.beartrap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Traps.MODID)
public class BearConfig {

	@Config.LangKey("config." + Traps.MODID + ".teleport")
	@Config.Comment("Allow the player to teleport out of traps?")
	public static boolean teleport = true;
	
	@Config.LangKey("config." + Traps.MODID + ".hardness")
	@Config.Comment("Sets the hardness of the trap block")
	public static float hardness = 5F;
	
	@Config.LangKey("config." + Traps.MODID + ".trapped")
	@Config.Comment("Sets the time it takes entities to escape (-1 means never)")
	public static int timeToEscape = -1;
	
	@Config.LangKey("config." + Traps.MODID + ".blacklist")
	@Config.Comment("Registry names of entities that cannot be captured")
	public static String[] BLACKLISTED_IDs = {};
	
	@EventBusSubscriber(modid = Traps.MODID)
	public static class Events{
		
		@SubscribeEvent
		public static void onConfigChaged(ConfigChangedEvent event) {
			if(event.getModID().equals(Traps.MODID)) {
				ConfigManager.sync(event.getModID(), Type.INSTANCE);
			}
		}
	}
	
	public static boolean canTrap(Entity entity) {
		ResourceLocation loc = EntityList.getKey(entity);
		if(loc != null) {
			for(String id : BLACKLISTED_IDs) {
				if(loc.toString().equals(id))
					return false;
			}
		}
		return true;
	}
}
