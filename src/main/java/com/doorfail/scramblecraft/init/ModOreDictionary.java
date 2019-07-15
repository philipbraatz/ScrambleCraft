package com.doorfail.scramblecraft.init;

import net.minecraftforge.oredict.OreDictionary;

public class ModOreDictionary {
    public static void registerOres()
    {
        OreDictionary.registerOre("craftingTable", ModBlocks.SCRAMBLE_BENCH);
        OreDictionary.registerOre("furnace", ModBlocks.SCRAMBLE_FURNACE_OFF);
        OreDictionary.registerOre("furnace", ModBlocks.SCRAMBLE_FURNACE_ON);
    }
}
