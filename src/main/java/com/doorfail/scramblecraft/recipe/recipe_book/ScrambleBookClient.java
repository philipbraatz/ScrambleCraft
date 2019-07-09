package com.doorfail.scramblecraft.recipe.recipe_book;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ScrambleBookClient extends ScrambleBook {
    public static final Map<CreativeTabs, List<ScrambleList>> RECIPES_BY_TAB = Maps.newHashMap();
    public static final List<ScrambleList> ALL_RECIPES = Lists.newArrayList();

    public ScrambleBookClient() {
    }

    private static ScrambleList newRecipeList(CreativeTabs srcTab) {
        ScrambleList recipelist = new ScrambleList();
        ALL_RECIPES.add(recipelist);
        (RECIPES_BY_TAB.computeIfAbsent(srcTab, (p_194085_0_) -> {
            return new ArrayList();
        })).add(recipelist);
        (RECIPES_BY_TAB.computeIfAbsent(CreativeTabs.SEARCH, (p_194083_0_) -> {
            return new ArrayList();
        })).add(recipelist);
        return recipelist;
    }

    private static CreativeTabs getItemStackTab(ItemStack stackIn) {
        CreativeTabs creativetabs = stackIn.getItem().getCreativeTab();
        if (creativetabs != CreativeTabs.BUILDING_BLOCKS && creativetabs != CreativeTabs.TOOLS && creativetabs != CreativeTabs.REDSTONE) {
            return creativetabs == CreativeTabs.COMBAT ? CreativeTabs.TOOLS : CreativeTabs.MISC;
        } else {
            return creativetabs;
        }
    }

    public static void rebuildTable() {
        RECIPES_BY_TAB.clear();
        ALL_RECIPES.clear();
        Table<CreativeTabs, String, ScrambleList> table = HashBasedTable.create();
        Iterator var1 = CraftingManager.REGISTRY.iterator();

        while(var1.hasNext()) {
            IRecipe irecipe = (IRecipe)var1.next();
            if (!irecipe.isDynamic()) {
                CreativeTabs creativetabs = getItemStackTab(irecipe.getRecipeOutput());
                String s = irecipe.getGroup();
                ScrambleList recipelist1;
                if (s.isEmpty()) {
                    recipelist1 = newRecipeList(creativetabs);
                } else {
                    recipelist1 = table.get(creativetabs, s);
                    if (recipelist1 == null) {
                        recipelist1 = newRecipeList(creativetabs);
                        table.put(creativetabs, s, recipelist1);
                    }
                }

                recipelist1.add(irecipe);
            }
        }

    }

    static {
        rebuildTable();
    }
}
