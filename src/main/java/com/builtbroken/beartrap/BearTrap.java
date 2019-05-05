package com.builtbroken.beartrap;

import com.builtbroken.beartrap.trap.BlockBearTrap;
import com.builtbroken.beartrap.trap.TileEntityBearTrap;
import com.builtbroken.beartrap.trap.TrapSyncPacket;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

@Mod(modid = BearTrap.DOMAIN, name = "[SBM] Bear Trap", version = BearTrap.VERSION)
@Mod.EventBusSubscriber(modid = BearTrap.DOMAIN)
public class BearTrap
{
    public static final String DOMAIN = "sbmbeartrap";
    public static final String PREFIX = DOMAIN + ":";

    public static final String MAJOR_VERSION = "@MAJOR@";
    public static final String MINOR_VERSION = "@MINOR@";
    public static final String REVISION_VERSION = "@REVIS@";
    public static final String BUILD_VERSION = "@BUILD@";
    public static final String VERSION = MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION + "." + BUILD_VERSION;

    public static BlockBearTrap blockBearTrap;

    public static Logger logger;

    public static SimpleNetworkWrapper NETWORK;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(DOMAIN);
        NETWORK.registerMessage(TrapSyncPacket.Handler.class, TrapSyncPacket.class, 0, Side.CLIENT);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(blockBearTrap = new BlockBearTrap());
        GameRegistry.registerTileEntity(TileEntityBearTrap.class, new ResourceLocation(DOMAIN, "trap"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(blockBearTrap).setRegistryName(blockBearTrap.getRegistryName()));
    }
}
