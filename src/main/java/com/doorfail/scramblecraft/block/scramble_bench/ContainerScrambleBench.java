package com.doorfail.scramblecraft.block.scramble_bench;


import com.doorfail.scramblecraft.init.ModRecipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.world.World;

import static com.doorfail.scramblecraft.init.ModRecipes.recipeExists;
import static com.doorfail.scramblecraft.init.ModRecipes.recipes;

public class ContainerScrambleBench extends Container
{
    /** The crafting matrix inventory (3x3). */
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    private InventoryCraftResult craftResult = new InventoryCraftResult();
    //private final World world;
    /** Position of the workbench */
    //private final BlockPos pos;
    private final EntityPlayer player;
    private TileEntityScrambleBench benchInventory;


    public ContainerScrambleBench(InventoryPlayer playerInv, TileEntityScrambleBench benchInventory, EntityPlayer player)
    {
        this.player = playerInv.player;
        this.benchInventory = benchInventory;

        if(recipes.size() <= 0)
            recipes = ScrambleCraftingManager.loadRecipes(this.player);

        //output
        this.addSlotToContainer(new SlotCrafting(playerInv.player, this.craftMatrix, this.craftResult, 0, 124, 35));

        //3x3 grid
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        //inventory 9x3
        for (int k = 0; k < 3; ++k)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.addSlotToContainer(new Slot(playerInv, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        //hotbar 9 slots
        for (int l = 0; l < 9; ++l)
        {
            this.addSlotToContainer(new Slot(playerInv, l, 8 + l * 18, 142));
        }
    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    public void onCraftMatrixChanged(IInventory inventoryIn)
    {
        this.slotChangedCraftingGrid(player.world, this.player, this.craftMatrix, this.craftResult);
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        if (!playerIn.world.isRemote)
        {
            //this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
        }
        benchInventory.closeInventory(playerIn);
        super.onContainerClosed(playerIn);
    }

    @Override
    protected void slotChangedCraftingGrid(World world, EntityPlayer entityPlayer, InventoryCrafting craftingGrid, InventoryCraftResult craftResult)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entityPlayer;
            IRecipe  gridRecipe = ScrambleCraftingManager.findMatchingRecipe(craftingGrid, world);
            if(gridRecipe != null) {
                ShapedRecipes shapedRecipe = new ShapedRecipes("scramble", craftingGrid.getWidth(), craftingGrid.getHeight(), gridRecipe.getIngredients(), gridRecipe.getRecipeOutput());

                if ((gridRecipe.isDynamic() ||
                        !world.getGameRules().getBoolean("doLimitedCrafting") ||
                        entityplayermp.getRecipeBook().isUnlocked(gridRecipe))) {
                    //set ingredients used
                    craftResult.setRecipeUsed(gridRecipe);

                    //add new
                    if (!recipeExists(ModRecipes.getIngredientAsItems(shapedRecipe.getIngredients())) && gridRecipe.getRecipeOutput().getItem() != Items.AIR) {
                        ItemStack wholeItem = gridRecipe.getRecipeOutput();
                        if (wholeItem.getCount() < 1)
                            wholeItem.setCount(1);
                        ModRecipes.addRecipe(entityPlayer,ModRecipes.getIngredientAsItems(shapedRecipe.getIngredients()), wholeItem);

                        //DEBUG
                        //player.inventory.setInventorySlotContents(0,new ItemStack(Items.REDSTONE));//added
                        //entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, new ItemStack(Items.REDSTONE)));
                    } else {//not added
                        //player.inventory.setInventorySlotContents(0,new ItemStack(Items.SUGAR));
                        //entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, new ItemStack(Items.SUGAR)));
                    }
                }

                ItemStack returnItem = ModRecipes.getOutput(entityPlayer,ModRecipes.getIngredientAsItems(shapedRecipe.getIngredients()));
                if (returnItem.getCount() < 1)
                    returnItem.setCount(1);
                else if(returnItem.getCount() >= ModRecipes.previousStackSize *2)//Temperary fix for output craft doubling
                    returnItem.setCount(ModRecipes.previousStackSize);//will still dupe if output is less than double
                ModRecipes.previousStackSize = returnItem.getCount();

                //server side
                craftResult.setInventorySlotContents(0, returnItem);
                //visual inside crafting table
                entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, returnItem));
            }
            else
            {
                craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                //visual inside crafting table
                entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0,  ItemStack.EMPTY));
            }
        }
    }

    /**
     * Determines whether supplied player can use this container
     */

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.benchInventory.isUsableByPlayer(playerIn);
    }


    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.craftMatrix.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.craftMatrix.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.craftMatrix.getSizeInventory(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
