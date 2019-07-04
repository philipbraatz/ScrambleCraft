package com.doorfail.scramblecraft.init;

import com.doorfail.scramblecraft.recipe.*;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ModRecipes {

    private static Logger logger;

    public static Map<UUID,ModRecipeRegistry> recipes = new HashMap<>();

    public static ModCraftingManager craftingManager = new ModCraftingManager();

    private static int newIndex =-1;
    private static int oldIndex =-1;
    public static int previousStackSize =222;//big stack number


    public static NonNullList<Ingredient> getItemsAsIngredient(List<Item> ingredients)
    {
        NonNullList<Ingredient> converted =NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        int i =0;
        for (Item it:ingredients) {
            converted.set(i,Ingredient.fromItem(it));
            i++;
        }
        return converted;
    }

    public static List<Item> getIngredientAsItems(NonNullList<Ingredient> recipeItems) {
        List<Item> ingredients =new ArrayList<>();
        for (Ingredient it:recipeItems)
            if(it.getMatchingStacks().length >0)
                ingredients.add(it.getMatchingStacks()[0].getItem());//add recipe as a new itemstack from item
            else
                ingredients.add(Items.AIR);
        return ingredients;
    }
    public static List<ItemStack> getIngredientAsItemStacks(NonNullList<Ingredient> recipeItems) {
        List<ItemStack> ingredients =new ArrayList<>();
        for (Ingredient it:recipeItems)
            if(it.getMatchingStacks().length >0)
                ingredients.add(new ItemStack( it.getMatchingStacks()[0].getItem()));//add recipe as a new itemstack from item
            else
                ingredients.add(ItemStack.EMPTY);
        return ingredients;
    }

    //save hardcoded values
    public static void init() {
        craftingManager.init();
        //removeRecipes(Blocks.CRAFTING_TABLE);

        ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>)ForgeRegistries.RECIPES;
        ArrayList<IRecipe> recipes = Lists.newArrayList(recipeRegistry.getValuesCollection());
        for (IRecipe r : recipes)
        {
            ItemStack output = r.getRecipeOutput();
            if (output.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))// remove crafting table
            {
                recipeRegistry.remove(r.getRegistryName());
                recipeRegistry.register(DummyRecipe.from(r));
            }
        }

        //logger.info("Removed recipe for crafting table");
        GameRegistry.addSmelting(ModItems.RUBY, new ItemStack(ModBlocks.RUBY_BLOCK, 1), 1.5f);
        GameRegistry.addSmelting(ModBlocks.RUBY_BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK, 2), 3.0f);
        GameRegistry.addSmelting(new ItemStack(Items.STICK), new ItemStack(Items.STRING,3),3);
        GameRegistry.addSmelting(new ItemStack(Blocks.OBSIDIAN), new ItemStack(ModItems.OBSIDIAN_INGOT), 0.4F);

        //ScrambleFurnaceRecipes.instance().addRecipe(ModBlocks.RUBY_ORE, new ItemStack(ModItems.RUBY), 0.3f);

    }



    public static void addRecipe(EntityPlayer entityPlayer,ResourceLocation craftingBlock,List<ItemStack> ingredients, ItemStack normalOutput)
    {
        ModCraftingManager.saveRecipe(entityPlayer,craftingBlock,new ModRecipe(craftingBlock ,ingredients,normalOutput));

        ModRecipeRegistry playerRecipes = recipes.get(entityPlayer.getUniqueID());
        playerRecipes.addRecipe(entityPlayer.getUniqueID(),ingredients,normalOutput,craftingBlock);
        recipes.replace(entityPlayer.getUniqueID(),playerRecipes);
    }

    public static List<ItemStack> tryToScramble(UUID playerId,ResourceLocation craftingBlock,InventoryCrafting inventoryCrafting, List<ItemStack> ingredient, boolean search)
    {
        List<ItemStack> newResultStack;
        boolean isSame;
        int overflow = 15;//number of times to try till failure
        int counter = 0;

        ModRecipeRegistry playerRecipes = recipes.get(playerId);

        //loop until unique or till failure
        do {
            isSame = false;

            counter++;
            if(search)
                newIndex = new Random().nextInt(recipes.size());//try to swap
            if (newIndex == oldIndex)
                isSame = true;

            newResultStack = playerRecipes.getRecipeList(playerId,craftingBlock).get(newIndex).checkResult();
            for (ItemStack itemStack:newResultStack
                 )
                for (ItemStack it : ingredient) {
                    //old == new
                    if (itemStack.getItem().getRegistryName() == it.getItem().getRegistryName() ||//the same recipe
                            itemStack == new ItemStack(Items.AIR)//EMPTY!
                    )
                        isSame = true;
                }
        } while (isSame && counter < overflow);

        if(!isSame) {
            //compare every old Item with every new Item
            for (ItemStack itNew: newResultStack)
                for (ItemStack itOld : playerRecipes.getRecipeList(playerId, craftingBlock).get(oldIndex).checkResult())
                    if (ModCraftingManager.areItemsRelated(inventoryCrafting, itNew.getItem(), itOld.getItem()))
                        return tryToScramble(playerId, craftingBlock, inventoryCrafting, ingredient, true);
                    else
                        return newResultStack;
        }
        else
            return new ArrayList<>();//overflowed
        return new ArrayList<>();//Why does java want this
    }

    public static void randomizeRecipe(IInventory craftGrid, EntityPlayer entityPlayer,ResourceLocation craftingBlock, List<ItemStack> ingredient)
    {
        //dont scramble the firt 2 crafts of any recipe
        if(recipes.size() >2)
        {
            oldIndex=ModRecipeRegistry.getMatchIndex(entityPlayer.getUniqueID(),craftingBlock,ingredient);
            List<ModRecipe> recipes = ModRecipeRegistry.getRecipeList(entityPlayer.getUniqueID(),craftingBlock);
            if(
                oldIndex != -1 &&
                        recipes.get(oldIndex).IsReady()
            ) {
                List<ItemStack> newResultStack = tryToScramble(entityPlayer.getUniqueID(),craftingBlock,(InventoryCrafting) craftGrid,ingredient,true);
                List<ItemStack> oldResultStack = recipes.get(oldIndex).checkResult();

                //get Ingredients for recipes that use the output item
                if (newResultStack !=new ArrayList<ItemStack>()) {
                    recipes.get(newIndex).setNewOutput(newResultStack);//current index -> change Item
                    recipes.get(oldIndex).setNewOutput(oldResultStack);//changed index -> current item
                    //logger.info("{} swapped with {}",oldIngredient.toString(), newIngredient.toString());
                }
                ModCraftingManager.updateRecipe(entityPlayer, craftingBlock, recipes.get(oldIndex), oldIndex);
                ModCraftingManager.updateRecipe(entityPlayer, craftingBlock, recipes.get(newIndex), newIndex);
            }
        }
    }

    //Only call for comparisons
    public static List<ItemStack> checkResult(UUID playerId,ResourceLocation craftingBlock,List<ItemStack> ingredients)
    {
        return ModRecipeRegistry.checkResult(ingredients,playerId,craftingBlock);
    }
    //Only call from Crafting Containers
    public static List<ItemStack> craftItem(UUID playerId, ResourceLocation craftingBlock, InventoryCraftResult ingredients)
    {
        List<ItemStack> ingItemStack = new ArrayList<>();
        for (int i = 0; i < ingredients.getSizeInventory(); i++) {
            ingItemStack.add(ingredients.getStackInSlot(i));
        }
        return ModRecipeRegistry.getRecipe(playerId,craftingBlock,ingItemStack).craftItem();
    }

    public static boolean recipeExists(UUID playerId, ResourceLocation craftingBlock,List<ItemStack> ingredients)
    {
        List<ModRecipe> recipeList =ModRecipeRegistry.getRecipeList(playerId,craftingBlock);
        for (ModRecipe check:recipeList) {
            List<ItemStack> checkStack = check.getInputItemStacks();
            if (ingredients.size() == checkStack.size()) {
                boolean match = true;
                for (int i = 0; i < checkStack.size(); i++) {
                    if (ingredients.get(i) != checkStack.get(i)) {
                        match = false;//recipe not the same
                        break;
                    }
                }
                if (match)
                    return true;//match
            }
        }
        return false;//no matching sizes
    }
}