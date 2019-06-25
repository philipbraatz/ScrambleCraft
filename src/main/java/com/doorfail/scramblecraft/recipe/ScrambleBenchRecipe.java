package com.doorfail.scramblecraft.recipe;

import com.doorfail.scramblecraft.block.scramble_bench.ScrambleCraftingManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ScrambleBenchRecipe {
    private Logger logger;

    public ItemStack outputItemStack;
    public List<Item> inputItems = new ArrayList<>();
    private int cooldown =0;
    private int count=0;

    public ScrambleCraftingManager craftingManager = new ScrambleCraftingManager();

    /** An NBTTagCompound containing data about an ItemStack. */
    private NBTTagCompound stackTagCompound;

    public ScrambleBenchRecipe( List<Item> inputs,ItemStack output)
    {
        try {
            if (ItemStack.EMPTY.getItem() != output.getItem() && inputs.size() > 0) {
                inputItems = inputs;
                outputItemStack = output;
                ResetCooldown();
            }
            else
                throw new InvalidParameterException("inputs cannot be empty and output cannot be empty");
        } catch (Exception e)
        {
            throw e;
        }
    }
    public ScrambleBenchRecipe(NBTTagCompound compound)
    {
        deserializeNBT(compound);
    }

    public void ResetCooldown()
    {
        //set cooldown to lowest value
        cooldown =0;
        IncreaseCooldown();
    }
    public void IncreaseCooldown()
    {
        cooldown+= inputItems.size()+outputItemStack.getCount();//cooldown += how big the craft is + the normal output count
        count =0;
    }

    public ItemStack craftItem()
    {
        count++;
        return outputItemStack;
    }

    public boolean IsReady()
    {
        return count >cooldown;
    }

    //returns new cooldown time
    public int setOutput(ItemStack newItemStack)
    {
        outputItemStack = newItemStack;
        IncreaseCooldown();
        return cooldown;
    }

    public void deserializeNBT(NBTTagCompound compound)
    {
        this.outputItemStack = compound.hasKey("output", Constants.NBT.TAG_COMPOUND) ?
                new ItemStack(compound.getCompoundTag("output"))
                : ItemStack.EMPTY; //Forge fix tons of NumberFormatExceptions that are caused by deserializing EMPTY ItemStacks.

        this.cooldown =compound.getInteger("cooldown");
        this.count =compound.getInteger("count");

        if(compound.hasKey("ingredients", Constants.NBT.TAG_LIST)) {
            NBTTagList ingredientNBTList = compound.getTagList("ingredients", Constants.NBT.TAG_STRING);
            for (NBTBase b : ingredientNBTList)
                this.inputItems.add(Item.getByNameOrId(b.toString()));
        }
    }

    public NBTTagCompound serializeNBT(int index)
    {
        NBTTagCompound ret = new NBTTagCompound();
        this.writeToNBT(ret,index);
        return ret;
    }

    /**
     * Write the Recipe fields to a NBT object. Return the new NBT object.
     * Takes the index of a list optionally
     */
    public NBTTagCompound writeToNBT(NBTTagCompound nbt,Integer _index)
    {
        _index = _index == null? -1 : 0;
        //output
        NBTTagCompound outputItemStackNBT = outputItemStack.serializeNBT();

        //input
        NBTTagList ingredientsNBT = new NBTTagList();
        for (Item it:inputItems
             ) {
            NBTBase itBase = new NBTTagString(it.getRegistryName().toString());
            ingredientsNBT.appendTag(itBase);
        }

        //counters
        NBTTagInt cooldownNBT = new NBTTagInt(cooldown);
        NBTTagInt countNBT = new NBTTagInt(count);
        NBTTagInt index =  new NBTTagInt(_index);

        //set NBT
        nbt.setTag("output",outputItemStackNBT);
        nbt.setTag("ingredients",ingredientsNBT);
        nbt.setTag("cooldown",cooldownNBT);
        nbt.setTag("count",countNBT);
        nbt.setTag("index",index);

        return nbt;
    }
}
