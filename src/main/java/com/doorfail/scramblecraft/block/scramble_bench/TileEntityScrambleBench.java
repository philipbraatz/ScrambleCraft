package com.doorfail.scramblecraft.block.scramble_bench;

import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;

public class TileEntityScrambleBench extends TileEntityLockableLoot implements ITickable, ISidedInventory
{
    private NonNullList<ItemStack> scrambleBenchItemStacks = NonNullList.<ItemStack>withSize(10, ItemStack.EMPTY);
    private String customName;
    public int numPlayersUsing;

    @Override
    public int getSizeInventory()
    {
        return scrambleBenchItemStacks.size()-1;
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack stack : this.scrambleBenchItemStacks)
            if (!stack.isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        if(index > scrambleBenchItemStacks.size()) {
            return ItemStack.EMPTY;
            //throw new Exception("Index is greater than size");
        }
        return this.scrambleBenchItemStacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        return ItemStackHelper.getAndSplit(scrambleBenchItemStacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        return ItemStackHelper.getAndRemove(scrambleBenchItemStacks, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack itemStack = this.scrambleBenchItemStacks.get(index);
        boolean flag = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackShareTagsEqual(stack, itemStack);
        this.scrambleBenchItemStacks.set(index, stack);
        if(stack.getCount() > this.getInventoryStackLimit())
        {
            stack.setCount(this.getInventoryStackLimit());
        }

        if(index == 0 && !flag)
            this.markDirty();
    }

    /**
     * Get the name of this object. For players this returns their username
     */
    @Override
    public String getName()
    {
        return this.hasCustomName() ? this.customName : "container.scramble_bench";
    }
    @Override
    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }
    public void setCustomInventoryName(String benchCustomName)
    {
        this.customName = benchCustomName;
    }

    public String getGuiID()
    {
        return Reference.MODID +":scramble_bench";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }


    public void update()
    {
        if (!this.world.isRemote )//&& this.numPlayersUsing != 0 )
        {
            this.numPlayersUsing = 0;
            float f = 5.0F;

            for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)pos.getX() - 5.0F), (double)((float)pos.getY() - 5.0F), (double)((float)pos.getZ() - 5.0F), (double)((float)(pos.getX() + 1) + 5.0F), (double)((float)(pos.getY() + 1) + 5.0F), (double)((float)(pos.getZ() + 1) + 5.0F))))
            {
                if (entityplayer.openContainer instanceof ContainerScrambleBench)
                {
                        ++this.numPlayersUsing;
                }
            }
        }

        if (!this.world.isRemote)
        {
            if (!this.world.isRemote && this.numPlayersUsing != 0 && (pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
            {
                this.numPlayersUsing = 0;
                float f = 5.0F;

                for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double) ((float) pos.getX() - 5.0F), (double) ((float) pos.getY() - 5.0F), (double) ((float) pos.getZ() - 5.0F), (double) ((float) (pos.getX() + 1) + 5.0F), (double) ((float) (pos.getY() + 1) + 5.0F), (double) ((float) (pos.getZ() + 1) + 5.0F))))
                    if (entityplayer.openContainer instanceof ContainerScrambleBench)
                        if (((ContainerScrambleBench) entityplayer.openContainer).getInventory() == this.scrambleBenchItemStacks)
                            ++this.numPlayersUsing;
            }
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if(this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    public void openInventory(EntityPlayer player)
    {
        ++this.numPlayersUsing;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
    }

    public void closeInventory(EntityPlayer player)
    {
        --this.numPlayersUsing;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
    }


    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {

    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    public int[] getSlotsForFace(EnumFacing side)
    {
        return new int[]{0};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        if(direction == EnumFacing.DOWN && index == 1)
        {
            Item item = stack.getItem();
            if(item != Items.WATER_BUCKET && item != Items.BUCKET)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerScrambleBench(playerInventory,(TileEntityScrambleBench) world.getTileEntity(this.pos),playerIn);
    }

    @Override
    public void clear()
    {
        this.scrambleBenchItemStacks.clear();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.scrambleBenchItemStacks;
    }

    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
        return super.getCapability(capability, facing);
    }
}