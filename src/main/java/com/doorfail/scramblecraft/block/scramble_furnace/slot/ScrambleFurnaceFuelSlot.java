package com.doorfail.scramblecraft.block.scramble_furnace.slot;

import com.doorfail.scramblecraft.block.scramble_furnace.TileEntityScrambleFurnace;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ScrambleFurnaceFuelSlot extends Slot
{
    public ScrambleFurnaceFuelSlot(IInventory inventoryIn, int slotIndex, int xPosition, int yPosition)
    {
        super(inventoryIn, slotIndex, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return TileEntityScrambleFurnace.isItemFuel(stack) || isBucket(stack);
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return isBucket(stack) ? 1 : super.getItemStackLimit(stack);
    }

    public static boolean isBucket(ItemStack stack)
    {
        return stack.getItem() == Items.BUCKET;
    }
}