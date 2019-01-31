package net.spectre.beartrap.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.spectre.beartrap.Traps;

@Mod.EventBusSubscriber(modid = Traps.MODID)
public class BItems {

	public static List<Item> ITEMS = new ArrayList<Item>();
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		for(Item item : ITEMS) {
			event.getRegistry().register(item);
		}
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for(Item item : ITEMS) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		}
	}

	public static void register() {}
}
