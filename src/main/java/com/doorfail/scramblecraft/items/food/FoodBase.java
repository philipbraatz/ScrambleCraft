package com.doorfail.scramblecraft.items.food;

import com.doorfail.scramblecraft.ScrambleCraft;
import com.doorfail.scramblecraft.init.ModItems;
import com.doorfail.scramblecraft.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class FoodBase extends ItemFood implements IHasModel
{

    public FoodBase(String name, int amount, float saturation, boolean isAnimalFood)
    {
        super(amount, saturation, isAnimalFood);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.FOOD);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels()
    {
        ScrambleCraft.proxy.registerItemRenderer(this, 0, "inventory");
    }

}