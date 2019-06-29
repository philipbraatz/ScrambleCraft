package com.doorfail.scramblecraft.handlers;

import com.doorfail.scramblecraft.block.scramble_bench.GUIScrambleBench;
import com.doorfail.scramblecraft.block.scramble_bench.RecipesGUIScrambleBench;
import com.doorfail.scramblecraft.util.Reference;
import com.doorfail.scramblecraft.util.ReflectionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.recipebook.GuiButtonRecipeTab;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.util.RecipeBookClient;
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

    static Map<CreativeTabs, List<RecipeList>> cache = new HashMap();

    public static boolean hidden = true;
    static GuiRecipeBook last = null;

    @SubscribeEvent
    public static void entityUpdate(RenderGameOverlayEvent.Pre evt) {
        if (evt.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if ((Minecraft.getMinecraft()).currentScreen instanceof net.minecraft.client.gui.inventory.GuiCrafting) {

                GuiRecipeBook recipeBookGui = (GuiRecipeBook) ReflectionUtils.getPrivateFieldValue(
                        net.minecraft.client.gui.inventory.GuiCrafting.class,
                        (Minecraft.getMinecraft()).currentScreen, "recipeBookGui");

                if (recipeBookGui != null) {
                    if (last != recipeBookGui) {
                        List<GuiButtonRecipeTab> recipeTabs = (List)ReflectionUtils.getPrivateFieldValue(GuiRecipeBook.class, recipeBookGui, "recipeTabs");
                        List<CreativeTabs> tabs = Arrays.asList(CreativeTabs.CREATIVE_TAB_ARRAY);
                        List<CreativeTabs> missing = new ArrayList<CreativeTabs>(tabs);
                        posx = 0;
                        posy = 1000;
                        for (GuiButtonRecipeTab gbrt : recipeTabs) {
                            posx = Math.max(posx, gbrt.x);
                            if (gbrt.y > 0) {
                                posy = Math.min(posy, gbrt.y);
                            }
                            CreativeTabs category = (CreativeTabs)ReflectionUtils.getPrivateFieldValue(GuiButtonRecipeTab.class, gbrt, "category");

                            List<RecipeList> l = (List)RecipeBookClient.RECIPES_BY_TAB.get(category);
                            if (l != null) {
                                for (RecipeList rl : l) {
                                    rl.updateKnownRecipes((Minecraft.getMinecraft()).player.getRecipeBook());
                                }
                            }

                            missing.remove(category);
                        }


                        for (CreativeTabs ct : missing) {
                            if (ct == CreativeTabs.INVENTORY || ct == CreativeTabs.BREWING || ct == CreativeTabs.HOTBAR) {
                                continue;
                            }
                            if (ct == CreativeTabs.COMBAT || ct == CreativeTabs.TOOLS || ct == CreativeTabs.FOOD) {
                                continue;
                            }
                            if (ct == CreativeTabs.DECORATIONS || ct == CreativeTabs.TRANSPORTATION || ct == CreativeTabs.MISC) {
                                continue;
                            }
                            List<RecipeList> list = new ArrayList<RecipeList>();
                            if (cache.containsKey(ct)) {
                                list.addAll((Collection)cache.get(ct));
                            } else {

                                for (ResourceLocation rl : Item.REGISTRY.getKeys()) {
                                    Item item = (Item)Item.REGISTRY.getObject(rl);
                                    RecipeList recList = new RecipeList();
                                    if (item.getCreativeTab() == ct) {
                                        for (IRecipe r : ForgeRegistries.RECIPES) {
                                            if (r.getRecipeOutput() != null && r.getRecipeOutput().getItem() == item) {
                                                recList.add(r);
                                            }
                                        }
                                    }
                                    recList.updateKnownRecipes((Minecraft.getMinecraft()).player.getRecipeBook());
                                    list.add(recList);
                                }
                                cache.put(ct, list);
                            }

                            GuiButtonRecipeTab newTab = new GuiButtonRecipeTab(recipeTabs.size(), ct)
                            {
                                boolean isHovered;


                                public boolean updateVisibility() { return this.visible = true; }




                                public boolean isMouseOver() { return this.isHovered; }



                                public void drawButtonForegroundLayer(int mouseX, int mouseY) {
                                    this.isHovered = false;
                                    if (this.x > mouseX && this.x + this.width < mouseX &&
                                            this.y > mouseY && this.y + this.height < mouseY) {
                                        this.isHovered = true;
                                    }

                                    super.drawButtonForegroundLayer(mouseX, mouseY);
                                }


                                public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                                    int pos = this.id - 5;
                                    this.x = ClientEventHandler.posx - 30 * ((int)Math.floor((pos / 6)) + 1);
                                    this.y = ClientEventHandler.posy + pos % 6 * 27;
                                    this.enabled = true;
                                    this.isHovered = false;
                                    if (this.x > mouseX && this.x + this.width < mouseX &&
                                            this.y > mouseY && this.y + this.height < mouseY) {
                                        this.isHovered = true;
                                    }

                                    super.drawButton(mc, mouseX, mouseY, partialTicks);
                                }
                            };

                            RecipeBookClient.RECIPES_BY_TAB.put(ct, list);
                            recipeTabs.add(newTab);
                        }
                    }


                    last = recipeBookGui;
                }
            }

            if ((Minecraft.getMinecraft()).currentScreen instanceof GuiFurnace) {
                GuiFurnace guiFurnace = (GuiFurnace)(Minecraft.getMinecraft()).currentScreen;
                List buttonList = (List)ReflectionUtils.getPrivateFieldValue(net.minecraft.client.gui.GuiScreen.class, guiFurnace, "buttonList");
                if (buttonList.isEmpty()) {
                    buttonList.add(new RecipesGUIScrambleBench(0, 0, 0, guiFurnace, buttonList));
                    ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

                    GuiButtonImage recipeButton = new GuiButtonImage(1, 0, 0, 20, 18, 0, 131, 19, CRAFTING_TABLE_GUI_TEXTURES)
                    {
                        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                            if (super.mousePressed(mc, mouseX, mouseY)) {
                                ClientEventHandler.hidden = !ClientEventHandler.hidden;
                                return true;
                            }

                            return false;
                        }
                    };

                    buttonList.add(recipeButton);
                }
            }
        }
    }
}