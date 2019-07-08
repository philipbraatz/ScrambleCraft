package com.doorfail.scramblecraft.items.armor;

import com.doorfail.scramblecraft.ScrambleCraft;
import com.doorfail.scramblecraft.init.ModItems;
import com.doorfail.scramblecraft.util.IHasModel;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;

public class ArmorBase extends ItemArmor implements IHasModel {

    public ArmorBase(String name, ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.COMBAT);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels()
    {
        ScrambleCraft.proxy.registerItemRenderer(this, 0, "inventory");
    }

}