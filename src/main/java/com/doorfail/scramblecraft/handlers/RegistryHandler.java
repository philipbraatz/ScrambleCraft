package com.doorfail.scramblecraft.handlers;

import com.doorfail.scramblecraft.ScrambleCraft;
import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.init.ModItems;
import com.doorfail.scramblecraft.init.ModOreDictionary;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@EventBusSubscriber
public class RegistryHandler
{
    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
        TileEntityHandler.registerTileEntities();
    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event)
    {

        for(Item item : ModItems.ITEMS)
            ScrambleCraft.proxy.registerItemRenderer(item, 0, "inventory");

        for(Block block : ModBlocks.BLOCKS)
        {
            ScrambleCraft.proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, "inventory");
        }
    }

    public static void preInitRegistries()
    {
        //handlers
    }

    public static void initRegistries()
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(ScrambleCraft.instance, new GUIHandler());
        ModRecipeRegistry.init();
        ModOreDictionary.registerOres();
    }

    public static void postInitRegistries()
    {

    }

    public static void serverRegistries(FMLServerStartingEvent event)
    {

    }
}