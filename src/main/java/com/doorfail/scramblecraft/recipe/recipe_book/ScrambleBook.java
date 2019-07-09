package com.doorfail.scramblecraft.recipe.recipe_book;

import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.BitSet;

public class ScrambleBook {
    private static Logger logger;

    protected final BitSet recipes = new BitSet();
    protected final BitSet newRecipes = new BitSet();
    protected boolean isGuiOpen;
    protected boolean isFilteringCraftable;

    public ScrambleBook() {
    }
    //from player recipe book
    public ScrambleBook(RecipeBook playerRecipeBook) {
        this.copyFrom(playerRecipeBook);
    }

    //DO NOT USE
    public RecipeBook toRecipeBook()
    {
        return new RecipeBook();
    }

    public void copyFrom(ScrambleBook that) {
        this.recipes.clear();
        this.newRecipes.clear();
        this.recipes.or(that.recipes);
        this.newRecipes.or(that.newRecipes);
    }

    //TODO properly copyFrom RecipeBook or remove
    public void copyFrom(RecipeBook that) {
        this.recipes.clear();
        this.newRecipes.clear();
        //this.recipes.or(that.recipes);
        //this.newRecipes.or(that.newRecipes);
    }

    public void unlock(IRecipe recipe) {
        if (!recipe.isDynamic()) {
            this.recipes.set(getRecipeId(recipe));
        }

    }

    public boolean isUnlocked(@Nullable IRecipe recipe) {
        return this.recipes.get(getRecipeId(recipe));
    }

    public void lock(IRecipe recipe) {
        int i = getRecipeId(recipe);
        this.recipes.clear(i);
        this.newRecipes.clear(i);
    }

    //might replace with custom registry
    /** @deprecated */
    @Deprecated
    protected static int getRecipeId(@Nullable IRecipe recipe) {
        int ret;
        if(recipe instanceof ModRecipe)
            ret = ModRecipeRegistry.getModRecipeId((ModRecipe) recipe);
        else
            ret = CraftingManager.REGISTRY.getIDForObject(recipe);

        if (ret == -1)
            logger.warn(String.format("Attempted to get the ID for a unknown recipe: %s Name: %s", recipe, recipe.getRegistryName()));

        return ret;
    }

    @SideOnly(Side.CLIENT)
    public boolean isNew(IRecipe recipe) {
        return this.newRecipes.get(getRecipeId(recipe));
    }

    public void markSeen(IRecipe recipe) {
        this.newRecipes.clear(getRecipeId(recipe));
    }

    public void markNew(IRecipe recipe) {
        this.newRecipes.set(getRecipeId(recipe));
    }

    @SideOnly(Side.CLIENT)
    public boolean isGuiOpen() {
        return this.isGuiOpen;
    }

    public void setGuiOpen(boolean open) {
        this.isGuiOpen = open;
    }

    @SideOnly(Side.CLIENT)
    public boolean isFilteringCraftable() {
        return this.isFilteringCraftable;
    }

    public void setFilteringCraftable(boolean shouldFilter) {
        this.isFilteringCraftable = shouldFilter;
    }
}
