package com.doorfail.scramblecraft.init;

import net.minecraftforge.oredict.OreDictionary;

public class ModOreDictionary {
    public static void registerOres()
    {
        OreDictionary.registerOre("ingotObsidian", ModItems.OBSIDIAN_INGOT);
        OreDictionary.registerOre("ingotRuby", ModItems.RUBY);
        OreDictionary.registerOre("blockRuby", ModBlocks.RUBY_ORE);
        OreDictionary.registerOre("blockRuby", ModBlocks.RUBY_BLOCK);
    }
}
