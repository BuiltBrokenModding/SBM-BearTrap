package net.spectre.beartrap.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.spectre.beartrap.Traps;
import net.spectre.beartrap.items.BItems;

@Mod.EventBusSubscriber(modid = Traps.MODID)
public class BBlocks {

	private static List<Block> BLOCKS = new ArrayList<Block>();
	
	public static Block bear_trap = register(new BlockBearTrap(), "bear_trap");
	
	public static Block register(Block block, String name) {
		ResourceLocation loc = new ResourceLocation(Traps.MODID, name);
		block.setRegistryName(loc);
		block.setTranslationKey(Traps.MODID + "." + name);
		BItems.ITEMS.add(new ItemBlock(block).setRegistryName(loc));
		BLOCKS.add(block);
		return block;
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		for(Block block : BLOCKS) {
			event.getRegistry().register(block);
		}
	}
	
	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event) {
		for(Block block : BLOCKS) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "normal"));
		}
	}

	public static void register() {}
}
