package com.doorfail.scramblecraft.util;


import com.doorfail.scramblecraft.block.scramble_bench.ContainerScrambleBench;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraftforge.common.crafting.IRecipeContainer;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.doorfail.scramblecraft.util.Reference.MODID;


//moves items to and from the Scramble Table
public class ServerScrambleBookHelper {
        private static Logger logger = LogManager.getLogger(MODID);
        private final RecipeItemHelper recipeHelper = new RecipeItemHelper();
        private EntityPlayerMP playerMP;
        private IRecipe iRecipe;
        private boolean forceStackSize;
        private InventoryCraftResult craftResult;
        private InventoryCrafting matrix;
        private List<Slot> slotList;

        public ServerScrambleBookHelper() {
        }

        //Called on recipe click
    //
        public void placeRecipe(EntityPlayerMP entityPlayerMP, @Nullable IRecipe iRecipe, boolean forceStackSize) {
            if (iRecipe != null )//&& entityPlayerMP.getRecipeBook().isUnlocked(iRecipe))
            {
                this.playerMP = entityPlayerMP;
                this.iRecipe = iRecipe;
                this.forceStackSize = forceStackSize;
                this.slotList = entityPlayerMP.openContainer.inventorySlots;//TODO CRASH - Null Pointer Exception: updating screen events
                Container container = entityPlayerMP.openContainer;
                this.craftResult = null;
                this.matrix = null;

                //switch to ContainerScrambleBench
                if (container instanceof ContainerScrambleBench) {
                    this.craftResult = ((ContainerScrambleBench)container).craftResult;//old Result
                    this.matrix = ((ContainerScrambleBench)container).craftMatrix;//Old Matrix
                } else if (container instanceof ContainerPlayer) {
                    this.craftResult = ((ContainerPlayer)container).craftResult;
                    this.matrix = ((ContainerPlayer)container).craftMatrix;
                } else if (container instanceof IRecipeContainer) {
                    this.craftResult = ((IRecipeContainer)container).getCraftResult();
                    this.matrix = ((IRecipeContainer)container).getCraftMatrix();
                }

                if (this.craftResult != null && this.matrix != null &&
                        (this.canStoreMoreItems() || entityPlayerMP.isCreative()))
                {
                    this.recipeHelper.clear();
                    entityPlayerMP.inventory.fillStackedContents(this.recipeHelper, false);
                    this.matrix.fillStackedContents(this.recipeHelper);
                    if (this.recipeHelper.canCraft(iRecipe, (IntList)null)) {
                        this.moveIngredientsToMatrix();
                    } else {
                        this.moveMatrixToPlayer();
                        entityPlayerMP.connection.sendPacket(new SPacketPlaceGhostRecipe(entityPlayerMP.openContainer.windowId, iRecipe));
                    }

                    entityPlayerMP.inventory.markDirty();
                }
            }

        }

        private void moveMatrixToPlayer() {
            InventoryPlayer inventoryplayer = this.playerMP.inventory;

            for(int i = 0; i < this.matrix.getSizeInventory(); ++i) {
                ItemStack stackInSlot = this.matrix.getStackInSlot(i);
                if (!stackInSlot.isEmpty()) while (stackInSlot.getCount() > 0)
                {
                    int matchingSlot = inventoryplayer.storeItemStack(stackInSlot);
                    if (matchingSlot == -1) {
                        matchingSlot = inventoryplayer.getFirstEmptyStack();
                    }

                    ItemStack newStack = stackInSlot.copy();
                    newStack.setCount(1);
                    inventoryplayer.add(matchingSlot, newStack);
                    this.matrix.decrStackSize(i, 1);
                }
            }

            this.matrix.clear();
            this.craftResult.clear();
        }

        private void moveIngredientsToMatrix() {
            boolean matches = this.iRecipe.matches(this.matrix, this.playerMP.world);
            int mostCraftable = this.recipeHelper.getBiggestCraftableStack(this.iRecipe, (IntList)null);

            if (matches) {
                boolean notEmpty = true;

                for(int j = 0; j < this.matrix.getSizeInventory(); ++j) {
                    ItemStack itemstack = this.matrix.getStackInSlot(j);
                    if (!itemstack.isEmpty() && Math.min(mostCraftable, itemstack.getMaxStackSize()) > itemstack.getCount())
                        notEmpty = false;
                }

                if (notEmpty) return;
            }

            int max = this.getStackSize(mostCraftable, matches);
            IntList intitemList = new IntArrayList();
            if (this.recipeHelper.canCraft(this.iRecipe, intitemList, max)) {
                IntListIterator itemIter = intitemList.iterator();

                while(itemIter.hasNext()) {
                    int itemId = itemIter.next();
                    int stackMax = RecipeItemHelper.unpack(itemId).getMaxStackSize();
                    if (stackMax < max) {
                        max = stackMax;
                    }
                }

                if (this.recipeHelper.canCraft(this.iRecipe, intitemList, max)) {
                    this.moveMatrixToPlayer();
                    this.moveItemsToMatrix(max, intitemList);
                }
            }

        }

        private int getStackSize(int forcedStackSize, boolean isMaxed) {
            int stackSize = 1;
            if (this.forceStackSize)
                stackSize = forcedStackSize;
            else if (isMaxed) {
                stackSize = 64;

                for(int j = 0; j < this.matrix.getSizeInventory(); ++j) {
                    ItemStack itemstack = this.matrix.getStackInSlot(j);
                    if (!itemstack.isEmpty() && stackSize > itemstack.getCount())
                        stackSize = itemstack.getCount();
                }

                if (stackSize < 64) {
                    ++stackSize;
                }
            }

            return stackSize;
        }

        private void moveItemsToMatrix(int sizeOfSomething, IntList matrixList) {
            int width = this.matrix.getWidth();
            int height = this.matrix.getHeight();
            if (this.iRecipe instanceof IShapedRecipe) {
                IShapedRecipe shapedrecipes = (IShapedRecipe)this.iRecipe;
                width = shapedrecipes.getRecipeWidth();
                height = shapedrecipes.getRecipeHeight();
            }

            int slotNum = 1;
            Iterator<Integer> matrixIter = matrixList.iterator();

            for(int w = 0; w < this.matrix.getWidth() && height != w; ++w)
            {
                for(int h = 0; h < this.matrix.getHeight(); ++h) {
                    if (width == h || !matrixIter.hasNext())
                    {
                        slotNum += this.matrix.getWidth() - h;
                        break;
                    }

                    Slot slot = this.slotList.get(slotNum);
                    ItemStack inputItem = RecipeItemHelper.unpack(matrixIter.next());
                    if (!inputItem.isEmpty())
                        for(int i1 = 0; i1 < sizeOfSomething; ++i1)
                            this.decrStackSizeBy1(slot, inputItem);

                    ++slotNum;
                }

                if (!matrixIter.hasNext()) {
                    break;
                }
            }

        }

        //t
        private void decrStackSizeBy1(Slot slot, ItemStack itemStack) {
            InventoryPlayer inventory = this.playerMP.inventory;
            int matchingSlot = inventory.findSlotMatchingUnusedItem(itemStack);
            if (matchingSlot != -1)
            {
                ItemStack slotItemStack = inventory.getStackInSlot(matchingSlot).copy();
                if (!slotItemStack.isEmpty()) {
                    if (slotItemStack.getCount() > 1) {
                        inventory.decrStackSize(matchingSlot, 1);
                    } else {
                        inventory.removeStackFromSlot(matchingSlot);
                    }

                    slotItemStack.setCount(1);
                    if (slot.getStack().isEmpty()) {
                        slot.putStack(slotItemStack);
                    } else {
                        slot.getStack().grow(1);
                    }
                }
            }

        }

        private boolean canStoreMoreItems() {
            InventoryPlayer inv = this.playerMP.inventory;

            for(int i = 0; i < this.matrix.getSizeInventory(); ++i) {
                ItemStack slot = this.matrix.getStackInSlot(i);
                if (!slot.isEmpty()) {
                    int matchingSlot = inv.storeItemStack(slot);
                    if (matchingSlot == -1)
                        matchingSlot = inv.getFirstEmptyStack();

                    if (matchingSlot == -1)
                        return false;//no available spaces
                }
            }

            return true;
        }
}
