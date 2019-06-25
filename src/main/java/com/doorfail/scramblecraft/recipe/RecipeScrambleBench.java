package com.doorfail.scramblecraft.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeScrambleBench extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private final ItemStack output;

    public RecipeScrambleBench(ItemStack output)
    {
        this.output = output;
    }

    public static IRecipe from(IRecipe other)
    {
        return new RecipeScrambleBench(other.getRecipeOutput()).setRegistryName(other.getRegistryName());
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn)
    {
        return false;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }
}