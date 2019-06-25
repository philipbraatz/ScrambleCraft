package com.doorfail.scramblecraft.jei.scrambleBench;

import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.List;

public class JEIScrambleBenchRecipe implements IRecipeWrapper {

    private final List<ItemStack> inputs;
    private  final ItemStack output;

    public JEIScrambleBenchRecipe(List<ItemStack> inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }


    @Override
    public void getIngredients(IIngredients ingredients) {
     ingredients.setInputs(ItemStack.class,inputs);
     ingredients.setOutput(ItemStack.class,output);
    }

    @Override
    public void drawInfo(Minecraft minecraft,int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        ScrambleBenchRecipes recipes = ScrambleBenchRecipes.instance();

    }
}
