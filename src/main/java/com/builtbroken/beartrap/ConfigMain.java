package com.builtbroken.beartrap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.Set;

@Config(modid = BearTrap.DOMAIN)
@EventBusSubscriber(modid = BearTrap.DOMAIN)
@Config.LangKey("config." + BearTrap.PREFIX + "title")
public class ConfigMain
{

    @Config.LangKey("config." + BearTrap.PREFIX + "teleport")
    @Config.Comment("Allow the player to teleport out of traps?")
    @Config.Name("allow_teleport")
    public static boolean allowPlayerTeleportation = true;

    @Config.LangKey("config." + BearTrap.PREFIX + "hardness")
    @Config.Comment("Sets the hardness of the trap block")
    @Config.Name("hardness")
    public static float hardness = 5F;

    @Config.LangKey("config." + BearTrap.PREFIX + "trapped.timer")
    @Config.Comment("Sets the time it takes entities to escape in ticks (20 ticks a second), set to -1 to never allow escape")
    @Config.Name("escape_timer")
    public static int escapeTimer = -1;

    @Config.LangKey("config." + BearTrap.PREFIX + "blacklist.entity")
    @Config.Comment("Registry names of entities that cannot be captured")
    @Config.Name("entity_blacklist")
    public static String[] entityBlackList = {"minecraft:squid"};

    @Config.LangKey("config." + BearTrap.PREFIX + "supported.sticks")
    @Config.Comment("Registry names of items that can work as sticks to trigger bear traps to open")
    @Config.Name("supported_sticks")
    public static String[] supportSticks = {"minecraft:stick"};

    @Config.LangKey("config." + BearTrap.PREFIX + "support.sticks.ore")
    @Config.Comment("Support any item that is registered as a stick in the ore-dictionary")
    @Config.Name("supported_ore_stick_names")
    public static boolean oreDictSticks = true;

    @Config.Ignore()
    private static final Set<ResourceLocation> _entityBlackListCache = new HashSet();
    private static final Set<ResourceLocation> _stickListCache = new HashSet();

    @SubscribeEvent
    public static void onConfigChange(ConfigChangedEvent event)
    {
        if (event.getModID().equals(BearTrap.DOMAIN))
        {
            //Reset cache
            _entityBlackListCache.clear();
            _stickListCache.clear();

            ConfigManager.sync(event.getModID(), Type.INSTANCE);
        }
    }

    public static boolean canTripTrap(ItemStack stack)
    {
        //Generate cache
        if (_stickListCache.isEmpty())
        {
            //handle user configs
            for (String s : supportSticks)
            {
                if (!s.trim().isEmpty())
                {
                    final ResourceLocation regName = new ResourceLocation(s);
                    final Item item = ForgeRegistries.ITEMS.getValue(regName);
                    if (item != null && item != Items.AIR)
                    {
                        _stickListCache.add(regName);
                    }
                    else
                    {
                        BearTrap.logger.error("Failed to locate item by name for supported stick list. Name = '" + s + "'");
                    }
                }
                else
                {
                    BearTrap.logger.error("Failed to locate item by name for supported stick list. Name = '" + s + "'");
                }
            }

            if (oreDictSticks)
            {
                //Handle ore-names
                final NonNullList<ItemStack> sticks = OreDictionary.getOres("stickWood");
                for (ItemStack itemStack : sticks)
                {
                    if (!itemStack.isEmpty())
                    {
                        _stickListCache.add(itemStack.getItem().getRegistryName());
                    }
                }
            }
        }

        //Check
        if (_stickListCache.contains(stack.getItem().getRegistryName()))
        {
            return true;
        }
        return false;
    }

    /**
     * Can the entity be trapped
     *
     * @param entity
     * @return
     */
    public static boolean canTrap(Entity entity)
    {
        final ResourceLocation entityRegName = EntityList.getKey(entity);
        if (entityRegName != null)
        {
            //Generate cache
            if (_entityBlackListCache.isEmpty() && entityBlackList.length > 0)
            {
                for (String s : entityBlackList)
                {
                    if (!s.trim().isEmpty())
                    {
                        ResourceLocation regName = new ResourceLocation(s.trim());
                        if (ForgeRegistries.ENTITIES.getValue(regName) != null)
                        {
                            _entityBlackListCache.add(regName);
                        }
                        else
                        {
                            BearTrap.logger.error("Failed to locate entity by name for blacklist. Name = '" + s + "'");
                        }
                    }
                    else
                    {
                        BearTrap.logger.error("Failed to locate entity by name for blacklist. Name = '" + s + "'");
                    }
                }
            }

            //Check if banned
            if (_entityBlackListCache.contains(entityRegName))
            {
                return false;
            }
        }
        return true;
    }
}
