package com.doorfail.scramblecraft.jei;

import com.doorfail.scramblecraft.block.scramble_bench.ContainerScrambleBench;
import com.doorfail.scramblecraft.block.scramble_bench.GUIScrambleBench;
import com.doorfail.scramblecraft.jei.scrambleBench.ScrambleBenchRecipeCategory;
import com.doorfail.scramblecraft.jei.scrambleBench.ScrambleBenchRecipeMaker;
import com.doorfail.scramblecraft.recipe.Recipe;
import com.doorfail.scramblecraft.util.Reference;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.client.resources.I18n;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@JEIPlugin
public class JEIscramblePlugin implements IModPlugin {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
    }

    public void registerIngredients(IModIngredientRegistration registry) {
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

        registry.addRecipes(ScrambleBenchRecipeMaker.getRecipe(jeiHelpers),RecipeCategories.SCRAMBLEBENCH);
        registry.addRecipeClickArea(GUIScrambleBench.class,110,0,10,10,RecipeCategories.SCRAMBLEBENCH);
        recipeTransfer.addRecipeTransferHandler(ContainerScrambleBench.class, RecipeCategories.SCRAMBLEBENCH,0,1,2,26);
    }

    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        logger.info("Running on Runtime");
    }


}
