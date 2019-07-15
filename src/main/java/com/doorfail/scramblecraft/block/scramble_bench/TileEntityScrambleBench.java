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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.ArrayList;
import java.util.List;

public class TileEntityScrambleBench extends TileEntityLockableLoot implements ITickable, ISidedInventory
{
    private NonNullList<ItemStack> scrambleBenchMatrix = NonNullList.<ItemStack>withSize(9, ItemStack.EMPTY);
    private ItemStack scrambleBenchResult = ItemStack.EMPTY;
    private String customName;
    public int numPlayersUsing=0;

    @Override
    public int getSizeInventory()
    {
        return scrambleBenchMatrix.size()+1;
    }

    @Override
    public boolean isEmpty()
    {
        for(ItemStack stack : this.scrambleBenchMatrix)
            if (!stack.isEmpty())
                return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        //throw new Exception("Index is greater than size");
        if(index == scrambleBenchMatrix.size()+1)
            return scrambleBenchResult;
        else if(index >=scrambleBenchMatrix.size())
            return ItemStack.EMPTY;
        return this.scrambleBenchMatrix.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {


        if(index == scrambleBenchMatrix.size()+1)
        {
            List<ItemStack> single = new ArrayList<>();
            single.add(scrambleBenchResult);
            return ItemStackHelper.getAndSplit(single, index, count);
        }
        else
            return ItemStackHelper.getAndSplit(scrambleBenchMatrix, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        if(index == scrambleBenchMatrix.size()+1)
        {
            List<ItemStack> single = new ArrayList<>();
            single.add(scrambleBenchResult);
            return ItemStackHelper.getAndRemove(single, index);
        }
        else
            return ItemStackHelper.getAndRemove(scrambleBenchMatrix, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        ItemStack itemStack;
        boolean flag;
        if(index ==scrambleBenchMatrix.size()+1) {
            itemStack = scrambleBenchResult;
            flag = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackShareTagsEqual(stack, itemStack);
            scrambleBenchResult = stack;
        }
        else
        {
            itemStack = this.scrambleBenchMatrix.get(index);
            flag = !stack.isEmpty() && stack.isItemEqual(itemStack) && ItemStack.areItemStackShareTagsEqual(stack, itemStack);
            this.scrambleBenchMatrix.set(index, stack);
        }
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

    @Override
    public void updateContainingBlockInfo()
    {
        super.updateContainingBlockInfo();
    }

    @Override
    public void update()
    {

        if (!this.world.isRemote && this.numPlayersUsing > 0 )
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

            if (this.numPlayersUsing != 0 && (pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
            {
                this.numPlayersUsing = 0;
                f = 5.0F;

                for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double) ((float) pos.getX() - 5.0F), (double) ((float) pos.getY() - 5.0F), (double) ((float) pos.getZ() - 5.0F), (double) ((float) (pos.getX() + 1) + 5.0F), (double) ((float) (pos.getY() + 1) + 5.0F), (double) ((float) (pos.getZ() + 1) + 5.0F))))
                    if (entityplayer.openContainer instanceof ContainerScrambleBench)
                        if (( entityplayer.openContainer).getInventory() == this.scrambleBenchMatrix)
                            ++this.numPlayersUsing;
            }
        }
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if(this.world.getTileEntity(this.pos) != this)
            return false;
        else
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public void openInventory(EntityPlayer player)
    {
        ++this.numPlayersUsing;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
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
        if(index ==scrambleBenchMatrix.size()+1)
            return false;
        else
            return true;
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
        if(index == scrambleBenchMatrix.size()+1)
            return false;
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        if(direction == EnumFacing.DOWN && index ==scrambleBenchMatrix.size()+1)
            return true;
        else
            return false;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
    {
        return new ContainerScrambleBench(playerIn.world,this.pos,(TileEntityScrambleBench) world.getTileEntity(this.pos),playerIn);
    }

    @Override
    public void clear()
    {
        this.scrambleBenchMatrix.clear();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.scrambleBenchMatrix;
    }

    @Override
    @javax.annotation.Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @javax.annotation.Nullable net.minecraft.util.EnumFacing facing)
    {
        return super.getCapability(capability, facing);
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, this.scrambleBenchMatrix);

        if (compound.hasKey("CustomName", 8))
            this.customName = compound.getString("CustomName");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        ItemStackHelper.saveAllItems(compound, this.scrambleBenchMatrix);

        if (this.hasCustomName())
            compound.setString("CustomName", this.customName);

        return compound;
    }
}