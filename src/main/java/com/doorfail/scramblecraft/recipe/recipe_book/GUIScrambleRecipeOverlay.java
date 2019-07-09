package com.doorfail.scramblecraft.recipe.recipe_book;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.recipebook.RecipeList;
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

@SideOnly(Side.CLIENT)
public class GUIScrambleRecipeOverlay extends Gui {
    private static final ResourceLocation RECIPE_BOOK_TEXTURE = new ResourceLocation(MODID+":textures/gui/recipe_book.png");
    private final List<GUIScrambleRecipeOverlay.Button> buttonList = Lists.newArrayList();
    private boolean visible;
    private int x;
    private int y;
    private Minecraft mc;
    private ScrambleList recipeList;
    private IRecipe lastRecipeClicked;
    private float time;

    public GUIScrambleRecipeOverlay() {
    }

    public void init(Minecraft mcIn, ScrambleList recipeListIn, int x, int y, int flIn, int f5In, float offset, ScrambleBook p_191845_8_) {
        this.mc = mcIn;
        this.recipeList = recipeListIn;
        boolean flag = p_191845_8_.isFilteringCraftable();
        List<IRecipe> list = recipeListIn.getDisplayRecipes(true);
        List<IRecipe> list1 = flag ? Collections.emptyList() : recipeListIn.getDisplayRecipes(false);
        int i = list.size();
        int j = i + list1.size();
        int k = j <= 16 ? 4 : 5;
        int l = (int)Math.ceil((double)((float)j / (float)k));
        this.x = x;
        this.y = y;
        int i1 = 1;
        float f = (float)(this.x + Math.min(j, k) * 25);
        float f1 = (float)(flIn + 50);
        if (f > f1) {
            this.x = (int)((float)this.x - offset * (float)((int)((f - f1) / offset)));
        }

        float f2 = (float)(this.y + l * 25);
        float f3 = (float)(f5In + 50);
        if (f2 > f3) {
            this.y = (int)((float)this.y - offset * (float)MathHelper.ceil((f2 - f3) / offset));
        }

        float f4 = (float)this.y;
        float f5 = (float)(f5In - 100);
        if (f4 < f5) {
            this.y = (int)((float)this.y - offset * (float)MathHelper.ceil((f4 - f5) / offset));
        }

        this.visible = true;
        this.buttonList.clear();

        for(int j1 = 0; j1 < j; ++j1) {
            boolean flag1 = j1 < i;
            this.buttonList.add(new GUIScrambleRecipeOverlay.Button(this.x + 4 + 25 * (j1 % k), this.y + 5 + 25 * (j1 / k), flag1 ? (IRecipe)list.get(j1) : (IRecipe)list1.get(j1 - i), flag1));
        }

        this.lastRecipeClicked = null;
    }

    public ScrambleList getRecipeList() {
        return this.recipeList;
    }

    public IRecipe getLastRecipeClicked() {
        return this.lastRecipeClicked;
    }

    public boolean buttonClicked(int x, int y, int time) {
        if (time != 0) {
            return false;
        } else {
            Iterator var4 = this.buttonList.iterator();

            GUIScrambleRecipeOverlay.Button guiRecipeOverlay$button;
            do {
                if (!var4.hasNext()) {
                    return false;
                }

                guiRecipeOverlay$button = (GUIScrambleRecipeOverlay.Button)var4.next();
            } while(!guiRecipeOverlay$button.mousePressed(this.mc, x, y));

            this.lastRecipeClicked = guiRecipeOverlay$button.recipe;
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
            int i = this.buttonList.size() <= 16 ? 4 : 5;
            int j = Math.min(this.buttonList.size(), i);
            int k = MathHelper.ceil((float)this.buttonList.size() / (float)i);
            int l = 1;
            int i1 = 1;
            int j1 = 1;
            int k1 = 1;
            this.nineInchSprite(j, k, 24, 4, 82, 208);
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
            Iterator var11 = this.buttonList.iterator();

            while(var11.hasNext()) {
                GUIScrambleRecipeOverlay.Button guirecipeoverlay$button = (GUIScrambleRecipeOverlay.Button)var11.next();
                guirecipeoverlay$button.drawButton(this.mc, x, y, time);
            }

            GlStateManager.popMatrix();
        }

    }

    private void nineInchSprite(int x, int y, int width, int height, int textureX, int textureY) {
        this.drawTexturedModalRect(this.x, this.y, textureX, textureY, height, height);
        this.drawTexturedModalRect(this.x + height * 2 + x * width, this.y, textureX + width + height, textureY, height, height);
        this.drawTexturedModalRect(this.x, this.y + height * 2 + y * width, textureX, textureY + width + height, height, height);
        this.drawTexturedModalRect(this.x + height * 2 + x * width, this.y + height * 2 + y * width, textureX + width + height, textureY + width + height, height, height);

        for(int i = 0; i < x; ++i) {
            this.drawTexturedModalRect(this.x + height + i * width, this.y, textureX + height, textureY, width, height);
            this.drawTexturedModalRect(this.x + height + (i + 1) * width, this.y, textureX + height, textureY, height, height);

            for(int j = 0; j < y; ++j) {
                if (i == 0) {
                    this.drawTexturedModalRect(this.x, this.y + height + j * width, textureX, textureY + height, height, width);
                    this.drawTexturedModalRect(this.x, this.y + height + (j + 1) * width, textureX, textureY + height, height, height);
                }

                this.drawTexturedModalRect(this.x + height + i * width, this.y + height + j * width, textureX + height, textureY + height, width, width);
                this.drawTexturedModalRect(this.x + height + (i + 1) * width, this.y + height + j * width, textureX + height, textureY + height, height, width);
                this.drawTexturedModalRect(this.x + height + i * width, this.y + height + (j + 1) * width, textureX + height, textureY + height, width, height);
                this.drawTexturedModalRect(this.x + height + (i + 1) * width - 1, this.y + height + (j + 1) * width - 1, textureX + height, textureY + height, height + 1, height + 1);
                if (i == x - 1) {
                    this.drawTexturedModalRect(this.x + height * 2 + x * width, this.y + height + j * width, textureX + width + height, textureY + height, height, width);
                    this.drawTexturedModalRect(this.x + height * 2 + x * width, this.y + height + (j + 1) * width, textureX + width + height, textureY + height, height, height);
                }
            }

            this.drawTexturedModalRect(this.x + height + i * width, this.y + height * 2 + y * width, textureX + height, textureY + width + height, width, height);
            this.drawTexturedModalRect(this.x + height + (i + 1) * width, this.y + height * 2 + y * width, textureX + height, textureY + width + height, height, height);
        }

    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return this.visible;
    }

    @SideOnly(Side.CLIENT)
    class Button extends GuiButton {
        private final IRecipe recipe;
        private final boolean isCraftable;

        public Button(int x, int y, IRecipe recipe, boolean craftable) {
            super(0, x, y, "");
            this.width = 24;
            this.height = 24;
            this.recipe = recipe;
            this.isCraftable = craftable;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableAlpha();
            mc.getTextureManager().bindTexture(GUIScrambleRecipeOverlay.RECIPE_BOOK_TEXTURE);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = 152;
            if (!this.isCraftable) {
                i += 26;
            }

            int j = 78;
            if (this.hovered) {
                j += 26;
            }

            this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
            int k = 3;
            int l = 3;
            //TODO Look into changing
            if (this.recipe instanceof IShapedRecipe) {
                IShapedRecipe shapedrecipes = (IShapedRecipe)this.recipe;
                k = shapedrecipes.getRecipeWidth();
                l = shapedrecipes.getRecipeHeight();
            }

            Iterator<Ingredient> iterator = this.recipe.getIngredients().iterator();

            for(int i1 = 0; i1 < l; ++i1) {
                int j1 = 3 + i1 * 7;

                for(int k1 = 0; k1 < k; ++k1)
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
                            mc.getRenderItem().renderItemAndEffectIntoGUI(aitemstack[MathHelper.floor(GUIScrambleRecipeOverlay.this.time / 30.0F) % aitemstack.length], i2, j2);
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
