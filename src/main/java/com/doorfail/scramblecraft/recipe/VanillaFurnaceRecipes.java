package com.doorfail.scramblecraft.recipe;

import com.doorfail.scramblecraft.init.ModItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class VanillaFurnaceRecipes {
    public static void init()
    {
        GameRegistry.addSmelting(new ItemStack(Blocks.OBSIDIAN), new ItemStack(ModItems.OBSIDIAN_INGOT), 0.4F);
    }
}
