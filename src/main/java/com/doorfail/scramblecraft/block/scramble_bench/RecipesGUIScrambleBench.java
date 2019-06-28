package com.doorfail.scramblecraft.block.scramble_bench;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

public class RecipesGUIScrambleBench extends GuiButton {
    RenderItem itemRender;
    GuiFurnace parent;
    List buttonList;
    boolean scroll = false;
    int scrollPosition = 0;
    long lastscrollPosition = 0L;
    protected static final ResourceLocation RECIPE_BOOK = new ResourceLocation("textures/gui/recipe_book.png");
    Map<Rectangle, ItemStack> positions = new HashMap();
    Rectangle selected = null;
    boolean makeSound = false;

    public RecipesGUIScrambleBench(int buttonId, int x, int y, GuiFurnace parent, List buttonList) {
        super(buttonId, x, y, "");
        this.parent = parent;
        this.buttonList = buttonList;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        GuiButton button = (GuiButton) this.buttonList.get(1);
        this.width = this.parent.width;
        this.height = this.parent.height;
        button.width = this.width / 2 - 84;
        button.height = this.height / 2 - 80;
        if (!ClientEventHandler.hidden) {
            this.drawGradientRect(0, 0, 24, this.height, -1072689136, -804253680);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            RenderHelper.enableGUIStandardItemLighting();
            mc.getTextureManager().getTexture(RECIPE_BOOK);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int offsetX = this.width / 2 - 75 - 138;
            int offsetY = this.height / 2 - 83;
            int widthX = 150;
            int heightY = 180;
            GL11.glScaled(0.85D, 1.0D, 1.0D);
            this.drawTexturedModalRect((int) ((double) offsetX / 0.85D), offsetY, 1, 1, widthX, heightY);
            GL11.glScaled(1.1764705882352942D, 1.0D, 1.0D);
            this.renderSmeltingRecipes(mc, mouseX, mouseY, offsetX + 6, offsetY + 24, widthX - 6, heightY - 48);
            GL11.glEnable(2896);
            GL11.glEnable(2929);
            RenderHelper.enableStandardItemLighting();
        }

    }

    public void renderSmeltingRecipes(Minecraft mc, int mouseX, int mouseY, int posX, int posY, int sizeX, int sizeY) {
        if (this.itemRender == null) {
            this.itemRender = mc.getRenderItem();
        }

        Map<Rectangle, ItemStack> temp = new HashMap();
        int position = 0;
        if (mouseX > posX && mouseX < sizeX + posX && mouseY > posY && mouseY < sizeY + posY) {
            int wheel = Mouse.getEventDWheel();
            if (wheel != 0 && this.lastscrollPosition != Mouse.getEventNanoseconds()) {
                this.lastscrollPosition = Mouse.getEventNanoseconds();
                this.scrollPosition += (int) Math.ceil((double) wheel / 15.0D);
                this.selected = null;
            }
        }

        double scaleX = (double) mc.displayWidth / (double) this.width;
        double scaleY = (double) mc.displayHeight / (double) this.height;
        int widthX = (int) Math.round((double) sizeX * scaleX);
        int widthY = (int) Math.round((double) sizeY * scaleY);
        GL11.glScissor((int) Math.round((double) posX * scaleX), (int) Math.round((double) (this.height - posY) * scaleY) - widthY, widthX, widthY);
        GL11.glPushMatrix();
        GL11.glTranslated((double) posX, (double) posY, 0.0D);
        int numberX = (int) Math.floor((double) (sizeX - 2) / 48.0D);
        GL11.glEnable(3089);
        Set<ItemStack> unlocked = (Set) EventHandler.unlockedFurnaceRecipes.get(Minecraft.getMinecraft().player.getUniqueID().toString());
        Iterator var18;
        Entry entry;
        if (unlocked != null && !unlocked.isEmpty()) {
            var18 = FurnaceRecipes.instance().getSmeltingList().entrySet().iterator();

            while (var18.hasNext()) {
                entry = (Entry) var18.next();
                ItemStack is = ((ItemStack) entry.getKey()).copy();
                is.setItemDamage(is.getMetadata() % 32767);
                boolean found = false;
                Iterator var22 = ((Set) EventHandler.unlockedFurnaceRecipes.get(Minecraft.getMinecraft().player.getUniqueID().toString())).iterator();

                while (var22.hasNext()) {
                    ItemStack is2 = (ItemStack) var22.next();
                    if (is2.getItem() == is.getItem()) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    Rectangle rect = new Rectangle(posX + 40 * (position % numberX) + 2, posY + (int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition, 16, 16);
                    temp.put(rect, is);
                    this.itemRender.renderItemAndEffectIntoGUI(is, 40 * (position % numberX) + 2, (int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition);
                    if (rect.equals(this.selected)) {
                        GL11.glDisable(3553);
                        GL11.glBegin(1);
                        GL11.glVertex2d((double) (40 * (position % numberX) + 2 + 16), (double) ((int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition + 4));
                        GL11.glVertex2d((double) (40 * (position % numberX) + 2 + 16 + 8), (double) ((int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition + 4 + 4));
                        GL11.glVertex2d((double) (40 * (position % numberX) + 2 + 16), (double) ((int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition + 8 + 4));
                        GL11.glVertex2d((double) (40 * (position % numberX) + 2 + 16 + 8), (double) ((int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition + 4 + 4));
                        GL11.glEnd();
                        GL11.glEnable(3553);
                        this.itemRender.renderItemIntoGUI((ItemStack) entry.getValue(), 40 * (position % numberX) + 24 + 2, (int) Math.floor((double) position / (double) numberX) * 16 + 2 + this.scrollPosition);
                    }

                    ++position;
                }
            }
        }

        this.positions = temp;
        GL11.glPopMatrix();
        GL11.glDisable(3089);
        GL11.glPushMatrix();
        var18 = this.positions.entrySet().iterator();

        while (var18.hasNext()) {
            entry = (Entry) var18.next();
            if (mouseX > ((Rectangle) entry.getKey()).getX() && mouseX < ((Rectangle) entry.getKey()).getX() + ((Rectangle) entry.getKey()).getWidth() && mouseY > ((Rectangle) entry.getKey()).getY() && mouseY < ((Rectangle) entry.getKey()).getY() + ((Rectangle) entry.getKey()).getHeight()) {
                this.itemRender.renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, (ItemStack) entry.getValue(), mouseX, mouseY, ((ItemStack) entry.getValue()).getDisplayName());
                break;
            }
        }

        GL11.glPopMatrix();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        Iterator var4 = this.positions.entrySet().iterator();

        while (true) {
            while (true) {
                Entry entry;
                do {
                    do {
                        do {
                            do {
                                if (!var4.hasNext()) {
                                    return super.mousePressed(mc, mouseX, mouseY);
                                }

                                entry = (Entry) var4.next();
                            } while (mouseX <= ((Rectangle) entry.getKey()).getX());
                        } while (mouseX >= ((Rectangle) entry.getKey()).getX() + ((Rectangle) entry.getKey()).getWidth());
                    } while (mouseY <= ((Rectangle) entry.getKey()).getY());
                } while (mouseY >= ((Rectangle) entry.getKey()).getY() + ((Rectangle) entry.getKey()).getHeight());

                this.makeSound = true;
                if (this.selected != null && this.selected.equals(entry.getKey())) {
                    this.selected = null;
                } else {
                    this.selected = (Rectangle) entry.getKey();
                }
            }
        }
    }

    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
        if (this.makeSound) {
            this.makeSound = false;
            super.playPressSound(soundHandlerIn);
        }

    }
}