package com.doorfail.scramblecraft.recipe.recipe_book;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.UUID;

public class ScrambleList {
    private List<ModRecipe> recipes = Lists.newArrayList();
    private final BitSet craftable = new BitSet();
    private final BitSet canFit = new BitSet();
    private final BitSet inBook = new BitSet();
    private boolean singleResultItem = true;

    public ScrambleList() {
    }
    public ScrambleList(RecipeList recipesIn,RecipeBook book) {
        for (IRecipe ir: recipesIn.getRecipes()
             ) {
            recipes.add((ModRecipe) ir);
            this.isCraftable(ir);
        }
        this.singleResultItem =recipesIn.hasSingleResultItem();


        this.updateKnownRecipes(book);
    }

    public static List<ScrambleList> fromRecipeList(List<RecipeList> recipeLists, RecipeBook book) {
        List<ScrambleList> from = new ArrayList<>();
        for (RecipeList recipeList:recipeLists)
            from.add(new ScrambleList(recipeList,book));
        return from;
    }

    public boolean isNotEmpty() {
        return !this.inBook.isEmpty();
    }

    public void updateKnownRecipes(RecipeBook book) {
        for(int i = 0; i < this.recipes.size(); ++i) {
            this.inBook.set(i, book.isUnlocked(this.recipes.get(i)));
        }

    }

    public void canCraft(RecipeItemHelper handler, int width, int height, RecipeBook book) {
        for(int i = 0; i < this.recipes.size(); ++i) {
            //boolean flag;
            //
            //if(this.recipes.get(i) instanceof ModRecipe)
            //{
            //    flag =((ModRecipe)this.recipes.get(i)).canFit(width,height);
            //    this.canFit.set(i,flag);
            //    this.craftable.set(i,flag && handler.canCraft(
            //            ((ModRecipe)this.recipes.get(i)).getInputIngredients()
            //    );
            //}
            IRecipe irecipe = this.recipes.get(i);
            boolean flag = irecipe.canFit(width, height) ;//&& book.isUnlocked(irecipe);//Error getting IDs for unique named color recipes
            this.canFit.set(i, flag);
            this.craftable.set(i, flag && handler.canCraft(irecipe, (IntList)null));
        }

    }

    public boolean isCraftable(IRecipe recipe) {
        //if(recipes.indexOf(recipe) != -1)
        //    return this.craftable.get(this.recipes.indexOf(recipe));
        //else
        //    return false;
        return true;
    }

    public boolean containsCraftableRecipes() {
        return !this.craftable.isEmpty();
    }

    public boolean containsValidRecipes() {
        //return !this.canFit.isEmpty();
        return true;
    }

    public List<ModRecipe> getRecipes() {
        return this.recipes;
    }

    public List<ModRecipe> getRecipes(boolean onlyCraftable) {
        List<ModRecipe> list = new ArrayList<>();//ModRecipeRegistry.getModRecipeList(Minecraft.getMinecraft().player.getUniqueID(),
                //ModBlocks.SCRAMBLE_BENCH.getRegistryName());

        for(int i = this.inBook.nextSetBit(0); i >= 0; i = this.inBook.nextSetBit(i + 1))
            //if ((onlyCraftable ? this.craftable : this.canFit).get(i))
                list.add(this.recipes.get(i));

        return list;
    }

    public List<IRecipe> getDisplayRecipes(boolean onlyCraftable) {
        //List<IRecipe> list = Lists.newArrayList();
//
        //for(int i = this.inBook.nextSetBit(0); i >= 0; i = this.inBook.nextSetBit(i + 1))
        //    if (this.canFit.get(i) && this.craftable.get(i) == onlyCraftable)
        //        list.add(this.recipes.get(i));

        return ModRecipeRegistry.getIRecipeList( recipes);
    }

    public void add(ModRecipe recipe) {
        this.recipes.add(recipe);
        if (this.singleResultItem) {
            ItemStack itemstack = (this.recipes.get(0)).getRecipeOutput();
            ItemStack itemstack1 = recipe.getRecipeOutput();
            this.singleResultItem = ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1);
        }

    }

    public boolean hasSingleResultItem() {
        return this.singleResultItem;
    }
}