package com.doorfail.scramblecraft.proxy;

import com.doorfail.scramblecraft.util.handlers.RegistryHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CommonProxy {

    private static Logger logger = LogManager.getLogger("scramblecraft");
    public void registerItemRenderer(Item item, int meta, String id) {}

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {

    }

    public void preInit(FMLPreInitializationEvent event){
        RegistryHandler.preInitRegistries();
    }
    public void init(FMLInitializationEvent event){RegistryHandler.initRegistries();}
    public void postInit(FMLPostInitializationEvent event){RegistryHandler.postInitRegistries();}
    public void serverStarting(FMLServerStartingEvent event){RegistryHandler.serverRegistries(event);}
    public void serverStopping(FMLServerStoppingEvent event){}
}
