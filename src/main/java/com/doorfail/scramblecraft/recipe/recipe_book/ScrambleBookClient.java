package com.doorfail.scramblecraft.recipe.recipe_book;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.RecipeBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

@SideOnly(Side.CLIENT)
public class ScrambleBookClient extends RecipeBook {
    public static final Map<CreativeTabs, List<ScrambleSubRecipes>> RECIPES_BY_TAB = Maps.newHashMap();
    public static final List<ScrambleSubRecipes> ALL_RECIPES = Lists.newArrayList();
    public static Logger logger;

    public ScrambleBookClient() {
    }

    //New Recipe
    private static ScrambleSubRecipes newRecipeList(CreativeTabs srcTab) {
        ScrambleSubRecipes recipelist = new ScrambleSubRecipes();
        ALL_RECIPES.add(recipelist);
        (RECIPES_BY_TAB.computeIfAbsent(srcTab, (tabs) -> {
            return new ArrayList();
        })).add(recipelist);
        (RECIPES_BY_TAB.computeIfAbsent(CreativeTabs.SEARCH, (tabs) -> {
            return new ArrayList();
        })).add(recipelist);
        return recipelist;
    }

    private static CreativeTabs getItemStackTab(ItemStack stackIn) {
        CreativeTabs creativetabs = stackIn.getItem().getCreativeTab();
        if (creativetabs != CreativeTabs.BUILDING_BLOCKS &&
                creativetabs != CreativeTabs.TOOLS &&
                creativetabs != CreativeTabs.REDSTONE) {
            return creativetabs == CreativeTabs.COMBAT ?
                    CreativeTabs.TOOLS :
                    CreativeTabs.MISC;
        } else {
            return creativetabs;
        }
    }

    //All possible recipes should be loaded here
    public static void rebuildTable() {
        RECIPES_BY_TAB.clear();
        ALL_RECIPES.clear();
        Table<CreativeTabs, String, ScrambleSubRecipes> table = HashBasedTable.create();
        Iterator var1 =ModRecipeRegistry.getModRecipeList(Minecraft.getMinecraft().player.getUniqueID(), ModBlocks.SCRAMBLE_BENCH.getRegistryName()).iterator();

        while(var1.hasNext()) {
            ModRecipe irecipe = (ModRecipe)var1.next();
            if (!irecipe.isDynamic()) {
                CreativeTabs recipeTab = getItemStackTab(irecipe.getRecipeOutput());
                String s = irecipe.getGroup();
                ScrambleSubRecipes recipelist1;
                if (s.isEmpty())
                    recipelist1 = newRecipeList(recipeTab);
                else {
                    recipelist1 = table.get(recipeTab, s);
                    if (recipelist1 == null) {
                        recipelist1 = newRecipeList(recipeTab);
                        table.put(recipeTab, s, recipelist1);
                    }
                }

                if(irecipe.checkResult().size() !=0)
                    recipelist1.add(irecipe);
                else
                    logger.info(irecipe.getRegistryName()+" Has an empty Output");
            }
        }

    }

    static {
        rebuildTable();
    }
}
