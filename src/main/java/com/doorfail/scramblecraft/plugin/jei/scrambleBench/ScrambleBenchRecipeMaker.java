package com.doorfail.scramblecraft.plugin.jei.scrambleBench;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.plugin.jei.ingredient.DynamicItemStack;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScrambleBenchRecipeMaker {
    public static List<JEIScrambleBenchRecipe> getRecipes(IJeiHelpers helpers, UUID playerId)
    {
        IStackHelper stackHelper = helpers.getStackHelper();
        //ScrambleBenchRecipes instance = ScrambleBenchRecipes.instance();
        //Table<ItemStack,ItemStack,ItemStack> recipes = instance.getConstantRecipes();
        List<JEIScrambleBenchRecipe> jeiRecipes =new ArrayList<>();
        List<DynamicItemStack> ingredients = new ArrayList<>();

        List<ModRecipe> recipes = ModRecipeRegistry.getRecipeList(playerId, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
        for (ModRecipe sbr:recipes ) {
            for (ItemStack it: sbr.checkResult())
                ingredients.add(new DynamicItemStack(it));

            jeiRecipes.add(new JEIScrambleBenchRecipe(ingredients,new DynamicItemStack( sbr.checkResult().get(0))));
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
