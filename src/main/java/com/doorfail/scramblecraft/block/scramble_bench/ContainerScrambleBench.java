package com.doorfail.scramblecraft.block.scramble_bench;


import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModCraftingManager;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.doorfail.scramblecraft.util.Reference.MODID;

public class ContainerScrambleBench extends Container
{
    private static Logger logger = LogManager.getLogger(MODID);

    /** The crafting matrix inventory (3x3). */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    private InventoryCraftResult craftResult = new InventoryCraftResult();
    private final World world;
    /** Position of the workbench */
    private final BlockPos pos;
    private final EntityPlayer player;
    private TileEntityScrambleBench benchInventory;
    private boolean craftedLast = false;


    public ContainerScrambleBench(World world,BlockPos block, TileEntityScrambleBench benchInventory, EntityPlayer player)
    {
        this.world = world;
        this.pos =block;
        this.player = player;
        this.benchInventory = benchInventory;
        benchInventory.openInventory(player);
        //if(recipes.size() <= 0) {
        //    ModCraftingManager.loadRecipes(this.player, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
        //}


        //output
        this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 124, 35));

        //3x3 grid
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 3; ++j)
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));

        //inventory 9x3
        for (int k = 0; k < 3; ++k)
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlotToContainer(new Slot(player.inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }

        //hotbar 9 slots
        for (int l = 0; l < 9; ++l)
            this.addSlotToContainer(new Slot(player.inventory, l, 8 + l * 18, 142));
    }

    public TileEntityScrambleBench getTileEntity(World world)
    {
        try {
            TileEntity tileentity = world.getTileEntity(pos);
            return (TileEntityScrambleBench) tileentity;
        }
        catch (Exception e) {
            throw new TypeNotPresentException("Could not find TileEntityScrambleBench at " + pos.toString(), e);
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

        try {
            TileEntityScrambleBench tile = getTileEntity(playerIn.world);

            BlockPos dropPos = pos;
            dropPos.up();
            if (tile != null)
                InventoryHelper.dropInventoryItems(playerIn.world, dropPos, tile);
        }
        catch (Exception e)
        {

        }
        benchInventory.closeInventory(playerIn);
        super.onContainerClosed(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        //if (this.world.getBlockState(this.pos).getBlock() != Blocks.CRAFTING_TABLE) {
        //    return false;
        //} else {
            return playerIn.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        //}
    }

    @Override
    protected void slotChangedCraftingGrid(World world, EntityPlayer entityPlayer, InventoryCrafting craftingGrid, InventoryCraftResult craftResult)
    {
        if (!world.isRemote)
        {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entityPlayer;
            ModRecipe  gridRecipe = ModCraftingManager.findMatchingRecipe(entityPlayer.getUniqueID(), ModBlocks.SCRAMBLE_BENCH.getRegistryName(), craftMatrix);
            if(gridRecipe.checkResult().size() == 1)//Crafting result of a single stack
            {
                //ShapedRecipes shapedRecipe = new ShapedRecipes("scramble", craftingGrid.getWidth(), craftingGrid.getHeight(), gridRecipe.getIngredients(), gridRecipe.getRecipeOutput());

                try {
                    if (!world.getGameRules().getBoolean("doLimitedCrafting")
                            || entityplayermp.getRecipeBook().isUnlocked(gridRecipe))
                    {
                        //set ingredients used
                        craftResult.setRecipeUsed(gridRecipe);

                        //ModRecipeRegistry.checkResult(ModRecipes.getIngredientAsItemStacks(shapedRecipe.getIngredients()),entityPlayer.getUniqueID(),ModBlocks.SCRAMBLE_BENCH.getRegistryName());
                        ItemStack returnItem = gridRecipe.craftItem(player,craftingGrid,this).get(0);

                        ///TODO see if dupe is still a problem
                        //if (returnItem.getCount() < 1)
                        //    returnItem.setCount(1);
                        //else if (returnItem.getCount() >= ModRecipeRegistry.previousStackSize * 2)//Temporary fix for output craft doubling
                        //    returnItem.setCount(ModRecipeRegistry.previousStackSize);//will still dupe if output is less than double
                        //ModRecipeRegistry.previousStackSize = returnItem.getCount();
                        craftedLast =true;

                        //server side
                        craftResult.setInventorySlotContents(0, returnItem);
                        //visual inside crafting table
                        entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, returnItem));

                    }

                }catch (Exception e)
                {
                    logger.info(e.getLocalizedMessage());
                }
            }
            else
            {
                //Invalid Recipe
                logger.info("Crafted = "+craftedLast);
                craftedLast =false;

                craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                //visual inside crafting table
                entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0,  ItemStack.EMPTY));


            }

            //Xupdate TileEntityX Only dupes Items after craft
            //try {
            //    TileEntityScrambleBench tileEntity = getTileEntity(world);
            //    for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
            //        tileEntity.setInventorySlotContents(i, craftingGrid.getStackInSlot(i));
            //        tileEntity.setInventorySlotContents(10, craftResult.getStackInSlot(0));
            //    }
            //}catch (Exception e){}
        }
    }


    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.craftMatrix.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.craftMatrix.getSizeInventory(), this.inventorySlots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(itemstack1, 0, this.craftMatrix.getSizeInventory(), false))
                return ItemStack.EMPTY;

            if (itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }

        return itemstack;
    }
}
