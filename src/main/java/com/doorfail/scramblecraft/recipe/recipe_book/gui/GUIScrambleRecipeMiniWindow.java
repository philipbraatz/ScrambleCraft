package com.doorfail.scramblecraft.recipe.recipe_book.gui;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleSubRecipes;
import com.google.common.collect.Lists;

import java.awt.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.doorfail.scramblecraft.util.Reference.MODID;

//Single miniWindowButton
@SideOnly(Side.CLIENT)
public class GUIScrambleRecipeMiniWindow extends Gui {
    private static final ResourceLocation RECIPE_BOOK_TEXTURE = new ResourceLocation(MODID+":textures/gui/recipe_book.png");
    private final List<miniWindowButton> miniWindowButtonList = Lists.newArrayList();
    private boolean visible;
    private int x;
    private int y;
    private Minecraft mc;
    private ScrambleSubRecipes recipeList;
    private ModRecipe lastRecipeClicked;
    private float time;

    public GUIScrambleRecipeMiniWindow() {
    }

    public void init(Minecraft mcIn, ScrambleSubRecipes recipeListIn, int x, int y, int flIn, int f5In, float offset, RecipeBook book) {
        this.mc = mcIn;
        this.recipeList = recipeListIn;
        boolean flag = book.isFilteringCraftable();
        List<IRecipe> list = recipeListIn.getDisplayRecipes(true);
        List<IRecipe> list1 = flag ? Collections.emptyList() : recipeListIn.getDisplayRecipes(false);
        int craftableCount = list.size();
        int recipeCount = craftableCount;
        int recipesPerRow = recipeCount <= 16 ? 4 : 5;
        int colCount = (int)Math.ceil((double)((float)recipeCount / (float)recipesPerRow));
        this.x = x;
        this.y = y;
        float endX = (float)(this.x + Math.min(recipeCount, recipesPerRow) * 25);
        float xCompare = (float)(flIn + 50);
        if (endX > xCompare) {
            this.x = (int)((float)this.x - offset * (float)((int)((endX - xCompare) / offset)));
        }

        float endY = (float)(this.y + colCount * 25);
        float yCompare = (float)(f5In + 50);
        if (endY > yCompare) {
            this.y = (int)((float)this.y - offset * (float)MathHelper.ceil((endY - yCompare) / offset));
        }

        float startY = (float)this.y;
        float startYCompare = (float)(f5In - 100);
        if (startY < startYCompare) {
            this.y = (int)((float)this.y - offset * (float)MathHelper.ceil((startY - startYCompare) / offset));
        }

        this.visible = true;//Shows recipe in mini window
        this.miniWindowButtonList.clear();

        for(int j1 = 0; j1 < recipeCount; ++j1) {
            boolean flag1 = j1 < craftableCount;
            this.miniWindowButtonList.add(new miniWindowButton(
                    this.x + 4 + 25 * (j1 % recipesPerRow),
                    this.y + 5 + 25 * (j1 / recipesPerRow),
                    flag1 ? ModRecipeRegistry.getMatchingModRecipe(
                                Minecraft.getMinecraft().player.getUniqueID(),
                                ModBlocks.SCRAMBLE_BENCH.getRegistryName(),
                                list.get(j1)) :
                            ModRecipeRegistry.getMatchingModRecipe(
                                Minecraft.getMinecraft().player.getUniqueID(),
                                ModBlocks.SCRAMBLE_BENCH.getRegistryName(),
                                list1.get(j1 - craftableCount)), flag1));
        }

        this.lastRecipeClicked = null;
    }

    public ScrambleSubRecipes getRecipeList() {
        return this.recipeList;
    }

    public ModRecipe getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    public boolean buttonClicked(int x, int y, int time) {
        if (time != 0) {
            return false;
        } else {
            Iterator var4 = this.miniWindowButtonList.iterator();

            miniWindowButton guiRecipeOverlay$miniWindowButton;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                guiRecipeOverlay$miniWindowButton = (miniWindowButton)var4.next();
            } while(!guiRecipeOverlay$miniWindowButton.mousePressed(this.mc, x, y));

            this.lastRecipeClicked = guiRecipeOverlay$miniWindowButton.recipe;
            return true;
        }
    }

    public void render(int x, int y, float time) {
        if (this.visible) {
            this.time += time;
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(RECIPE_BOOK_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 170.0F);
            int i = this.miniWindowButtonList.size() <= 16 ? 4 : 5;
            int spriteX = Math.min(this.miniWindowButtonList.size(), i);
            int spriteY = MathHelper.ceil((float)this.miniWindowButtonList.size() / (float)i);
            this.nineInchSprite(spriteX, spriteY, 24, 4, 82, 208);
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            Iterator var11 = this.miniWindowButtonList.iterator();

            while(var11.hasNext()) {
                miniWindowButton guirecipeoverlay$miniWindowButton = (miniWindowButton)var11.next();
                guirecipeoverlay$miniWindowButton.drawButton(this.mc, x, y, time);
            }

            GlStateManager.popMatrix();
        }

    }

    private void nineInchSprite(int width, int height, int startX, int startY, int endX, int endY) {
        int xy2wx =this.x + startY * 2 + width * startX;
        int exxy =endX + startX + startY;
        int eyxy =endY + startX + startY;
        int yy2hx =this.y + startY * 2 + height * startX;
        int eyy =endY + startY;
        int exy =endX+startY;
        int yy =this.y +startY;
        int xy =this.x +startY;

        Point endXYy =new Point (exy, eyy);

        this.drawTexturedModalRect(this.x,  this.y, endX,   endY, startY, startY);
        this.drawTexturedModalRect(xy2wx,   this.y, exxy,endY, startY, startY);
        this.drawTexturedModalRect(this.x,yy2hx, endX, eyxy, startY, startY);
        this.drawTexturedModalRect(xy2wx, yy2hx, exxy, eyxy, startY, startY);

        for(int i = 0; i < width; ++i)
        {
            this.drawTexturedModalRect(xy + i * startX, this.y, exy, endY, startX, startY);
            this.drawTexturedModalRect(xy + (i + 1) * startX, this.y, exy, endY, startY, startY);

            for(int j = 0; j < height; ++j) {
                if (i == 0) {
                    this.drawTexturedModalRect(this.x, yy + j * startX, endX, eyy, startY, startX);
                    this.drawTexturedModalRect(this.x, yy + (j + 1) * startX, endX, eyy, startY, startY);
                }

                this.drawTexturedModalRect(xy + i * startX,      yy + j * startX,         endXYy.x, endXYy.y, startX, startX);
                this.drawTexturedModalRect(xy + (i + 1) * startX,yy + j * startX,         endXYy.x, endXYy.y, startY, startX);
                this.drawTexturedModalRect(xy + i * startX,      yy + (j + 1) * startX,   endXYy.x, endXYy.y, startX, startY);
                this.drawTexturedModalRect(xy + (i + 1) * startX - 1, yy + (j + 1) * startX - 1, endXYy.x, endXYy.y, startY + 1, startY + 1);
                if (i == width - 1) {
                    this.drawTexturedModalRect(xy2wx, yy+ j * startX, exxy, eyy, startY, startX);
                    this.drawTexturedModalRect(xy2wx, yy + (j + 1) * startX, exxy, eyy, startY, startY);
                }
            }

            this.drawTexturedModalRect(this.x + startY + i * startX, yy2hx, exy, eyxy, startX, startY);
            this.drawTexturedModalRect(this.x + startY + (i + 1) * startX, yy2hx, exy, eyxy, startY, startY);
        }

    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    //Mini Crafting Window
    @SideOnly(Side.CLIENT)
    class miniWindowButton extends GuiButton {
        private final ModRecipe recipe;
        private final boolean isCraftable;

        public miniWindowButton(int x, int y, ModRecipe recipe, boolean craftable) {
            super(0, x, y, "");
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.isCraftable = craftable;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableAlpha();
            mc.getTextureManager().bindTexture(GUIScrambleRecipeMiniWindow.RECIPE_BOOK_TEXTURE);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            int endX = !this.isCraftable? 152+26 :26;
            int endY = !this.isCraftable?78+26:26;

            this.drawTexturedModalRect(this.x, this.y, endX, endY, this.width, this.height);
            int width = 3;
            int height = 3;
            //TODO Look into changing
            if (this.recipe instanceof IShapedRecipe) {
                IShapedRecipe shapedrecipes = (IShapedRecipe)this.recipe;
                width = shapedrecipes.getRecipeWidth();
                height = shapedrecipes.getRecipeHeight();
            }

            Iterator<Ingredient> iterator = this.recipe.getIngredients().iterator();

            for(int i1 = 0; i1 < height; ++i1) {
                int j1 = 3 + i1 * 7;

                for(int k1 = 0; k1 < width; ++k1)
                    if (iterator.hasNext()) {
                        ItemStack[] aitemstack = (iterator.next()).getMatchingStacks();
                        if (aitemstack.length != 0) {
                            int l1 = 3 + k1 * 7;
                            GlStateManager.pushMatrix();
                            float f = 0.42F;
                            int i2 = (int) ((float) (this.x + l1) / 0.42F - 3.0F);
                            int j2 = (int) ((float) (this.y + j1) / 0.42F - 3.0F);
                            GlStateManager.scale(0.42F, 0.42F, 1.0F);
                            GlStateManager.enableLighting();
                            mc.getRenderItem().renderItemAndEffectIntoGUI(aitemstack[MathHelper.floor(GUIScrambleRecipeMiniWindow.this.time / 30.0F) % aitemstack.length], i2, j2);
                            GlStateManager.disableLighting();
                            GlStateManager.popMatrix();
                        }
                    }
            }

            GlStateManager.disableAlpha();
            RenderHelper.disableStandardItemLighting();
        }
    }
}
