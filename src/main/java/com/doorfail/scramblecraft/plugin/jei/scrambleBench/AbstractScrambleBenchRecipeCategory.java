package com.doorfail.scramblecraft.plugin.jei.scrambleBench;

import com.doorfail.scramblecraft.util.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractScrambleBenchRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {
    static final ResourceLocation TEXTURES =new ResourceLocation(Reference.MODID+":textures/recipe_book/scramble_bench.png");
    static final int input11 =0;
    static final int input12 =1;
    static final int input13 =2;
    static final int input21 =3;
    static final int input22 =4;
    static final int input23 =5;
    static final int input31 =6;
    static final int input32 =7;
    static final int input33 =8;
    static final int output =9;

    public AbstractScrambleBenchRecipeCategory(IGuiHelper helper)
    {

    }

}
