package com.doorfail.scramblecraft.items.tools;

import com.doorfail.scramblecraft.ScrambleCraft;
import com.doorfail.scramblecraft.init.ModItems;
import com.doorfail.scramblecraft.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemAxe;

public class ToolAxe extends ItemAxe implements IHasModel {

    public ToolAxe(String name, ToolMaterial material)
    {
        super(material, 6.0F, -3.2F);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.MATERIALS);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels()
    {
        ScrambleCraft.proxy.registerItemRenderer(this, 0, "inventory");
    }
}