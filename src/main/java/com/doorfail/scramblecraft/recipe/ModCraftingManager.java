package com.doorfail.scramblecraft.recipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModCraftingManager extends CraftingManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static ResourceLocation lastCrafterUsed;

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

    @Nullable
    public static ModRecipe findMatchingRecipe(UUID playerId, ResourceLocation craftingBlock, Container self,int index)
    {
        InventoryCrafting inv = new InventoryCrafting(self,1,1);
        inv.setInventorySlotContents(0,self.getSlot(index).getStack());

        return findMatchingRecipe(playerId,craftingBlock,inv);
    }
    @Nullable
    public static ModRecipe findMatchingRecipe(UUID playerId, ResourceLocation craftingBlock, InventoryCrafting craftMatrix)
    {
        //Dont bother checking if inventory is empty
        boolean empty =true;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            if(craftMatrix.getStackInSlot(i).getCount() != 0 &&
                    craftMatrix.getStackInSlot(i) != ItemStack.EMPTY)
                empty = false;
        }
        if(empty)
            return ModRecipe.EMPTY(craftingBlock);//inventory was empty

        //try to match every recipe
        List<ModRecipe> iter = ModRecipeRegistry.getRecipeList(playerId,craftingBlock);
        if(iter != null || iter.size() != 0) {
            IRecipe irecipe;
            for (ModRecipe recipe: iter) {
                if(recipe.matches(craftMatrix, null))
                    return recipe;
            }
        }
        return ModRecipe.EMPTY(craftingBlock);//Not a valid recipe
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
