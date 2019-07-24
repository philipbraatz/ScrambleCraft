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
    public static boolean hasCrafted;

    @SubscribeEvent
    public void itemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if(firstCall) {
            firstCall = false;

            if (event.craftMatrix.getSizeInventory() == 4) {
                hasCrafted =true;
            } else {
                //logger.info("Crafting - " + event.crafting);
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
