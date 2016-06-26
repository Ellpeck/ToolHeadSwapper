/*
 * This file ("ToolHeadSwapper.java") is part of the Tool Head Swapper mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Tool Head Swapper License to be found at
 * https://github.com/Ellpeck/ToolHeadSwapper/blob/master/LICENSE.md
 * View the source code at https://github.com/Ellpeck/ToolHeadSwapper
 *
 * © 2016 Ellpeck
 */

package de.ellpeck.toolheadswapper;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = ToolHeadSwapper.MOD_ID, name = ToolHeadSwapper.NAME, version = ToolHeadSwapper.VERSION)
public class ToolHeadSwapper{

    public static final String MOD_ID = "toolheadswapper";
    public static final String NAME = "Tool Head Swapper";
    public static final String VERSION = "@VERSION@";

    public static final List<Item> ALL_TOOLS = new ArrayList<Item>();
    private static final List<Item> EXCEPTIONS = new ArrayList<Item>();
    public static boolean keepDurability;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event){
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();

        keepDurability = config.get(Configuration.CATEGORY_GENERAL, "keepDurability", true, "If tools that are created via head swapping should keep the durability of the original tool. When turned off, newly created tools will have full durability.").getBoolean();

        String[] exceptionNames = config.get(Configuration.CATEGORY_GENERAL, "exceptions", new String[0], "Registry names of tools that should not be able to be created via head swapping.").getStringList();
        for(String name : exceptionNames){
            Item item = Item.REGISTRY.getObject(new ResourceLocation(name));
            if(item != null){
                EXCEPTIONS.add(item);
            }
        }

        if(config.hasChanged()){
            config.save();
        }
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event){
        for(Item item : Item.REGISTRY){
            if(!EXCEPTIONS.contains(item)){
                if(item.isRepairable()){
                    ALL_TOOLS.add(item);
                }
            }
        }

        GameRegistry.addRecipe(new RecipeSwapHead());
        RecipeSorter.register(MOD_ID, RecipeSwapHead.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }
}
