package com.doorfail.scramblecraft.mod.jei.scrambleBench;

import com.doorfail.scramblecraft.init.ModRecipes;
import com.doorfail.scramblecraft.mod.jei.ingredient.DynamicItemStack;
import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipe;
import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipes;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScrambleBenchRecipeMaker {
    public static List<JEIScrambleBenchRecipe> getRecipes(IJeiHelpers helpers)
    {
        IStackHelper stackHelper = helpers.getStackHelper();
        ScrambleBenchRecipes instance = ScrambleBenchRecipes.instance();
        //Table<ItemStack,ItemStack,ItemStack> recipes = instance.getConstantRecipes();
        List<JEIScrambleBenchRecipe> jeiRecipes =new ArrayList<>();
        List<DynamicItemStack> ingredients = new ArrayList<>();

        for (ScrambleBenchRecipe sbr: ModRecipes.recipes) {
            for (Item it: sbr.inputItems)
                ingredients.add(new DynamicItemStack(it));

            jeiRecipes.add(new JEIScrambleBenchRecipe(ingredients,new DynamicItemStack( sbr.outputItemStack)));
        }

        //debug recipe
        ingredients.clear();
        ingredients.add(new DynamicItemStack(Items.BONE));
        ingredients.add(new DynamicItemStack(Items.REDSTONE));
        ingredients.add(new DynamicItemStack(Items.POISONOUS_POTATO));
        ingredients.add(new DynamicItemStack(Items.BUCKET));
        ingredients.add(new DynamicItemStack(Items.STICK));
        ingredients.add(new DynamicItemStack(Items.DIAMOND));
        ingredients.add(new DynamicItemStack(Items.IRON_INGOT));
        ingredients.add(new DynamicItemStack(Items.GOLD_INGOT));
        ingredients.add(new DynamicItemStack(Items.GOLD_NUGGET));
        jeiRecipes.add(new JEIScrambleBenchRecipe(ingredients,new DynamicItemStack(Items.BLAZE_POWDER)));

        return jeiRecipes;
    }

}
