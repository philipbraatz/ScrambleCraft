package com.doorfail.scramblecraft.plugin.jei.scrambleBench;

import com.doorfail.scramblecraft.plugin.jei.ingredient.DynamicItemStack;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;

import java.util.List;

public class JEIScrambleBenchRecipe implements IRecipeWrapper {

    private final List<DynamicItemStack> inputs;
    private  final DynamicItemStack output;

    public JEIScrambleBenchRecipe(List<DynamicItemStack> inputs, DynamicItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }


    @Override
    public void getIngredients(IIngredients ingredients) {
     ingredients.setInputs(DynamicItemStack.class,inputs);
     ingredients.setOutput(DynamicItemStack.class,output);
    }

    @Override
    public void drawInfo(Minecraft minecraft,int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        //ScrambleBenchRecipes recipes = ScrambleBenchRecipes.instance();

    }
}
