package com.doorfail.scramblecraft.plugin.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;

import javax.annotation.Nullable;

public class DynamicIngredientRenderer implements IIngredientRenderer<DynamicItemStack> {
    private IIngredientHelper<DynamicItemStack> ingredientHelper;
    private IIngredientRenderer<DynamicItemStack> itemRenderer;

    public DynamicIngredientRenderer(IIngredientRenderer<DynamicItemStack> itemRenderer,DynamicIngredientHelper ingredientHelper) {
        this.ingredientHelper = ingredientHelper;
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable DynamicItemStack ingredient) {
        if (ingredient != null) {
            itemRenderer.render(minecraft,xPosition,yPosition,ingredient);
        }
    }

    @Override
    public java.util.List<String> getTooltip(Minecraft minecraft, DynamicItemStack ingredient, ITooltipFlag tooltipFlag) {
        return itemRenderer.getTooltip(minecraft,ingredient,tooltipFlag);
    }
}
