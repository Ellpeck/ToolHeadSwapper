package de.ellpeck.toolheadswapper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.Set;

public class RecipeSwapHead implements IRecipe{

    @Override
    public boolean matches(InventoryCrafting inv, World world){
        return getOutputFromInputs(inv, false) != null;
    }

    private static ItemStack getOutputFromInputs(InventoryCrafting inv, boolean breakWhenFound){
        ItemStack oldTool = null;
        ItemStack newTool = null;
        boolean foundSwap = false;

        for(int iterate = 0; iterate < 2; iterate++){
            for(int i = 0; i < inv.getSizeInventory(); i++){
                ItemStack stack = inv.getStackInSlot(i);
                if(stack != null){
                    boolean isTool = stack.getItem() instanceof ItemTool;
                    if(iterate == 0){
                        //Find the tool first
                        if(isTool){
                            if(oldTool == null){
                                oldTool = stack;
                            }
                            else{
                                return null;
                            }
                        }
                    }
                    else if(!isTool){
                        //Try to find fitting swap material after
                        if(oldTool == null){
                            return null;
                        }
                        else{
                            newTool = getNewTool(oldTool, stack);
                            if(newTool != null){
                                if(breakWhenFound){
                                    return newTool;
                                }
                                else{
                                    if(!foundSwap){
                                        foundSwap = true;
                                    }
                                    else{
                                        return null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return foundSwap ? newTool : null;
    }

    @Nullable
    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv){
        return getOutputFromInputs(inv, false);
    }

    private static ItemStack getNewTool(ItemStack oldStack, ItemStack repair){
        Set<String> toolClasses = oldStack.getItem().getToolClasses(oldStack);
        if(toolClasses != null && !toolClasses.isEmpty()){
            for(ItemTool newItem : ToolHeadSwapper.ALL_TOOLS.keySet()){
                if(newItem != oldStack.getItem()){
                    if(newItem.getMaxDamage() >= oldStack.getItem().getMaxDamage()){
                        Item.ToolMaterial newMaterial = newItem.getToolMaterial();
                        if(newMaterial != null){
                            if(OreDictionary.itemMatches(newMaterial.getRepairItemStack(), repair, false)){
                                int damageTaken = oldStack.getMaxDamage()-oldStack.getItemDamage();
                                ItemStack newStack = new ItemStack(newItem, 1, newItem.getMaxDamage()-damageTaken);
                                Set<String> newToolClasses = newItem.getToolClasses(newStack);
                                if(newToolClasses != null && newToolClasses.equals(toolClasses)){
                                    return newStack;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int getRecipeSize(){
        return 4;
    }

    @Nullable
    @Override
    public ItemStack getRecipeOutput(){
        return null;
    }

    public ItemStack[] getRemainingItems(InventoryCrafting inv){
        ItemStack[] stacks = new ItemStack[inv.getSizeInventory()];

        for(int i = 0; i < stacks.length; ++i){
            stacks[i] = ForgeHooks.getContainerItem(inv.getStackInSlot(i));
        }

        return stacks;
    }
}
