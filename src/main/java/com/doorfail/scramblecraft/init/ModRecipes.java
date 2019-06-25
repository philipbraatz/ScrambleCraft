package com.doorfail.scramblecraft.init;

import com.doorfail.scramblecraft.block.scramble_bench.ScrambleCraftingManager;
import com.doorfail.scramblecraft.recipe.DummyRecipe;
import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipe;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModRecipes {

    private static Logger logger;

    public static List<ScrambleBenchRecipe> recipes = new ArrayList<>();

    public static ScrambleCraftingManager craftingManager = new ScrambleCraftingManager();

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

    public static void init() {
        craftingManager.init();
        //removeRecipes(Blocks.CRAFTING_TABLE);

        ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>)ForgeRegistries.RECIPES;
        ArrayList<IRecipe> recipes = Lists.newArrayList(recipeRegistry.getValuesCollection());
        for (IRecipe r : recipes)
        {
            ItemStack output = r.getRecipeOutput();
            if (output.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
            {
                recipeRegistry.remove(r.getRegistryName());
                recipeRegistry.register(DummyRecipe.from(r));
            }
        }

        //logger.info("Removed recipe for crafting table");
        //ItemStack planks4 = new ItemStack(Item.getItemFromBlock(Blocks.PLANKS), 4);
        //GameRegistry.addShapelessRecipe(new ResourceLocation("silver_ingot"), null, silver_ingot, Ingredient.fromStacks(ItemStack.EMPTY));
        //GameRegistry.addShapelessRecipe(Item.getItemFromBlock(Blocks.PLANKS).getRegistryName(), null,planks4, Ingredient.fromItem(Item.getItemFromBlock( ModBlocks.SCRAMBLE_BENCH)));
        GameRegistry.addSmelting(ModItems.RUBY, new ItemStack(ModBlocks.RUBY_BLOCK, 1), 1.5f);
        GameRegistry.addSmelting(ModBlocks.RUBY_BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK, 2), 3.0f);
        GameRegistry.addSmelting(new ItemStack(Items.STICK), new ItemStack(Items.STRING,3),3);

    }

    private static int getMatchIndex(List<Item> ingredients)
    {
        for (int i = 0; i <recipes.size() ; i++) {
            List<ResourceLocation> ingItems = new ArrayList<>();
            List<ResourceLocation> sourceItems = new ArrayList<>();

            for (Item item : ingredients)
                ingItems.add(item.getRegistryName());
            for (Item item : recipes.get(i).inputItems)
                sourceItems.add(item.getRegistryName());

            if (ingItems.size() == sourceItems.size()) //possible match
            {
                boolean match =true;
                for (int j = 0; j < ingItems.size(); j++)
                {
                    if (ingItems.get(j) != sourceItems.get(j))//ingredient[i] is not the same
                        match =false;//fail item
                }
                if(match)
                    return i;
            }
        }
        //new Item
        return -1;
    }


    public static void addRecipe(EntityPlayer entityPlayer,List<Item> ingredients, ItemStack normalOutput)
    {
        ScrambleBenchRecipe newRecipe =new ScrambleBenchRecipe( ingredients,normalOutput);
        ScrambleCraftingManager.saveRecipe(entityPlayer,newRecipe);
        recipes.add(newRecipe);
    }

public static ItemStack tryToScramble(InventoryCrafting inventoryCrafting, List<Item> ingredient, boolean search)
{
    ItemStack newResultStack;
    boolean isSame;
    int overflow = 15;//number of times to try till failure
    int counter = 0;

    //loop until unique or till failure
    do {
        isSame = false;

        counter++;
        if(search)
            newIndex = new Random().nextInt(recipes.size());//try to swap
        if (newIndex == oldIndex)
            isSame = true;

        newResultStack = recipes.get(newIndex).outputItemStack;
        for (Item it : ingredient) {
            //old == new
            if (newResultStack.getItem().getRegistryName() == it.getRegistryName() ||//the same recipe
                    newResultStack == new ItemStack(Items.AIR)//EMPTY!
            )
                isSame = true;
        }
    } while (isSame && counter < overflow);

    if(!isSame) {
        if(ScrambleCraftingManager.areItemsRelated(inventoryCrafting, newResultStack.getItem(), recipes.get(oldIndex).outputItemStack.getItem()))
            return tryToScramble(inventoryCrafting,ingredient,true);

        return newResultStack;
    }
    else
        return ItemStack.EMPTY;//overflowed
}

    public static void randomizeRecipe(IInventory craftGrid, EntityPlayer entityPlayer, List<Item> ingredient)
    {
        //scramble if more than a few
        if(recipes.size() >2)
        {
            oldIndex=getMatchIndex(ingredient);
            if(
                oldIndex != -1 &&
                        recipes.get(oldIndex).IsReady()
            ) {
                ItemStack newResultStack = tryToScramble((InventoryCrafting) craftGrid,ingredient,true);
                ItemStack oldResultStack = recipes.get(oldIndex).outputItemStack;

                //get Ingredients for recipes that use the output item
                if (newResultStack != ItemStack.EMPTY) {


                    recipes.get(oldIndex).setOutput(newResultStack);//current index -> change Item
                    recipes.get(newIndex).setOutput(oldResultStack);//changed index -> current item
                    //logger.info("{} swapped with {}",oldIngredient.toString(), newIngredient.toString());
                    ScrambleCraftingManager.updateRecipe(entityPlayer,recipes.get(oldIndex),oldIndex);
                    ScrambleCraftingManager.updateRecipe(entityPlayer,recipes.get(newIndex),newIndex);
                }
            }
        }
    }
    public static boolean isAir(){return false;}

    private static List<List<Item>> getListInputs()
    {
        List<List<Item>> recipeInputs =new ArrayList<>();
        for (ScrambleBenchRecipe r:recipes)
            recipeInputs.add(r.inputItems);
        return recipeInputs;
    }

    public static ItemStack getOutput(EntityPlayer entityPlayer,List<Item> ingredients)
    {

        int index = getListInputs().indexOf(ingredients);
        if(index != -1)
            return recipes.get(index).craftItem();//valid
        else
        {
            ShapedRecipes sr = new ShapedRecipes(null,3,3, getItemsAsIngredient(ingredients),ItemStack.EMPTY);
            addRecipe(entityPlayer,ingredients,sr.getRecipeOutput());
        }
        return ItemStack.EMPTY;
    }

    public static boolean recipeExists(List<Item> ingredients)
    {
        for (List<Item> check:getListInputs())
            if (ingredients.size() == check.size()) {
                boolean match = true;
                for (int i = 0; i < check.size(); i++) {
                    if (ingredients.get(i) != check.get(i)) {
                        match = false;//recipe not the same
                        break;
                    }
                }
                if (match)
                    return true;//match
            }
        return false;//no matching sizes
    }
}