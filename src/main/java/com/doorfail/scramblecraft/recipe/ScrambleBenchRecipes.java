package com.doorfail.scramblecraft.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import com.google.common.collect.Table;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ScrambleBenchRecipes
{
    private static final ScrambleBenchRecipes BENCH_BASE = new ScrambleBenchRecipes();
    private List<Recipe> recipeList =new ArrayList<>();


    public static ScrambleBenchRecipes instance()
    {
        return BENCH_BASE;
    }

    private ScrambleBenchRecipes()
    {
        this.addRecipe(new Recipe(Items.APPLE,Items.AIR,RecipeType.donut,new ItemStack(Items.ARROW)));
    }

    public void addRecipe(ItemStack topLeft,     ItemStack top,   ItemStack topRight,
                                         ItemStack middleLeft,  ItemStack middle, ItemStack middleRight,
                                         ItemStack bottomLeft,  ItemStack bottom, ItemStack bottomRight
                                        ,ItemStack output)
    {
        recipeList.add(new Recipe( topLeft,      top,    topRight,
                 middleLeft,   middle,  middleRight,
                 bottomLeft,   bottom,  bottomRight
                , output));
    }

    public void addRecipe(Recipe r)
    {
        recipeList.add(r);
    }

    //public Table<ItemStack,ItemStack,ItemStack> getConstantRecipes()
    //{
    //    return NotImplementedException();//TODO Implement
    //}

    private Recipe findRecipe(Recipe r)
    {
        r.result = ItemStack.EMPTY;
        for (int i=0; i < this.recipeList.size(); i++)
        {
            if(compareItemStacks(r,this.recipeList.get(i)))
                return this.recipeList.get(i);
        }
        return r;//no recipe
    }
    private boolean compareItemStacks(Recipe stack1, Recipe stack2)
    {
        {
            if(
                    stack1.tl == stack2.tl &&
                            stack1.bl == stack2.bl &&
                            stack1.bm == stack2.bm &&
                            stack1.br == stack2.br &&
                            stack1.cl == stack2.cl &&
                            stack1.cm == stack2.cm &&
                            stack1.cr == stack2.cr &&
                            stack1.tm == stack2.tm &&
                            stack1.tr == stack2.tr
            )
                return true;
            else
                return false;
        }
    }
}
