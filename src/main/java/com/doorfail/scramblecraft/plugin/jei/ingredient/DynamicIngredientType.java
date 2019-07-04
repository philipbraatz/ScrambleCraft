package com.doorfail.scramblecraft.plugin.jei.ingredient;

import mezz.jei.api.recipe.IIngredientType;

public class DynamicIngredientType implements IIngredientType<DynamicItemStack> {
    @Override
    public Class<? extends DynamicItemStack> getIngredientClass() {
        return null;
    }
}
