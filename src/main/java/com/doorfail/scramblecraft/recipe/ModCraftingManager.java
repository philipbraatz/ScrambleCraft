package com.doorfail.scramblecraft.recipe;

import com.google.gson.*;
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

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

public class ModCraftingManager extends CraftingManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ResourceLocation lastCrafterUsed;

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
                                LOGGER.error("Couldn't read recipe " + resourcelocation + " from " + path1, (ioexception));
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

    public static void saveRecipe(EntityPlayer entityPlayer,ResourceLocation craftingBlock, ModRecipe recipe)
    {
        lastCrafterUsed =craftingBlock;
        NBTTagCompound entityTags = entityPlayer.getEntityData();
        NBTTagList craftingTags =entityTags.getTagList(craftingBlock.toString(), Constants.NBT.TAG_COMPOUND);
        int i =0;
        craftingTags.appendTag(recipe.serializeNBT(craftingTags.tagCount()));
        entityTags.setTag(craftingBlock.toString(),craftingTags);
    }

    public static void updateRecipe(EntityPlayer entityPlayer,ResourceLocation craftingBlock,ModRecipe recipe,int index)
    {
        lastCrafterUsed =craftingBlock;
        NBTTagCompound entityTags = entityPlayer.getEntityData();
        NBTTagList craftingTags =entityTags.getTagList(craftingBlock.toString(), Constants.NBT.TAG_COMPOUND);
        if(craftingTags.tagCount()==0)
        {

            for (int i = 0; i < craftingTags.tagCount(); i++) {
                if(craftingTags.getCompoundTagAt(i).getInteger("index")==index)
                    craftingTags.set(i,recipe.serializeNBT(index));
            }
            entityTags.setTag(craftingBlock.toString(),craftingTags);
        }
    }

//    public static void saveAllRecipes(EntityPlayer entityPlayer, List<ModRecipe> recipes)
//    {
//        NBTTagCompound entityTags = entityPlayer.getEntityData();
//        NBTTagList scrambleTags =entityTags.getTagList("ScrambleBench", Constants.NBT.TAG_COMPOUND);
//        if(scrambleTags.tagCount()==0)
//        {
//            int i =0;
//            for (ScrambleBenchRecipe sbr:recipes) {
//                scrambleTags.appendTag(sbr.serializeNBT(i));
//                i++;
//            }
//            entityTags.setTag("ScrambleBench",scrambleTags);
//        }
//    }

    public static void loadRecipes(EntityPlayer entityPlayer, ResourceLocation craftingBlock)
    {
        lastCrafterUsed =craftingBlock;
        List<ResourceLocation> resources = new ArrayList<>();
        resources.add(craftingBlock);
        loadRecipes( entityPlayer, resources);
    }
    public static void loadRecipes(EntityPlayer entityPlayer, List<ResourceLocation> craftingBlocks)
    {
        NBTTagCompound entityData = entityPlayer.getEntityData();
        for (ResourceLocation cBlock:craftingBlocks)
            if (entityData.hasKey(cBlock.toString(), Constants.NBT.TAG_COMPOUND))
            {
                NBTTagList craftingBlockData = entityData.getTagList(cBlock.toString(), Constants.NBT.TAG_COMPOUND);

                for (int i = 0; i < craftingBlockData.tagCount(); i++)
                    ModRecipeRegistry.addRecipe(
                        craftingBlockData.getCompoundTagAt(i).getInteger("index"),
                        entityPlayer.getUniqueID(),
                        new ModRecipe(craftingBlockData.getCompoundTagAt(i)));
            }
    }

    /**
     * Retrieves an ItemStack that has multiple recipes for it.
     */
    public static ItemStack findMatchingResult(UUID playerId,ResourceLocation craftingBlock,InventoryCrafting craftMatrix, World worldIn)
    {
        for (IRecipe irecipe : ModRecipeRegistry.getRecipeList(playerId,craftingBlock))
            if (irecipe.matches(craftMatrix, worldIn))
                return irecipe.getCraftingResult(craftMatrix);

        return ItemStack.EMPTY;
    }

    @Nullable
    public static ModRecipe findMatchingRecipe(UUID playerId,ResourceLocation craftingBlock,InventoryCrafting craftMatrix)
    {
        return ModRecipeRegistry.getRecipe(playerId,craftingBlock,ModRecipe.inventoryToItemStackList(craftMatrix));
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

    private static boolean doesChildCraftGrandParent(InventoryCrafting inventoryCrafting,Item grandparent,Item child)
    {
        if (child == null || grandparent == null ||
                grandparent == Items.AIR || child == Items.AIR
        )
            return false;
        else if (grandparent == child)
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
                            if (itemStack.getItem() == grandparent)//is grandparent in its -> own recipe
                                return true;
                            else if(doesChildCraftParent(inventoryCrafting,grandparent,itemStack.getItem()))
                                return true;//is a Item from its -> own recipe made ingredients from -> that ingredients recipe
            } catch (Exception e) {
                throw new Error(irecipe.getRecipeOutput().getDisplayName() + " could not find crafting result");
            }
        }
        return false;
    }
    private static boolean doesChildCraftParent(InventoryCrafting inventoryCrafting,Item parent,Item child) {
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
        //up to 2 generations!
        return doesChildCraftGrandParent(inventoryCrafting,a,b) ||//A,B
                doesChildCraftGrandParent(inventoryCrafting,b,a);//B,A
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
        lastCrafterUsed =name;
        return REGISTRY.getObject(name);
    }
}
