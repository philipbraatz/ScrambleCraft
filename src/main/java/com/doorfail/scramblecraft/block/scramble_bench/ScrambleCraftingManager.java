package com.doorfail.scramblecraft.block.scramble_bench;

import com.doorfail.scramblecraft.recipe.ScrambleBenchRecipe;
import com.google.gson.*;
import com.typesafe.config.ConfigException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.tools.nsc.backend.icode.ExceptionHandlers;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class ScrambleCraftingManager extends CraftingManager {

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean init()
    {
        try
        {
            //register special crafting rules
            return parseJsonRecipes();
        }
        catch (Throwable var1)
        {
            return false;
        }
    }

    private static boolean parseJsonRecipes()
    {
        FileSystem filesystem = null;
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        boolean flag1;

        try
        {
            URL url = CraftingManager.class.getResource("/assets/.mcassetsroot");

            if (url != null)
            {
                URI uri = url.toURI();
                Path path;

                if ("file".equals(uri.getScheme()))
                {
                    //TODO maybe use custom asset recipes
                    path = Paths.get(CraftingManager.class.getResource("/assets/minecraft/recipes").toURI());
                }
                else
                {
                    if (!"jar".equals(uri.getScheme()))
                    {
                        LOGGER.error("Unsupported scheme " + uri + " trying to list all recipes");
                        return false;
                    }

                    filesystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
                    path = filesystem.getPath("/assets/minecraft/recipes");
                }

                Iterator<Path> iterator = Files.walk(path).iterator();

                while (iterator.hasNext())
                {
                    Path path1 = iterator.next();

                    if ("json".equals(FilenameUtils.getExtension(path1.toString())))
                    {
                        Path path2 = path.relativize(path1);
                        String s = FilenameUtils.removeExtension(path2.toString()).replaceAll("\\\\", "/");
                        ResourceLocation resourcelocation = new ResourceLocation(s);
                        BufferedReader bufferedreader = null;

                        try
                        {
                            try
                            {
                                bufferedreader = Files.newBufferedReader(path1);
                                //register(s, parseRecipeJson((JsonObject)JsonUtils.fromJson(gson, bufferedreader, JsonObject.class)));
                            }
                            catch (JsonParseException jsonparseexception)
                            {
                                LOGGER.error("Parsing error loading recipe " + resourcelocation, jsonparseexception);
                                return false;
                            }
                            catch (IOException ioexception)
                            {
                                LOGGER.error("Couldn't read recipe " + resourcelocation + " from " + path1, (ioexception);
                                return false;
                            }
                        }
                        finally
                        {
                            IOUtils.closeQuietly(bufferedreader);
                        }
                    }
                }

                return true;
            }

            LOGGER.error("Couldn't find .mcassetsroot");
            flag1 = false;
        }
        catch (IOException | URISyntaxException urisyntaxexception)
        {
            LOGGER.error("Couldn't get a list of all recipe files", urisyntaxexception);
            flag1 = false;
            return flag1;
        }
        finally
        {
            IOUtils.closeQuietly(filesystem);
        }

        return flag1;
    }

    private static IRecipe parseRecipeJson(JsonObject recipe)
    {
        String s = JsonUtils.getString(recipe, "type");

        if ("crafting_shaped".equals(s))
        {
            //TODO maybe not necessary
            //switch to ShapedRecipes
            return ShapedRecipes.deserialize(recipe);
        }
        else if ("crafting_shapeless".equals(s))
        {
            return ShapelessRecipes.deserialize(recipe);
       }
        else
        {
            throw new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'");
        }
    }

    public static void saveRecipe(EntityPlayer entityPlayer, ScrambleBenchRecipe recipe)
    {
        NBTTagCompound entityTags = entityPlayer.getEntityData();
        NBTTagList scrambleTags =entityTags.getTagList("ScrambleBench", Constants.NBT.TAG_COMPOUND);
        int i =0;
        scrambleTags.appendTag(recipe.serializeNBT(scrambleTags.tagCount()));
        entityTags.setTag("ScrambleBench",scrambleTags);
    }

    public static void updateRecipe(EntityPlayer entityPlayer,ScrambleBenchRecipe recipe,int index)
    {
        NBTTagCompound entityTags = entityPlayer.getEntityData();
        NBTTagList scrambleTags =entityTags.getTagList("ScrambleBench", Constants.NBT.TAG_COMPOUND);
        if(scrambleTags.tagCount()==0)
        {

            for (int i = 0; i < scrambleTags.tagCount(); i++) {
                if(scrambleTags.getCompoundTagAt(i).getInteger("index")==index)
                    scrambleTags.set(i,recipe.serializeNBT(index));

            }
            entityTags.setTag("ScrambleBench",scrambleTags);
        }
    }

    public static void saveAllRecipes(EntityPlayer entityPlayer, List<ScrambleBenchRecipe> recipes)
    {
        NBTTagCompound entityTags = entityPlayer.getEntityData();
        NBTTagList scrambleTags =entityTags.getTagList("ScrambleBench", Constants.NBT.TAG_COMPOUND);
        if(scrambleTags.tagCount()==0)
        {
            int i =0;
            for (ScrambleBenchRecipe sbr:recipes) {
                scrambleTags.appendTag(sbr.serializeNBT(i));
                i++;
            }
            entityTags.setTag("ScrambleBench",scrambleTags);
        }
    }

    public static List<ScrambleBenchRecipe> loadRecipes(EntityPlayer entityPlayer)
    {
        List<ScrambleBenchRecipe> recipes =new ArrayList<>();
        NBTTagCompound entityData = entityPlayer.getEntityData();
        if (entityData.hasKey("ScrambleBench", Constants.NBT.TAG_COMPOUND)) {
            NBTTagList modData = entityData.getTagList("ScrambleBench", Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < modData.tagCount(); i++) {
                recipes.add(modData.getCompoundTagAt(i).getInteger("index"), new ScrambleBenchRecipe(modData.getCompoundTagAt(i)));
            }
        }
        return recipes;
    }

    /**
     * Retrieves an ItemStack that has multiple recipes for it.
     */
    public static ItemStack findMatchingResult(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : REGISTRY)
            if (irecipe.matches(craftMatrix, worldIn))
                return irecipe.getCraftingResult(craftMatrix);

        return ItemStack.EMPTY;
    }

    @Nullable
    public static IRecipe findMatchingRecipe(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : REGISTRY)
            if (irecipe.matches(craftMatrix, worldIn)) {
                return irecipe;
                //ItemStack it = irecipe.getRecipeOutput();
                //return new ShapedRecipes(irecipe.getGroup(), craftMatrix.getWidth(), craftMatrix.getHeight(), irecipe.getIngredients(), it);
            }
        return null;
    }

    public static List<IRecipe> findRecipesUsedIn(Item result)
    {
        List<IRecipe> listRecipes =new ArrayList<>();
        for (IRecipe irecipe : REGISTRY)
            for (Ingredient ing:
                 irecipe.getIngredients()) {
                for (ItemStack itemStack:
                     ing.getMatchingStacks()) {
                    if(itemStack.getItem() == result)
                        listRecipes.add(irecipe);
                }
            }
        return listRecipes;
    }

    private static boolean doesItemCraftResult(InventoryCrafting inventoryCrafting,Item parent,Item child) {
        if (child == null || parent == null ||
            parent == Items.AIR || child == Items.AIR
        )
            return false;
        else if (parent == child)
            return true;

        for (IRecipe irecipe : REGISTRY)//ALL items
        {
            if(irecipe.getRecipeOutput().getItem() ==Items.AIR)
                return false;

            try {
                ItemStack testStack = irecipe.getCraftingResult(inventoryCrafting);

                Item testItem = testStack.getItem();
                if (testStack != ItemStack.EMPTY ||//not empty
                        testItem != Items.AIR//not empty
                        //child is never result
                )
                    for (Ingredient ing : irecipe.getIngredients())//all ingredients
                        for (ItemStack itemStack : ing.getMatchingStacks())//ingredient ugliness
                            if (itemStack.getItem() == parent)//parent creates child
                                return true;
            } catch (Exception e) {
                throw new Error(irecipe.getRecipeOutput().getDisplayName() + " could not find crafting result");
            }
        }
        return false;
    }

    public static boolean areItemsRelated(InventoryCrafting inventoryCrafting, Item a, Item b)
    {
        return doesItemCraftResult(inventoryCrafting,a,b) ||
        doesItemCraftResult(inventoryCrafting,b,a);
    }

    public static NonNullList<ItemStack> getRemainingItems(InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : REGISTRY)
            if (irecipe.matches(craftMatrix, worldIn))
                return irecipe.getRemainingItems(craftMatrix);

        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(craftMatrix.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i)
            nonnulllist.set(i, craftMatrix.getStackInSlot(i));

        return nonnulllist;
    }

    @Nullable
    public static IRecipe getRecipe(ResourceLocation name)
    {
        return REGISTRY.getObject(name);
    }

    @Deprecated //DO NOT USE THIS
    public static int getIDForRecipe(IRecipe recipe)
    {
        return REGISTRY.getIDForObject(recipe);
    }

    @Deprecated //DO NOT USE THIS
    @Nullable
    public static IRecipe getRecipeById(int id)
    {
        return REGISTRY.getObjectById(id);
    }
}
