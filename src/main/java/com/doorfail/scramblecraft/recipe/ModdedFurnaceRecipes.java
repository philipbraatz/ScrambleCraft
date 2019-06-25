package com.doorfail.scramblecraft.recipe;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;

public class ModdedFurnaceRecipes
{
    private static final ModdedFurnaceRecipes SMELTING_BASE = new ModdedFurnaceRecipes();
    /** The list of smelting results. */
    private final Map<ItemStack, ItemStack> smeltingList = Maps.<ItemStack, ItemStack>newHashMap();
    /** A list which contains how many experience points each recipe output will give. */
    private final Map<ItemStack, Float> experienceList = Maps.<ItemStack, Float>newHashMap();

    /**
     * Returns an instance of FurnaceRecipes.
     */
    public static ModdedFurnaceRecipes instance()
    {
        return SMELTING_BASE;
    }

    public ModdedFurnaceRecipes()
    {
        this.addSmeltingRecipeForBlock(Blocks.IRON_ORE, new ItemStack(Items.IRON_INGOT), 0.7F);

    }

    /**
     * Adds a smelting recipe, where the input item is an instance of Block.
     */
    public void addSmeltingRecipeForBlock(Block input, ItemStack stack, float experience)
    {
        this.addSmelting(Item.getItemFromBlock(input), stack, experience);
    }
    public void removeSmeltingRecipeForBlock(Block input, ItemStack stack, float experience)
    {
        this.removeSmelting(Item.getItemFromBlock(input), stack, experience);
    }
    public void switchSmeltingRecipes(Block a,Block b)
    {
        Block c =a;
        this.smeltingList.replace(new ItemStack(Item.getItemFromBlock(a), 1, 32767),new ItemStack(Item.getItemFromBlock(c), 1, 32767),new ItemStack(Item.getItemFromBlock(b), 1, 32767));
        this.smeltingList.replace(new ItemStack(Item.getItemFromBlock(a), 1, 32767),new ItemStack(Item.getItemFromBlock(a), 1, 32767),new ItemStack(Item.getItemFromBlock(b), 1, 32767));
    }

    /**
     * Adds a smelting recipe using an Item as the input item.
     */
    public void addSmelting(Item input, ItemStack stack, float experience)
    {
        this.addSmeltingRecipe(new ItemStack(input, 1, 32767), stack, experience);
    }
    public void removeSmelting(Item input, ItemStack stack, float experience)
    {
        this.removeSmeltingRecipe(new ItemStack(input, 1, 32767), stack, experience);
    }
    public void switchSmeltingRecipes(Item a,Item b)
    {
        Item c =a;
        this.smeltingList.replace(new ItemStack(a,1,32767),new ItemStack(c,1,327867),new ItemStack(b,1,32767));
        this.smeltingList.replace(new ItemStack(a,1,32767),new ItemStack(a,1,327867),new ItemStack(b,1,32767));
    }


    /**
     * Adds a smelting recipe using an ItemStack as the input for the recipe.
     */
    public void addSmeltingRecipe(ItemStack input, ItemStack stack, float experience)
{
    if (getSmeltingResult(input) != ItemStack.EMPTY) { net.minecraftforge.fml.common.FMLLog.log.info("Ignored smelting recipe with conflicting input: {} = {}", input, stack); return; }
    this.smeltingList.put(input, stack);
    this.experienceList.put(stack, Float.valueOf(experience));
}
    public void removeSmeltingRecipe(ItemStack input, ItemStack stack, float experience)
    {
        if (getSmeltingResult(input) != ItemStack.EMPTY) { net.minecraftforge.fml.common.FMLLog.log.info("Ignored smelting recipe with conflicting input: {} = {}", input, stack); return; }
        this.smeltingList.remove(input, stack);
        this.experienceList.remove(stack, Float.valueOf(experience));
    }
    public void switchSmeltingRecipes(ItemStack a,ItemStack b)
    {
        ItemStack c =a;
        this.smeltingList.replace(a,c,b);
        this.smeltingList.replace(a,a,b);
    }


    /**
     * Returns the smelting result of an item.
     */
    public ItemStack getSmeltingResult(ItemStack stack)
    {
        for (Map.Entry<ItemStack, ItemStack> entry : this.smeltingList.entrySet())
        {
            if (this.compareItemStacks(stack, entry.getKey()))
            {
                return entry.getValue();
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Compares two itemstacks to ensure that they are the same. This checks both the item and the metadata of the item.
     */
    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }

    public Map<ItemStack, ItemStack> getSmeltingList()
    {
        return this.smeltingList;
    }

    public float getSmeltingExperience(ItemStack stack)
    {
        float ret = stack.getItem().getSmeltingExperience(stack);
        if (ret != -1) return ret;

        for (Map.Entry<ItemStack, Float> entry : this.experienceList.entrySet())
        {
            if (this.compareItemStacks(stack, entry.getKey()))
            {
                return ((Float)entry.getValue()).floatValue();
            }
        }

        return 0.0F;
    }
}