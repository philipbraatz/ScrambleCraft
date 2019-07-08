package com.doorfail.scramblecraft.handlers;

import com.doorfail.scramblecraft.util.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CraftingEventHandler {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    boolean testDebug =false;
    boolean firstCall =true;//fix double call

    @SubscribeEvent
    public void itemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if(firstCall) {
            firstCall = false;

            if (event.craftMatrix.getSizeInventory() == 4) {
               // event.craftMatrix.clear();
                //event.crafting.setCount(0);

                //server side
               // event.craftMatrix.setInventorySlotContents(0, ItemStack.EMPTY);
                //event.craftMatrix.setInventorySlotContents(1, ItemStack.EMPTY);
                //event.craftMatrix.setInventorySlotContents(2, ItemStack.EMPTY);
                //event.craftMatrix.setInventorySlotContents(3, ItemStack.EMPTY);
               // event.setResult(Event.Result.DENY);
                event.setCanceled(true);
                //event.
                //visual inside crafting table
                //entityplayermp.connection.sendPacket(new SPacketSetSlot(this.windowId, 0, returnItem));
            } else {

                //event.crafting = new ItemStack( Items.CARROT);
                logger.info("Crafting - " + event.crafting);
                //List<Item> ingredients = new ArrayList<>();
                //for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
                //    ingredients.add(event.craftMatrix.getStackInSlot(i).getItem());
                //}
                //List<ItemStack> ingredientClean = new ArrayList<>();
                //for (Item itemStack : ingredients) {
                //    if (itemStack != Items.AIR)
                //        ingredientClean.add(new ItemStack(itemStack));
                //}

                //if(!ModRecipes.ingredientInputs.contains(ingredients))
                //    ModRecipes.addRecipe(ingredients,new ItemStack(event.crafting.getItem()));


                //Get the crafting block being looked at
                //RayTraceResult mop =  Minecraft.getMinecraft().objectMouseOver;
                //if(mop != null) {
                //    //EnumFacing blockHitSide = mop.sideHit;
                //    Block blockLookingAt = event.player.world.getBlockState(mop.getBlockPos()).getBlock();
//
                //    ModRecipeRegistry.randomizeRecipe(event.craftMatrix, event.player, blockLookingAt.getRegistryName(), ingredientClean);
                //}
                //logger.info("Randomized Recipe");
            }
        }
        else
            firstCall = true;

    }

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
        //logger.info("Crafting Registered Success");
    }

}
