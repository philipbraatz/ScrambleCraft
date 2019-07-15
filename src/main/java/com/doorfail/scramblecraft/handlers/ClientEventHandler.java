package com.doorfail.scramblecraft.handlers;

import com.doorfail.scramblecraft.block.scramble_bench.GUIScrambleBench;
import com.doorfail.scramblecraft.block.scramble_bench.RecipesGUIScrambleBench;
import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleBookClient;
import com.doorfail.scramblecraft.recipe.recipe_book.gui.GUIScrambleBook;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleList;
import com.doorfail.scramblecraft.util.Reference;
import com.doorfail.scramblecraft.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;


@EventBusSubscriber(value = {Side.CLIENT}, modid = Reference.MODID)
public class ClientEventHandler
{
    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {
        try {
            //CommonProxy.itemScroll.initModel();
        } catch (Throwable throwable) {}

        try {
            //CommonProxy.spinner.initModel();
        } catch (Throwable throwable) {}

        try {
            //CommonProxy.vending.initModel();
        } catch (Throwable throwable) {}

        try {
            //CommonProxy.slotmachine.initModel();
        } catch (Throwable throwable) {}
    }

    static int posx = 0;
    static int posy = 0;

    static Map<CreativeTabs, List<ScrambleList>> cache = new HashMap();

    public static boolean hidden = true;
    static GUIScrambleBook last = null;

    @SubscribeEvent
    public static void entityUpdate(RenderGameOverlayEvent.Pre evt) {
        //if (evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
        //    if ((Minecraft.getMinecraft()).currentScreen instanceof GUIScrambleBench) {
//
//
        //        GUIScrambleBench guiFurnace = (GUIScrambleBench)(Minecraft.getMinecraft()).currentScreen;
        //        List buttonList = (List)ReflectionUtils.getPrivateFieldValue(net.minecraft.client.gui.GuiScreen.class, guiFurnace, "buttonList");
        //        if (buttonList.isEmpty()) {
        //            buttonList.add(new RecipesGUIScrambleBench(0, 0, 0, guiFurnace, buttonList));
        //            ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/recipe_book/container/crafting_table.png");
//
        //            GuiButtonImage recipeButton = new GuiButtonImage(1, 0, 0, 20, 18, 0, 131, 19, CRAFTING_TABLE_GUI_TEXTURES)
        //            {
        //                public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        //                    if (super.mousePressed(mc, mouseX, mouseY)) {
        //                        ClientEventHandler.hidden = !ClientEventHandler.hidden;
        //                        return true;
        //                    }
//
        //                    return false;
        //                }
        //            };
//
        //            buttonList.add(recipeButton);
        //        }
//
//
//
        //          GUIScrambleBook recipeBookGui = (GUIScrambleBook) ReflectionUtils.getPrivateFieldValue(
        //                  net.minecraft.client.gui.inventory.GuiCrafting.class,
        //                  (Minecraft.getMinecraft()).currentScreen, "recipeBookGui");
//
//
        //        if (recipeBookGui != null) {
        //              if (last != recipeBookGui) {
        //                  List<net.minecraft.client.gui.recipebook.GuiButtonRecipeTab> recipeTabs = (List)ReflectionUtils.getPrivateFieldValue(net.minecraft.client.gui.recipebook.GuiRecipeBook.class, recipeBookGui, "recipeTabs");
        //                  List<CreativeTabs> tabs = Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY);
        //                  List<CreativeTabs> missing = new ArrayList<>(tabs);
        //                  posx = 0;
        //                  posy = 1000;
        //                  for (net.minecraft.client.gui.recipebook.GuiButtonRecipeTab gbrt : recipeTabs) {
        //                      posx = Math.max(posx, gbrt.x);
        //                      if (gbrt.y > 0) {
        //                          posy = Math.min(posy, gbrt.y);
        //                      }
        //                      CreativeTabs category = (CreativeTabs)ReflectionUtils.getPrivateFieldValue(net.minecraft.client.gui.recipebook.GuiButtonRecipeTab.class, gbrt, "category");
//
        //                     List<ScrambleList> l = (List) ScrambleBookClient.RECIPES_BY_TAB.get(category);
        //                      if (l != null) {
        //                          for (ScrambleList rl : l) {
        //                              rl.updateKnownRecipes(Minecraft.getMinecraft().player.getRecipeBook());
        //                          }
        //                      }
//
        //                     missing.remove(category);
        //                  }
//
//
        //                for (CreativeTabs ct : missing) {
        //                      if (ct == CreativeTabs.INVENTORY || ct == CreativeTabs.BREWING || ct == CreativeTabs.HOTBAR) {
        //                          continue;
        //                      }
        //                      if (ct == CreativeTabs.COMBAT || ct == CreativeTabs.TOOLS || ct == CreativeTabs.FOOD) {
        //                          continue;
        //                      }
        //                      if (ct == CreativeTabs.DECORATIONS || ct == CreativeTabs.TRANSPORTATION || ct == CreativeTabs.MISC) {
        //                          continue;
        //                      }
        //                      List<ScrambleList> list = new ArrayList<>();
        //                      if (cache.containsKey(ct)) {
        //                          list.addAll((Collection)cache.get(ct));
        //                      } else {
//
        //                         for (ResourceLocation rl : Item.REGISTRY.getKeys()) {
        //                              Item item = (Item)Item.REGISTRY.getObject(rl);
        //                              ScrambleList recList = new ScrambleList();
        //                              if (item.getCreativeTab() == ct) {
        //                                  for (ModRecipe r : ModRecipeRegistry.getModRecipeList(Minecraft.getMinecraft().player.getUniqueID(),ModBlocks.SCRAMBLE_BENCH.getRegistryName())) {
        //                                      if (r.getRecipeOutput() != null && r.getRecipeOutput().getItem() == item) {
        //                                          recList.add(r);
        //                                      }
        //                                  }
        //                              }
        //                              recList.updateKnownRecipes(Minecraft.getMinecraft().player.getRecipeBook());
        //                              list.add(recList);
        //                          }
        //                          cache.put(ct, list);
        //                      }
//
        //                     net.minecraft.client.gui.recipebook.GuiButtonRecipeTab newTab = new net.minecraft.client.gui.recipebook.GuiButtonRecipeTab(recipeTabs.size(), ct)
        //                      {
        //                          boolean isHovered;
//
//
        //                        public boolean updateVisibility() { return this.visible = true; }
//
//
//
//
        //                      public boolean isMouseOver() { return this.isHovered; }
//
//
//
        //                       public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        //                              this.isHovered = false;
        //                              if (this.x > mouseX && this.x + this.width < mouseX &&
        //                                      this.y > mouseY && this.y + this.height < mouseY) {
        //                                  this.isHovered = true;
        //                              }
//
        //                             super.drawButtonForegroundLayer(mouseX, mouseY);
        //                          }
//
//
        //                        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        //                              int pos = this.id - 5;
        //                              this.x = ClientEventHandler.posx - 30 * ((int)Math.floor((pos / 6)) + 1);
        //                              this.y = ClientEventHandler.posy + pos % 6 * 27;
        //                              this.enabled = true;
        //                              this.isHovered = false;
        //                              if (this.x > mouseX && this.x + this.width < mouseX &&
        //                                      this.y > mouseY && this.y + this.height < mouseY) {
        //                                  this.isHovered = true;
        //                              }
//
        //                             super.drawButton(mc, mouseX, mouseY, partialTicks);
        //                          }
        //                      };
//
        //                     ScrambleBookClient.RECIPES_BY_TAB.put(ct, list);
        //                      recipeTabs.add(newTab);
        //                  }
        //              }
//
//
        //            last = recipeBookGui;
        //          }
            }
//
            //if ((Minecraft.getMinecraft()).currentScreen instanceof GuiFurnace) {
            //    GuiFurnace guiFurnace = (GuiFurnace)(Minecraft.getMinecraft()).currentScreen;
            //    List buttonList = (List)ReflectionUtils.getPrivateFieldValue(net.minecraft.client.gui.GuiScreen.class, guiFurnace, "buttonList");
            //    if (buttonList.isEmpty()) {
            //        buttonList.add(new RecipesGUIScrambleBench(0, 0, 0, guiFurnace, buttonList));
            //        ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/recipe_book/container/crafting_table.png");
//
            //        GuiButtonImage recipeButton = new GuiButtonImage(1, 0, 0, 20, 18, 0, 131, 19, CRAFTING_TABLE_GUI_TEXTURES)
            //        {
            //            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            //                if (super.mousePressed(mc, mouseX, mouseY)) {
            //                    ClientEventHandler.hidden = !ClientEventHandler.hidden;
            //                    return true;
            //                }
//
            //                return false;
            //            }
            //        };
//
            //        buttonList.add(recipeButton);
            //    }
            //}
//        }
//    }
}