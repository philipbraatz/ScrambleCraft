package com.doorfail.scramblecraft.handlers;

import com.doorfail.scramblecraft.block.scramble_bench.TileEntityScrambleBench;
import com.doorfail.scramblecraft.block.scramble_furnace.TileEntityScrambleFurnace;
import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityHandler {
    public static void registerTileEntities()
    {
        GameRegistry.registerTileEntity(TileEntityScrambleBench.class, new ResourceLocation(Reference.MODID + ":scramble_bench"));
        GameRegistry.registerTileEntity(TileEntityScrambleFurnace.class, new ResourceLocation(Reference.MODID + ":scramble_furnace"));
    }
}
