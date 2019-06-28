package com.doorfail.scramblecraft.mod.jei.scrambleBench;

import com.doorfail.scramblecraft.mod.jei.RecipeCategories;
import com.doorfail.scramblecraft.util.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;

public class ScrambleBenchRecipeCategory extends AbstractScrambleBenchRecipeCategory<JEIScrambleBenchRecipe> {
    private  final IDrawable background;
    private final String name;

    public ScrambleBenchRecipeCategory(IGuiHelper helper)
    {
        super(helper);
        background = helper.createDrawable(TEXTURES,4,12,150,60);
        name = "Scramble Bench";
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(Minecraft minecraft)
    {

    }

    @Override
    public String getTitle()
    {
        return name;
    }

    @Override
    public String getModName()
    {
        return Reference.NAME;
    }

    @Override
    public String getUid()
    {
        return RecipeCategories.SCRAMBLEBENCH;
    }

    public void setRecipe(IRecipeLayout recipeLayout, JEIScrambleBenchRecipe recipeWrapper, IIngredients ingredients)
    {
        IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        stacks.init(input11,true,25,4);
        stacks.init(input21,true,43,4);
        stacks.init(input31,true,61,4);

        stacks.init(input12,true,25,22);
        stacks.init(input22,true,43,22);
        stacks.init(input23,true,61,22);

        stacks.init(input13,true,25,40);
        stacks.init(input23,true,43,40);
        stacks.init(input33,true,61,40);
        stacks.init(output,false,121,22);
        stacks.set(ingredients);
    }
}
