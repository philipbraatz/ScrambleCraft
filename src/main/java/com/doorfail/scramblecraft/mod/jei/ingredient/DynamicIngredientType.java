package com.doorfail.scramblecraft.mod.jei.ingredient;

import mezz.jei.api.recipe.IIngredientType;
import net.minecraft.item.ItemStack;

public class DynamicIngredientType implements IIngredientType<DynamicItemStack> {
    @Override
    public Class<? extends DynamicItemStack> getIngredientClass() {
        return null;
    }
}
