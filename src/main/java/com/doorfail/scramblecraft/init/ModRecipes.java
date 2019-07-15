package com.doorfail.scramblecraft.init;

import com.doorfail.scramblecraft.recipe.DummyRecipe;
import com.doorfail.scramblecraft.recipe.ModCraftingManager;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.google.common.collect.Lists;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModRecipes {

    private static Logger logger;

    public static Map<UUID, ModRecipeRegistry> recipes = new HashMap<>();

    public static ModCraftingManager craftingManager = new ModCraftingManager();

    private static int newIndex =-1;
    private static int oldIndex =-1;
    public static int previousStackSize =222;//big stack number

    //save hardcoded values
    public static void init() {
        craftingManager.init();
        //removeRecipes(Blocks.CRAFTING_TABLE);

        ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
        ArrayList<IRecipe> recipes = Lists.newArrayList(recipeRegistry.getValuesCollection());
        for (IRecipe r : recipes)
        {
            //recipeRegistry = replaceRecipe(recipeRegistry,r,Item.getItemFromBlock(Blocks.CRAFTING_TABLE),new ItemStack(ModBlocks.SCRAMBLE_BENCH));
            //recipeRegistry = replaceRecipe(recipeRegistry,r,Item.getItemFromBlock(Blocks.FURNACE),new ItemStack(ModBlocks.SCRAMBLE_FURNACE_OFF));

            ItemStack output = r.getRecipeOutput();
            if (output.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
            {
                recipeRegistry.remove(r.getRegistryName());
                //recipeRegistry.register(DummyRecipe.from(r));
            }
        }

        //logger.info("Removed recipe for crafting table");
        //Ingredient ruby =Ingredient.fromItem(ModItems.RUBY);
        //GameRegistry.addShapelessRecipe(ModBlocks.SCRAMBLE_BENCH.getRegistryName(), null, new ItemStack(ModBlocks.RUBY_BLOCK, 1),
        //        ruby,ruby,ruby,ruby,ruby,ruby,ruby,ruby,ruby);
        //GameRegistry.addSmelting(ModBlocks.RUBY_BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK, 2), 3.0f);
        //GameRegistry.addSmelting(new ItemStack(Blocks.OBSIDIAN), new ItemStack(ModItems.OBSIDIAN_INGOT), 0.4F);
        GameRegistry.addShapedRecipe(Blocks.CRAFTING_TABLE.getRegistryName(),null,new ItemStack(ModBlocks.SCRAMBLE_BENCH),
                "WW",
                "WW",
                'W', Blocks.PLANKS);
        //ScrambleFurnaceRecipes.instance().addRecipe(ModBlocks.RUBY_ORE, new ItemStack(ModItems.RUBY), 0.3f);

    }

    private static ForgeRegistry<IRecipe> replaceRecipe(ForgeRegistry<IRecipe> recipeRegistry,IRecipe current,Item oldResult,ItemStack newResult)
    {
        ItemStack output = current.getRecipeOutput();
        if (output.getItem() == oldResult)
        {
            recipeRegistry.remove(current.getRegistryName());
            recipeRegistry.register(DummyRecipe.from(current));
        }
        return recipeRegistry;//IDK if this is required
    }
}