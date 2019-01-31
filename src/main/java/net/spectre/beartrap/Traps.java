package net.spectre.beartrap;

import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.spectre.beartrap.blocks.BBlocks;
import net.spectre.beartrap.items.BItems;
import net.spectre.beartrap.tileentity.TileEntityBearTrap;

@Mod(modid = Traps.MODID, name = Traps.NAME, version = Traps.VERSION)
public class Traps
{
    public static final String MODID = "beartrap";
    public static final String NAME = "Bear Trap";
    public static final String VERSION = "1.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        GameRegistry.registerTileEntity(TileEntityBearTrap.class, new ResourceLocation(MODID, "bear_trap"));
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
    }
}
