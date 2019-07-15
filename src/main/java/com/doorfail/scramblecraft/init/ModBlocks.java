package com.doorfail.scramblecraft.init;

import com.doorfail.scramblecraft.block.scramble_bench.BlockScrambleBench;
import com.doorfail.scramblecraft.block.scramble_furnace.BlockScrambleFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks
{
    public static final List<Block> BLOCKS = new ArrayList<Block>();

    //Blocks
    public static final Block SCRAMBLE_FURNACE_OFF = new BlockScrambleFurnace("scramble_furnace_off",false).setCreativeTab(CreativeTabs.REDSTONE);
    public static final Block SCRAMBLE_FURNACE_ON = new BlockScrambleFurnace("scramble_furnace_on", true);
    public static final Block SCRAMBLE_BENCH = new BlockScrambleBench("scramble_bench");
}