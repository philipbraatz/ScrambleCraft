package com.doorfail.scramblecraft.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.Logger;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

//single custom made recipe
public class ModRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IShapedRecipe {
    private Logger logger;

    public ResourceLocation craftingMachine;

    private List<ItemStack> outputItemStacks= new ArrayList<>();
    private List<Ingredient> inputIngredients = new ArrayList<>();
    private int coolDown =0;
    private int count=0;
    private int width;
    private int height;

    /** An NBTTagCompound containing data about an ItemStack. */
    private NBTTagCompound stackTagCompound;

    public ModRecipe(ResourceLocation craftingBlock, List<Ingredient> inputs,List<ItemStack> outputs,int width, int height,boolean canScramble)
    {
        SetComponents(craftingBlock,inputs,outputs, width, height,canScramble,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, List<Ingredient> inputs,List<ItemStack> outputs,int width, int height)
    {
        SetComponents(craftingBlock,inputs,outputs, width,  height,true,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, Ingredient input,List<ItemStack> outputs,int width, int height)
    {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(input);
        SetComponents(craftingBlock,ingredients,outputs, width, height,true,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, List<Ingredient> inputs,ItemStack output,int width, int height)
    {
        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(output);
        SetComponents(craftingBlock,inputs,ingredients, width,  height,true,true);
    }
    public ModRecipe(ResourceLocation craftingBlock, Ingredient input,ItemStack output,int width, int height)
    {
        List<Ingredient> ingredients = new ArrayList<>();
        List<ItemStack> outgoing = new ArrayList<>();
        ingredients.add(input);
        outgoing.add(output);
        SetComponents(craftingBlock,ingredients,outgoing, width, height,true,true);
    }
    private ModRecipe(ResourceLocation craftingBlock, List<Ingredient> inputs,List<ItemStack> outputs,int width, int height,boolean NA_cantScramble,boolean bypass)
    {
        SetComponents(craftingBlock,inputs,outputs, width, height,false,true);
    }

    public List<Ingredient> getInputIngredients()
    {
        return inputIngredients;
    }

    private void SetComponents(ResourceLocation craftingBlock, List<Ingredient> inputs,List<ItemStack> outputs,int width, int height,boolean canScramble,boolean bypass)
    {
        craftingMachine =craftingBlock;
        this.width =width;
        this.height =height;
        if(outputs.size()>0)
            this.setRegistryName(outputs.get(0).getItem().getRegistryName());

        try {
            boolean isEmpty = true;
            for (Ingredient it:inputs
            ) {
                if(it != Ingredient.EMPTY)
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

            inputIngredients = inputs;
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
            int stacksCount= 0;
            for (ItemStack it:outputItemStacks)
                stacksCount += it.getCount();//normally just a single ItemStack

            coolDown += inputIngredients.size() + stacksCount;//coolDown += how big the craft is + the normal output count
            count = 0;
        }
    }

    //only use for comparisons
    public List<ItemStack> checkResult()
    {
        return outputItemStacks;
    }
    //only use when giving item to player
    public List<ItemStack> craftItem(EntityPlayer player, IInventory crafting, Container container)
    {
        count++;
        if(IsReady())
            ModRecipeRegistry.randomizeRecipe(crafting,player,craftingMachine,this.inputIngredients,container);
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
            for (int i = 0; i <ingredientNBTList.tagCount() ; i++)
                this.inputIngredients.add(Ingredient.fromStacks(new ItemStack(ingredientNBTList.getCompoundTagAt(i))));
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
        for (Ingredient it: inputIngredients)
            ingredientsNBT.appendTag((it).getMatchingStacks()[0].serializeNBT());//TODO serialize all ingredients getMatchingStacks()

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
                inventory.add( craftMatrix.getStackInSlot(i));
        }
        return inventory;
    }

    //@Override
    //public boolean matches(InventoryCrafting inv, World worldIn) {
    //    IShapedRecipe thisRecipe =this;
    //    return thisRecipe.matches(inv,worldIn);
    //}

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        for(int i = 0; i <= inv.getWidth() - this.width; ++i) {
            for(int j = 0; j <= inv.getHeight() - this.height; ++j) {
                if (this.checkMatch(inv, i, j, true)) {
                    return true;
                }
                if (this.checkMatch(inv, i, j, false)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkMatch(InventoryCrafting craftingInventory, int i, int j, boolean hasHeight) {


        for(int row = 0; row < craftingInventory.getWidth(); ++row) {
            for(int col = 0; col < craftingInventory.getHeight(); ++col) {
                int rowi = row - i;
                int colj = col - j;
                Ingredient ingredient = Ingredient.EMPTY;

                try {
                    //If indexes are still within inventory
                    if (rowi >= 0 && colj >= 0 &&
                            rowi < this.width && colj < this.height) {
                        if (hasHeight) {
                            ingredient = this.inputIngredients.get(this.width - rowi - 1 + colj * this.height);
                        } else {
                            ingredient = this.inputIngredients.get(rowi + colj * this.width);
                        }
                    }

                    if (!ingredient.apply(craftingInventory.getStackInRowAndColumn(row, col))) {
                        return false;
                    }
                }catch (IndexOutOfBoundsException e)
                {
                    //logger.warn("Out of bounds at row "+row+", col "+col);
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
        if(matches(inventoryCrafting, Minecraft.getMinecraft().world))
            return craftItem(Minecraft.getMinecraft().player,inventoryCrafting,null).get(0);
        else
            return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit(int i, int i1) {
        return i <=width && i1 <=height;//not sure if needed or how to use
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.outputItemStacks.get(0);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
       NonNullList<Ingredient> list = NonNullList.create();
        for (Ingredient it:this.inputIngredients
             ) {
            list.add(it);
        }
        return list;
    }


    public static ModRecipe EMPTY(ResourceLocation craftingBlock)
    {
        ModRecipe blank = new ModRecipe(craftingBlock,new ArrayList<Ingredient>(),new ArrayList<ItemStack>(),0,0,false,true);
        return blank;
    }
    public static ModRecipe EMTPY_INPUT(ResourceLocation craftingBlock,List<Ingredient> input,int width,int height)
    {
        //Uses bypass initialization
        return new ModRecipe(craftingBlock,input,new ArrayList<ItemStack>(),width,height,false,true);
    }

    @Override
    public int getRecipeWidth() {
        return width;
    }

    @Override
    public int getRecipeHeight() {
        return height;
    }

    public boolean equals(Object o)
    {
        if (o == this) return true;

        if ((o instanceof ModRecipe)) {
            ModRecipe c = (ModRecipe) o;
            return c.craftingMachine == this.craftingMachine &&
                c.inputIngredients.equals( this.inputIngredients) &&
                c.width == this.width &&
                c.height == this.height &&
                c.outputItemStacks.equals(this.outputItemStacks);
                //c.coolDown ==this.coolDown;
        }
        else
            return false;
    }
}
