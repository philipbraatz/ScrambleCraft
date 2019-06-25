package com.doorfail.scramblecraft.jei.scrambleBench;

import com.doorfail.scramblecraft.util.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.util.ResourceLocation;

public abstract class AbstractScrambleBenchRecipeCategory<T extends IRecipeWrapper> implements IRecipeCategory<T> {
    protected  static final ResourceLocation TEXTURES =new ResourceLocation(Reference.MODID+":textures/gui/scramble_bench.png");
    protected  static final int input11 =0;
    protected  static final int input12 =1;
    protected  static final int input13 =2;
    protected  static final int input21 =3;
    protected  static final int input22 =4;
    protected  static final int input23 =5;
    protected  static final int input31 =6;
    protected  static final int input32 =7;
    protected  static final int input33 =8;
    protected  static final int output =9;

    public AbstractScrambleBenchRecipeCategory(IGuiHelper helper)
    {

    }

}
