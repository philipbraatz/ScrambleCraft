package com.doorfail.scramblecraft;

import com.doorfail.scramblecraft.handlers.CraftingEventHandler;
import com.doorfail.scramblecraft.init.ModRecipes;
import com.doorfail.scramblecraft.proxy.CommonProxy;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.doorfail.scramblecraft.util.Reference;
import com.google.common.collect.Maps;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.doorfail.scramblecraft.util.Reference.MODID;


@Mod(modid = MODID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MCVERSION)
public class ScrambleCraft
{

    @Mod.Instance
    public static ScrambleCraft instance;

    public static CraftingEventHandler craftEvent = new CraftingEventHandler();

    private static Logger logger = LogManager.getLogger(MODID);

    @SidedProxy(clientSide = Reference.CLIENT_PROXY, serverSide = Reference.COMMON_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //System.out.println("system Print printing");
        //logger.info("Pre init");
        FMLInterModComms.sendMessage("waila","register","com.doorfail.scramblecraft.plugin.waila.ScrambleCraftWailaPlugin.onWailaCall");
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //logger.info("Init");

        Configuration config = new Configuration(new File("config/TutorialMod.cfg"));
        config.load();
        int option1  = config.get("test","enableThisConfig",34).getInt();
        //logger.info(option1);


        proxy.init(event);
        //ModRecipes.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        craftEvent.init();

        //logger.info("postInit");
        proxy.postInit(event);
    }

    @Config.LangKey("config_test.config.annotations")
    @Config(modid = MODID)
    public static class CONFIG_ANNOTATIONS
    {
        @Config.RangeDouble(min = -10.5, max = 100.5)
        public static double DoubleRange = 10.0;

        @Config.RangeInt(min = -10, max = 100)
        public static double IntRange = 10;

        @Config.LangKey("this.is.not.a.good.key")
        @Config.Comment({"This is a really long", "Multi-line comment"})
        public static String Comments = "Hi Tv!";

        @Config.Comment("Category Comment Test")
        public static NestedType Inner = new NestedType();

        public static class NestedType
        {
            public String HeyLook = "Go in!";
        }
    }

    @Config.LangKey("config_test.config.subcats")
    @Config(modid = MODID, name = MODID + "_subcats", category = "")
    public static class CONFIG_SUBCATS
    {
        //public static String THIS_WILL_ERROR = "DUH";

        @Config.Name("test_a")
        public static SubCat sub1 = new SubCat("Hello");
        @Config.Name("test_b")
        public static SubCat sub2 = new SubCat("Goodbye");
        @Config.Name("test_c")
        public static SubCat2 sub3 = new SubCat2("Hi");

        public static class SubCat
        {
            @Config.Name("i_say")
            public String value;

            public SubCat(String value)
            {
                this.value = value;
            }
        }

        public static class SubCat2
        {
            @Config.Name("child_cat")
            public SubCat child;

            public SubCat2(String value)
            {
                this.child = new SubCat(value);
            }
        }
    }

    @Config.LangKey("config_test.config.maps")
    @Config(modid = MODID, name = MODID + "_map")
    public static class CONFIG_MAP
    {
        @Config.Name("map")
        @Config.Comment("This comment belongs to the \"map\" category, not the \"general\" category")
        @Config.RequiresMcRestart
        public static Map<String, Integer[]> theMap;

        @Config.Name("regex(test]")
        public static Map<String, String> regexText = new HashMap<>();

        static
        {
            theMap = Maps.newHashMap();
            for (int i = 0; i < 7; i++)
            {
                Integer[] array = new Integer[6];
                for (int x = 0; x < array.length; x++)
                {
                    array[x] = i + x;
                }
                theMap.put("" + i, array);
                regexText.put("" + i, "" + i);
            }
        }
    }

}
