package com.doorfail.scramblecraft.event;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

public class TestEventHandler {
    private static Logger logger;

    @SubscribeEvent
    public void pickupItem(EntityItemPickupEvent event) {
        logger.info("Item picked up!");
    }

    public void init(Logger log)
    {
        logger =log;
        logger.info("Test Event Logger connected");
        MinecraftForge.EVENT_BUS.register(this);
    }
}