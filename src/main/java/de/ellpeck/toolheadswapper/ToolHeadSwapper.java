package de.ellpeck.toolheadswapper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = ToolHeadSwapper.MOD_ID, name = ToolHeadSwapper.NAME, version = ToolHeadSwapper.VERSION)
public class ToolHeadSwapper{

    public static final String MOD_ID = "toolheadswapper";
    public static final String NAME = "Tool Head Swapper";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static final List<ItemTool> ALL_TOOLS = new ArrayList<ItemTool>();
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
            if(item instanceof ItemTool && !EXCEPTIONS.contains(item)){
                ItemTool tool = (ItemTool)item;
                Item.ToolMaterial material = tool.getToolMaterial();
                if(material != null){
                    ItemStack repairStack = material.getRepairItemStack();
                    if(repairStack != null){
                        ALL_TOOLS.add(tool);
                    }
                }
            }
        }

        LOGGER.info("Found "+ALL_TOOLS.size()+" tools for conversion recipes!");

        GameRegistry.addRecipe(new RecipeSwapHead());
        RecipeSorter.register(MOD_ID, RecipeSwapHead.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }
}
