package com.doorfail.scramblecraft.recipe;

import com.doorfail.scramblecraft.init.ModBlocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class DummyRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private final ItemStack output;

    public DummyRecipe(ItemStack output)
    {
        this.output = output;
    }

    public static IRecipe from(IRecipe old)
    {
        return new DummyRecipe(old.getRecipeOutput()).setRegistryName(old.getRegistryName());
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
        return new ItemStack(Items.ENDER_PEARL);
    }
}