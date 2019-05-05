package com.builtbroken.beartrap;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by Dark(DarkGuardsman, Robert) on 5/5/2019.
 */
@Mod.EventBusSubscriber(modid = BearTrap.DOMAIN, value = Side.CLIENT)
public class ClientReg
{
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BearTrap.blockBearTrap), 0, new ModelResourceLocation(BearTrap.blockBearTrap.getRegistryName(), "normal"));
    }
}
