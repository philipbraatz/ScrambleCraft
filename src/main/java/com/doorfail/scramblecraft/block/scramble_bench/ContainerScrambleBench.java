package com.doorfail.scramblecraft.block.scramble_bench;


import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModCraftingManager;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.doorfail.scramblecraft.util.Reference.MODID;

public class ContainerScrambleBench extends Container {
    private static Logger logger = LogManager.getLogger(MODID);

    /**
     * The crafting matrix inventory (3x3).
     */
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public InventoryCraftResult craftResult = new InventoryCraftResult();
    public int test;
    //slot numbers
    //0: result
    //1-9:matrix

    private final World world;
    /**
     * Position of the workbench
     */
    private final BlockPos pos;
    private final EntityPlayer player;
    private TileEntityScrambleBench benchInventory;
    private boolean craftedLast = false;

    //Prevent Duping on large scale
    private int matrixItemCount = 0;
    private int matrixItemCountPrevious2 = 0;
    private int matrixItemCountPrevious1 = 0;

    private static Map<UUID, EntityPlayerMP> playerMap = new HashMap<>();

    public ContainerScrambleBench(World world, BlockPos block, TileEntityScrambleBench benchInventory, EntityPlayer player) {
        this.world = world;
        this.pos = block;
        this.player = player;
        this.benchInventory = benchInventory;
        benchInventory.openInventory(player);
        //if(recipes.size() <= 0) {
        //    ModCraftingManager.loadRecipes(this.player, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
        //}

        if (!player.world.isRemote && !playerMap.containsKey(player.getUniqueID()))
            playerMap.put(player.getUniqueID(), (EntityPlayerMP) player);

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

    public TileEntityScrambleBench getTileEntity(World world) {
        try {
            TileEntity tileentity = world.getTileEntity(pos);
            return (TileEntityScrambleBench) tileentity;
        } catch (Exception e) {
            throw new TypeNotPresentException("Could not find TileEntityScrambleBench at " + pos.toString(), e);
        }

    }

    /**
     * Callback for when the crafting matrix is changed.
     */
    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        this.slotChangedCraftingGrid(player.world, this.player, this.craftMatrix, this.craftResult);
    }

    /**
     * Called when the container is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if (!playerIn.world.isRemote) {
            if (playerMap.containsKey(player.getUniqueID()))
                playerMap.remove(player.getUniqueID());
            //this.clearContainer(playerIn, playerIn.world, this.craftMatrix);
        }

        try {
            BlockPos dropPos = pos;
            dropPos.offset(playerIn.getHorizontalFacing(), 1);
            InventoryHelper.dropInventoryItems(playerIn.world, dropPos, benchInventory);
        } catch (Exception e) {

        }
        benchInventory.closeInventory(playerIn);
        super.onContainerClosed(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        //if (this.world.getBlockState(this.pos).getBlock() != Blocks.CRAFTING_TABLE) {
        //    return false;
        //} else {
        return playerIn.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        //}
    }

    @Override
    protected void slotChangedCraftingGrid(World world, EntityPlayer entityPlayer, InventoryCrafting craftingGrid, InventoryCraftResult IncraftResult) {
        if (!world.isRemote) {
            matrixItemCountPrevious2 =matrixItemCountPrevious1;
            matrixItemCountPrevious1 =matrixItemCount;
            matrixItemCount=0;
            for (int i = 0; i < craftingGrid.getSizeInventory(); i++) {
                benchInventory.setInventorySlotContents(i, craftingGrid.getStackInSlot(i));
                matrixItemCount += craftingGrid.getStackInSlot(i).getCount();
            }

            EntityPlayerMP entityplayermp = (EntityPlayerMP) entityPlayer;
            if (!playerMap.containsKey(entityplayermp.getUniqueID()))
                playerMap.put(entityplayermp.getUniqueID(), entityplayermp);

            ModRecipe gridRecipe = ModCraftingManager.findMatchingRecipe(entityPlayer.getUniqueID(), ModBlocks.SCRAMBLE_BENCH.getRegistryName(), craftMatrix);
            if (gridRecipe.checkResult().size() == 1)//Crafting result of a single stack
            {
                //ShapedRecipes shapedRecipe = new ShapedRecipes("scramble", craftingGrid.getWidth(), craftingGrid.getHeight(), gridRecipe.getIngredients(), gridRecipe.getRecipeOutput());

                try {
                    if (!world.getGameRules().getBoolean("doLimitedCrafting")
                            || entityplayermp.getRecipeBook().isUnlocked(gridRecipe)) {
                        //set ingredients used
                        IncraftResult.setRecipeUsed(gridRecipe);

                        //ModRecipeRegistry.checkResult(ModRecipes.getIngredientAsItemStacks(shapedRecipe.getIngredients()),entityPlayer.getUniqueID(),ModBlocks.SCRAMBLE_BENCH.getRegistryName());
                        //if(craftResult.getStackInSlot(0).getItem()!=Items.AIR )
                        //{
                        //    ModRecipe prevRecipe = (ModRecipe) ModCraftingManager.getRecipe(IncraftResult.getStackInSlot(0).getItem().getRegistryName());
                        //    prevRecipe.unCraftItem();//prevents scrambling without crafting
                        //}
                        ItemStack returnItem;
                        if(matrixItemCountPrevious2 != matrixItemCount &&
                                ( matrixItemCountPrevious2 !=0 || matrixItemCountPrevious1 ==0 )&&
                                matrixItemCountPrevious1 != matrixItemCount
                        )
                            returnItem = gridRecipe.craftItem(entityPlayer,craftingGrid,this).get(0);
                        else
                            returnItem =gridRecipe.checkResult().get(0);

                        returnItem.setCount(1);//force EVERY item to return previous size

                        ModRecipeRegistry.previousStackSize = returnItem;
                        craftedLast = true;


                        //server side
                        IncraftResult.setInventorySlotContents(0, returnItem);
                        //visual inside crafting table
                        decrementMatrix(entityPlayer,0);//Refreshes inventory, stops duping
                        entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, returnItem));//output
                    }

                } catch (Exception e) {
                    logger.warn("Error: ");
                    e.printStackTrace();
                }
            } else {
                //Invalid Recipe
                //logger.info("Crafted = "+craftedLast);
                craftedLast = false;

                IncraftResult.setInventorySlotContents(0, ItemStack.EMPTY);
                //visual inside crafting table
                entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, ItemStack.EMPTY));


            }
        }
    }


    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        slotChangedCraftingGrid(world, playerIn, craftMatrix, craftResult);
        if (!player.world.isRemote && !playerMap.containsKey(player.getUniqueID()))
            playerMap.put(player.getUniqueID(), (EntityPlayerMP) player);

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < this.craftMatrix.getSizeInventory()) {
                if (!this.mergeItemStack(itemstack1, this.craftMatrix.getSizeInventory(), this.inventorySlots.size(), true)) {
                    if (index == 0)
                        decrementMatrix(playerIn,1);
                    slotChangedCraftingGrid(world, playerIn, craftMatrix, craftResult);

                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, this.craftMatrix.getSizeInventory(), false)) {
                if (index == 0)
                    decrementMatrix(playerIn,1);
                slotChangedCraftingGrid(world, playerIn, craftMatrix, craftResult);
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
                slot.putStack(ItemStack.EMPTY);
            else
                slot.onSlotChanged();
        }
        if (index == 0)
            decrementMatrix(playerIn,1);
        slotChangedCraftingGrid(world, playerIn, craftMatrix, craftResult);
        return itemstack;
    }

    public static EntityPlayerMP getEntityPlayerMP(UUID playerId) {
        try {
            if (playerMap.containsKey(playerId))
                return playerMap.get(playerId);
            else
                throw new Exception("Player with ID:" + playerId.toString() + " does not exist");
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.benchInventory);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        //craftedLast=true;
//
        //for(int i = 0; i < this.inventorySlots.size(); ++i) {
        //    ItemStack clientStack = (this.inventorySlots.get(i)).getStack();
        //    ItemStack serverStack = this.inventoryItemStacks.get(i);
        //    if (!ItemStack.areItemStacksEqual(serverStack, clientStack)) {
        //        boolean clientStackChanged = !ItemStack.areItemStacksEqualUsingNBTShareTag(serverStack, clientStack);
        //        serverStack = clientStack.isEmpty() ? ItemStack.EMPTY : clientStack.copy();
        //        this.inventoryItemStacks.set(i, serverStack);
        //        if (clientStackChanged) {
        //            if( i>9 || craftResult.isEmpty() || serverStack.getCount()+1 != clientStack.getCount())
        //            {
        //                craftedLast =false;
        //            }else
        //                logger.info("Item Was crafted!");
        //            for(int j = 0; j < this.listeners.size(); ++j) {
        //                (this.listeners.get(j)).sendSlotContents(this, i, serverStack);
        //            }
        //        }
        //    }
        //}
    }
    public void onCraftSlotUsed()
    {

    }

    public void decrementMatrix(EntityPlayer playerIn, int amount) {
        if (!world.isRemote) {
            for (int i = 1; i < craftMatrix.getSizeInventory() + 1; i++) {
                ItemStack slot = craftMatrix.getStackInSlot(i);
                if (slot.getCount() > 64) {
                    slot.setCount(64);
                    logger.warn("Overflow Itemstack");
                }
                if (slot.getCount() > 0)
                    slot.setCount(slot.getCount() - amount);
                ((EntityPlayerMP) playerIn).connection.sendPacket(new SPacketSetSlot(this.windowId, i + 1, slot));//half each slot
            }
        }
    }
}
