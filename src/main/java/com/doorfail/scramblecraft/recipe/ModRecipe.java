package com.doorfail.scramblecraft.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

//single custom made recipe
public class ModRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private Logger logger;

    public ResourceLocation craftingMachine;

    private List<ItemStack> outputItemStacks= new ArrayList<>();
    private List<ItemStack> inputItemStacks = new ArrayList<>();
    private int coolDown =0;
    private int count=0;

    /** An NBTTagCompound containing data about an ItemStack. */
    private NBTTagCompound stackTagCompound;

    public ModRecipe(ResourceLocation craftingBlock, List<ItemStack> inputs,List<ItemStack> outputs,boolean canScramble)
    {
        SetComponents(craftingBlock,inputs,outputs,canScramble,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, List<ItemStack> inputs,List<ItemStack> outputs)
    {
        SetComponents(craftingBlock,inputs,outputs,true,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, ItemStack input,List<ItemStack> outputs)
    {
        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(input);
        SetComponents(craftingBlock,ingredients,outputs,true,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, List<ItemStack> inputs,ItemStack output)
    {
        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(output);
        SetComponents(craftingBlock,inputs,ingredients,true,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, ItemStack input,ItemStack output)
    {
        List<ItemStack> ingredients = new ArrayList<>();
        List<ItemStack> outgoing = new ArrayList<>();
        ingredients.add(input);
        outgoing.add(output);
        SetComponents(craftingBlock,ingredients,outgoing,true,true);
    }
    private ModRecipe(ResourceLocation craftingBlock, List<ItemStack> inputs,List<ItemStack> outputs,boolean NA_cantScramble,boolean bypass)
    {
        SetComponents(craftingBlock,inputs,outputs,false,true);
    }

    public List<ItemStack> getInputItemStacks()
    {
        return inputItemStacks;
    }

    private void SetComponents(ResourceLocation craftingBlock, List<ItemStack> inputs,List<ItemStack> outputs,boolean canScramble,boolean bypass)
    {
        craftingMachine =craftingBlock;

        try {
            boolean isEmpty = true;
            for (ItemStack it:inputs
            ) {
                if(it != ItemStack.EMPTY)
                    isEmpty =false;
            }
            if(isEmpty && !bypass)
                throw new InvalidParameterException("inputs cannot be empty");

            isEmpty =true;
            for (ItemStack it:outputs
            ) {
                if(it != ItemStack.EMPTY)
                    isEmpty =false;
            }
            if(isEmpty&& !bypass)
                throw new InvalidParameterException("outputs cannot be empty");

            inputItemStacks = inputs;
            outputItemStacks = outputs;
        } catch (Exception e)
        {
            throw e;
        }

        if(!canScramble)
            coolDown =-1;
        else
            coolDown =0;

        ResetCoolDown();
    }

    public ModRecipe(NBTTagCompound compound)
    {
        deserializeNBT(compound);
    }

    public void ResetCoolDown()
    {
        //set cooldown to lowest value
        if(coolDown != -1) {
            coolDown = 0;
            IncreaseCoolDown();
        }
    }
    public void IncreaseCoolDown()
    {
        if(coolDown != -1)
        {
            int coolDownIncrease=0;
            for (ItemStack it:inputItemStacks)
                coolDownIncrease += it.getCount();
            for (ItemStack it:outputItemStacks)
                coolDownIncrease += it.getCount();

            coolDown += inputItemStacks.size() + outputItemStacks.size();//cooldown += how big the craft is + the normal output count
            count = 0;
        }
    }

    //only use for comparisons
    public List<ItemStack> checkResult()
    {
        return outputItemStacks;
    }
    //only use when giving item to player
    public List<ItemStack> craftItem()
    {
        count++;
        return outputItemStacks;
    }

    public boolean IsReady()
    {
        return count >coolDown;
    }

    //returns new cooldown time
    public int setNewOutput(List<ItemStack> newItemStack)
    {
        outputItemStacks = newItemStack;
        IncreaseCoolDown();
        return coolDown;
    }

    public void deserializeNBT(NBTTagCompound compound)
    {
        this.coolDown =compound.getInteger("cooldown");
        this.count =compound.getInteger("count");
        this.craftingMachine =new ResourceLocation(compound.getString("crafter"));

        if(compound.hasKey("ingredients", Constants.NBT.TAG_LIST)) {
            NBTTagList ingredientNBTList = compound.getTagList("ingredients", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i <ingredientNBTList.tagCount() ; i++) {
                this.inputItemStacks.add(new ItemStack(ingredientNBTList.getCompoundTagAt(i)));
            }
        }

        if(compound.hasKey("output", Constants.NBT.TAG_LIST)) {
            NBTTagList ingredientNBTList = compound.getTagList("output", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < ingredientNBTList.tagCount(); i++) {
                this.outputItemStacks.add(new ItemStack(ingredientNBTList.getCompoundTagAt(i)));
            }
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

        //input
        NBTTagList ingredientsNBT = new NBTTagList();
        for (ItemStack it:inputItemStacks)
            ingredientsNBT.appendTag(it.serializeNBT());

        //output
        NBTTagList outputItemStackNBT = new NBTTagList();
        for (ItemStack it:outputItemStacks)
            outputItemStackNBT.appendTag(it.serializeNBT());

        //counters
        NBTTagInt cooldownNBT = new NBTTagInt(coolDown);
        NBTTagInt countNBT = new NBTTagInt(count);
        NBTTagInt indexNBT =  new NBTTagInt(_index);

        //craftingMachine
        NBTTagString crafterNBT = new NBTTagString(craftingMachine.toString());

        //set NBT
        nbt.setTag("output",outputItemStackNBT);
        nbt.setTag("ingredients",ingredientsNBT);
        nbt.setTag("cooldown",cooldownNBT);
        nbt.setTag("count",countNBT);
        nbt.setTag("index",indexNBT);
        nbt.setTag("crafter",crafterNBT);

        return nbt;
    }

    public static List<ItemStack> inventoryToItemStackList(InventoryCrafting craftMatrix)
    {
        List<ItemStack> inventory =new ArrayList<>();
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            if(craftMatrix.getStackInSlot(i) != ItemStack.EMPTY)
                inventory.add( craftMatrix.getStackInSlot(i));
        }
        return inventory;
    }

    @Override
    public boolean matches(InventoryCrafting inventoryCrafting, World world) {
        return inventoryToItemStackList(inventoryCrafting) == this.outputItemStacks;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        if(matches(inventoryCrafting, Minecraft.getMinecraft().world))
            return craftItem().get(0);
        else
            return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int i, int i1) {
        return true;//not sure if needed or how to use
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.outputItemStacks.get(0);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
       NonNullList<Ingredient> list = NonNullList.create();
        for (ItemStack it:this.inputItemStacks
             ) {
            list.add(Ingredient.fromStacks(it));
        }
        return list;
    }


    public static ModRecipe EMPTY(ResourceLocation craftingBlock)
    {
        return new ModRecipe(craftingBlock,new ArrayList<ItemStack>(),new ArrayList<ItemStack>(),false,true);
    }
    public static ModRecipe EMTPY_INPUT(ResourceLocation craftingBlock,List<ItemStack> input)
    {
        //Uses bypass initialization
        return new ModRecipe(craftingBlock,input,new ArrayList<ItemStack>(),false,true);
    }
}
