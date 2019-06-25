package com.doorfail.scramblecraft.jei.scrambleBench;

import com.doorfail.scramblecraft.init.ModRecipes;
import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipe;
import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipes;
import com.google.common.collect.Table;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScrambleBenchRecipeMaker {
    public static List<JEIScrambleBenchRecipe> getRecipe(IJeiHelpers helpers)
    {
        IStackHelper stackHelper = helpers.getStackHelper();
        ScrambleBenchRecipes instance = ScrambleBenchRecipes.instance();
        //Table<ItemStack,ItemStack,ItemStack> recipes = instance.getConstantRecipes();
        List<JEIScrambleBenchRecipe> jeiRecipes =new ArrayList<>();
        List<ItemStack> ingredients = new ArrayList<>();

        for (ScrambleBenchRecipe sbr: ModRecipes.recipes) {
            for (Item it: sbr.inputItems)
                ingredients.add(new ItemStack(it));

            jeiRecipes.add(new JEIScrambleBenchRecipe(ingredients, sbr.outputItemStack));
        }

        //debug recipe
        ingredients.clear();
        ingredients.add(new ItemStack(Items.BONE));
        ingredients.add(new ItemStack(Items.REDSTONE));
        ingredients.add(new ItemStack(Items.POISONOUS_POTATO));
        ingredients.add(new ItemStack(Items.BUCKET));
        ingredients.add(new ItemStack(Items.STICK));
        ingredients.add(new ItemStack(Items.DIAMOND));
        ingredients.add(new ItemStack(Items.IRON_INGOT));
        ingredients.add(new ItemStack(Items.GOLD_INGOT));
        ingredients.add(new ItemStack(Items.GOLD_NUGGET));
        jeiRecipes.add(new JEIScrambleBenchRecipe(ingredients,new ItemStack(Items.BLAZE_POWDER)));
        return jeiRecipes;
    }

}
