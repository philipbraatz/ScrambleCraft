package com.doorfail.scramblecraft.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class RegisterUtil
{
    public static void registerALL(FMLPreInitializationEvent event )
    {

    }
    private static void registerBlocks(FMLPreInitializationEvent event, Block...blocks)
    {
        for (Block block:blocks) {
            final ItemBlock itemBlock = new ItemBlock(block);
            if(event.getSide() == Side.CLIENT)
            {
                //ForgeRegistries.register((TileEntity)block.);
                //GameRegistry.registerTileEntity(itemBlock,block.getRegistryName());
            }
        }
    }
    private static void registerItems(FMLPreInitializationEvent event, Item...items)
    {

    }
}
