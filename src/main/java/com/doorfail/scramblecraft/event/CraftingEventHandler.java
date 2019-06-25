package com.doorfail.scramblecraft.event;

import com.doorfail.scramblecraft.init.ModRecipes;
import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CraftingEventHandler {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    boolean testDebug =false;
    boolean firstCall =true;//fix double call

    @SubscribeEvent
    public void itemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if(firstCall) {
            firstCall =false;

            //event.crafting = new ItemStack( Items.CARROT);
            logger.info("Crafting - "+event.crafting);
            List<Item> ingredients = new ArrayList<>();
            for (int i =0; i <event.craftMatrix.getSizeInventory();i++) {
                ingredients.add(event.craftMatrix.getStackInSlot(i).getItem());
            }
            List<Item> ingredientClean = new ArrayList<>();
            for(Item itemStack:ingredients)
            {
                if(itemStack !=Items.AIR)
                    ingredientClean.add(itemStack);
            }

            //if(!ModRecipes.ingredientInputs.contains(ingredients))
            //    ModRecipes.addRecipe(ingredients,new ItemStack(event.crafting.getItem()));

            ModRecipes.randomizeRecipe(event.craftMatrix,event.player,ingredientClean);

            //logger.info("Randomized Recipe");
        }
        else
            firstCall =true;
    }

    public void init()
    {
        MinecraftForge.EVENT_BUS.register(this);
        logger.info("Crafting Registered Success");
    }

}