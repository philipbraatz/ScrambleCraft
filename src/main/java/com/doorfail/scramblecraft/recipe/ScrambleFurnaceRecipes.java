package com.doorfail.scramblecraft.recipe;

import java.util.Map;
import java.util.Map.Entry;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.init.ModItems;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ScrambleFurnaceRecipes
{
    private static final ScrambleFurnaceRecipes COOKING_BASE = new ScrambleFurnaceRecipes();
    private static Map<ItemStack, ItemStack> cookingList = Maps.newHashMap();
    private static Map<ItemStack, Float> experienceList = Maps.newHashMap();

    public static ScrambleFurnaceRecipes instance()
    {
        return COOKING_BASE;
    }

    private ScrambleFurnaceRecipes()
    {
        this.addRecipe(ModBlocks.RUBY_ORE, new ItemStack(ModItems.RUBY), 0.3f);
    }

    public void addRecipe(Block input, ItemStack stack, float experience)
    {
        this.addRecipe(Item.getItemFromBlock(input), stack, experience);
    }

    public void addRecipe(Item input, ItemStack stack, float experience)
    {
        this.addRecipe(new ItemStack(input, 1, 32767), stack, experience);
    }

    public void addRecipe(ItemStack input, ItemStack stack, float experience)
    {
        if (getResult(input) != ItemStack.EMPTY)
        {
            net.minecraftforge.fml.common.FMLLog.log.info("Ignored cooking recipe with conflicting input: {} = {}", input, stack); return;
        }
        this.cookingList.put(input, stack);
        this.experienceList.put(stack, Float.valueOf(experience));
    }

    public ItemStack getResult(ItemStack stack)
    {
        for (Entry<ItemStack, ItemStack> entry : this.cookingList.entrySet())
        {
            if (this.compareItemStacks(stack, entry.getKey()))
            {
                return entry.getValue();
            }
        }

        return ItemStack.EMPTY;
    }

    private boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }

    public Map<ItemStack, ItemStack> getCookingList()
    {
        return this.cookingList;
    }

    public float getCookingExperience(ItemStack stack)
    {
        float ret = stack.getItem().getSmeltingExperience(stack);
        if (ret != -1) return ret;
        for (Entry<ItemStack, Float> entry : this.experienceList.entrySet())
        {
            if (this.compareItemStacks(stack, entry.getKey()))
            {
                return ((Float)entry.getValue()).floatValue();
            }
        }
        return 0.0F;
    }
}
