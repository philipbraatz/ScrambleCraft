package com.doorfail.scramblecraft.mod.jei.ingredient;

import com.doorfail.scramblecraft.util.Reference;
import mezz.jei.api.ingredients.IIngredientHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Collections;

public class DynamicIngredientHelper implements IIngredientHelper<DynamicItemStack> {
    @Nullable
    @Override
    public DynamicItemStack getMatch(Iterable<DynamicItemStack> ingredients, DynamicItemStack ingredientToMatch) {
        for (DynamicItemStack DynamicItemStack : ingredients) {
            if (DynamicItemStack.getItem() == ingredientToMatch.getItem()) {
                return DynamicItemStack;
            }
        }
        return null;
    }

    @Override
    public String getDisplayName(DynamicItemStack ingredient) {
        return "JEI Debug Item #" + ingredient.getDisplayName();
    }

    @Override
    public String getUniqueId(DynamicItemStack ingredient) {
        return ingredient.getItem().getRegistryName().toString();
    }

    @Override
    public String getWildcardId(DynamicItemStack ingredient) {
        return getUniqueId(ingredient);
    }

    @Override
    public String getModId(DynamicItemStack ingredient) {
        return Reference.MODID;
    }

    @Override
    public Iterable<Color> getColors(DynamicItemStack ingredient) {
        return Collections.emptyList();
    }

    @Override
    public String getResourceId(DynamicItemStack ingredient) {
        return ingredient.getItem().getRegistryName().toString();
    }

    @Override
    public ItemStack getCheatItemStack(DynamicItemStack ingredient) {
        ingredient.setCount(64);
        return ingredient.getItemStack();
    }

    @Override
    public DynamicItemStack copyIngredient(DynamicItemStack ingredient) {
        return ingredient.copy();
    }

    @Override
    public String getErrorInfo(@Nullable DynamicItemStack ingredient) {
        if (ingredient == null) {
            return "ScrambleCraft ingredient: null";
        }
        return getDisplayName(ingredient);
    }
}
