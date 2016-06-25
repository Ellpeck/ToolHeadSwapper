package de.ellpeck.toolheadswapper;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = ToolHeadSwapper.MOD_ID, name = ToolHeadSwapper.NAME, version = ToolHeadSwapper.VERSION)
public class ToolHeadSwapper{

    public static final String MOD_ID = "toolheadswapper";
    public static final String NAME = "Tool Head Swapper";
    public static final String VERSION = "@VERSION@";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    //new tool, swap item
    public static final Map<ItemTool, ItemStack> ALL_TOOLS = new HashMap<ItemTool, ItemStack>();

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent event){
        for(Item item : Item.REGISTRY){
            if(item instanceof ItemTool){
                ItemTool tool = (ItemTool)item;
                Item.ToolMaterial material = tool.getToolMaterial();
                if(material != null){
                    ItemStack repairStack = material.getRepairItemStack();
                    if(repairStack != null){
                        ALL_TOOLS.put(tool, repairStack);
                    }
                }
            }
        }

        LOGGER.info("Found "+ALL_TOOLS.size()+" tools for conversion recipes!");

        GameRegistry.addRecipe(new RecipeSwapHead());
        RecipeSorter.register(MOD_ID, RecipeSwapHead.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }
}
