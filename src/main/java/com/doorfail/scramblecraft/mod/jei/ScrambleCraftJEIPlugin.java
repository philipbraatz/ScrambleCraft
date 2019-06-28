package com.doorfail.scramblecraft.mod.jei;

import com.doorfail.scramblecraft.block.scramble_bench.ContainerScrambleBench;
import com.doorfail.scramblecraft.block.scramble_bench.GUIScrambleBench;
import com.doorfail.scramblecraft.mod.jei.ingredient.DynamicIngredientHelper;
import com.doorfail.scramblecraft.mod.jei.ingredient.DynamicIngredientRenderer;
import com.doorfail.scramblecraft.mod.jei.ingredient.DynamicItemStack;
import com.doorfail.scramblecraft.mod.jei.ingredient.DynamicItemStackRenderer;
import com.doorfail.scramblecraft.mod.jei.scrambleBench.ScrambleBenchRecipeCategory;
import com.doorfail.scramblecraft.mod.jei.scrambleBench.ScrambleBenchRecipeMaker;
import com.doorfail.scramblecraft.util.Reference;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JEIPlugin
public class ScrambleCraftJEIPlugin implements IModPlugin {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
    }

    public void registerIngredients(IModIngredientRegistration registry) {
        DynamicItemStackRenderer itemStackRenderer = new DynamicItemStackRenderer();
        DynamicIngredientHelper ingredientHelper = new DynamicIngredientHelper();
        DynamicIngredientRenderer ingredientRenderer = new DynamicIngredientRenderer(itemStackRenderer,ingredientHelper);
        registry.register(
                DynamicItemStack.class,
                Collections.emptyList(),
                ingredientHelper,
                ingredientRenderer
        );
    }

    public void registerCategories(IRecipeCategoryRegistration registry) {
        final IJeiHelpers helpers = registry.getJeiHelpers();
        final IGuiHelper gui = helpers.getGuiHelper();

        registry.addRecipeCategories(new ScrambleBenchRecipeCategory(gui));
    }

    public void register(IModRegistry registry) {
        final IIngredientRegistry ingredientRegistry =registry.getIngredientRegistry();
        final IJeiHelpers jeiHelpers =registry.getJeiHelpers();
        IRecipeTransferRegistry recipeTransfer =registry.getRecipeTransferRegistry();

        registry.addRecipes(ScrambleBenchRecipeMaker.getRecipes(jeiHelpers),RecipeCategories.SCRAMBLEBENCH);
        registry.addRecipeClickArea(GUIScrambleBench.class,156,3,172,17,RecipeCategories.SCRAMBLEBENCH);
        //recipeTransfer.addRecipeTransferHandler(ContainerScrambleBench.class, RecipeCategories.SCRAMBLEBENCH,5,5, 50,50);
    }

    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        //logger.info("Running on Runtime");
    }
}