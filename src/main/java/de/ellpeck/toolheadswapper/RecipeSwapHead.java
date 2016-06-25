package de.ellpeck.toolheadswapper;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeSwapHead implements IRecipe{

    private static final String[] COMPARE_BASE = new String[]{"Pickaxe", "Axe", "Shovel", "Sword", "Hoe"};
    private static final Map<String, Integer> COMPARE = new HashMap<String, Integer>();

    static{
        for(int i = 0; i < COMPARE_BASE.length; i++){
            String s = COMPARE_BASE[i];
            //Make it ignore things like "Paxel" because it'd need Axe, _axe or axe_
            COMPARE.put(s, i);

            String sLower = s.toLowerCase(Locale.ROOT);
            COMPARE.put("_"+sLower, i);
            COMPARE.put(sLower+"_", i);
        }
    }

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
                if(stack != null && stack.getItem() != null){
                    boolean isTool = stack.getItem().isRepairable();
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
        for(Item newItem : ToolHeadSwapper.ALL_TOOLS){
            if(newItem != oldStack.getItem()){
                if(newItem.getMaxDamage() >= oldStack.getItem().getMaxDamage()){
                    int newDamage = 0;
                    if(ToolHeadSwapper.keepDurability){
                        int damageTaken = oldStack.getMaxDamage()-oldStack.getItemDamage();
                        newDamage = newItem.getMaxDamage()-damageTaken;
                    }

                    ItemStack newStack = new ItemStack(newItem, 1, newDamage);
                    if(newItem.getIsRepairable(newStack, repair) && areSameToolType(oldStack, newStack)){
                        return newStack;
                    }
                }
            }
        }
        return null;
    }

    private static boolean areSameToolType(ItemStack stack1, ItemStack stack2){
        Set<String> toolClasses1 = stack1.getItem().getToolClasses(stack1);
        Set<String> toolClasses2 = stack2.getItem().getToolClasses(stack2);
        if(toolClasses1 != null && toolClasses2 != null && !toolClasses1.isEmpty() && toolClasses1.equals(toolClasses2)){
            return true;
        }
        else{
            //This is a hacky workaround for mods that don't specify tool classes
            String reg1 = stack1.getItem().getRegistryName().toString();
            String reg2 = stack2.getItem().getRegistryName().toString();

            int compared1 = -1;
            int compared2 = -1;
            for(Map.Entry<String, Integer> s : COMPARE.entrySet()){
                if(reg1.contains(s.getKey())){
                    compared1 = s.getValue();
                }
                if(reg2.contains(s.getKey())){
                    compared2 = s.getValue();
                }

                if(compared1 >= 0 && compared2 >= 0){
                    return compared1 == compared2;
                }
            }
        }
        return false;
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
