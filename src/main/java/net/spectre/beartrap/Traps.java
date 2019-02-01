package net.spectre.beartrap;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.spectre.beartrap.tileentity.TileEntityBearTrap;

@Mod(modid = Traps.MODID, name = Traps.NAME, version = Traps.VERSION)
public class Traps
{
    public static final String MODID = "beartrap";
    public static final String NAME = "Bear Trap";
    public static final String VERSION = "1.0";

    //Things that can pry off traps of people's legs
    public static List<ResourceLocation> STICKS = new ArrayList<ResourceLocation>();
    
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        GameRegistry.registerTileEntity(TileEntityBearTrap.class, new ResourceLocation(MODID, "bear_trap"));
        readConfig(event.getModConfigurationDirectory());
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        
    }
    
    public static void readConfig(File f) {
    	f = new File(f.getPath() + "/bearTrapPryItems.json");
    	try {
    		if(f.exists()) {
    			JsonReader read = new GsonBuilder().create().newJsonReader(new FileReader(f));
    			read.beginArray();
    			while(read.hasNext()) {
    				STICKS.add(new ResourceLocation(read.nextString()));
    			}
    			read.endArray();
    			read.close();
    		}
    		else {
    			f.createNewFile();
    			Gson gson = new GsonBuilder().setPrettyPrinting().create();
    			JsonWriter wr = gson.newJsonWriter(new FileWriter(f));
    			wr.beginArray();
    			wr.value("minecraft:stick");
    			wr.endArray();
    			wr.close();
    			STICKS.add(Items.STICK.getRegistryName());
    		}
    	}
    	catch(Exception e) {}
    	System.out.println(f);
    }
    
}
