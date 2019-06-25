package com.doorfail.scramblecraft.util.handlers;

import com.doorfail.scramblecraft.block.scramble_bench.ContainerScrambleBench;
import com.doorfail.scramblecraft.block.scramble_bench.GUIScrambleBench;
import com.doorfail.scramblecraft.block.scramble_bench.TileEntityScrambleBench;
import com.doorfail.scramblecraft.block.scramble_furnace.ContainerScrambleFurnace;
import com.doorfail.scramblecraft.block.scramble_furnace.GUIScrambleFurnace;
import com.doorfail.scramblecraft.block.scramble_furnace.TileEntityScrambleFurnace;
import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == Reference.GUI_SCRAMBLE_FURNACE)
            return new ContainerScrambleFurnace(player.inventory, (TileEntityScrambleFurnace) world.getTileEntity(new BlockPos(x, y, z)));
        else if(ID == Reference.GUI_SCRAMBLE_BENCH)
            return new ContainerScrambleBench(player.inventory, (TileEntityScrambleBench) world.getTileEntity(new BlockPos(x, y, z)),player);
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == Reference.GUI_SCRAMBLE_FURNACE)
            return new GUIScrambleFurnace(player.inventory, (TileEntityScrambleFurnace) world.getTileEntity(new BlockPos(x, y, z)));
        else if(ID == Reference.GUI_SCRAMBLE_BENCH)
            return new GUIScrambleBench(player.inventory, (TileEntityScrambleBench) world.getTileEntity(new BlockPos(x, y, z)),player);
        return null;
    }

    public static void registerGuis()
    {

    }
}
