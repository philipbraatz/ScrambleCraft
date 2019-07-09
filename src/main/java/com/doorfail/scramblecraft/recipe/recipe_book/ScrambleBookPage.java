package com.doorfail.scramblecraft.recipe.recipe_book;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.gui.recipebook.IRecipeUpdateListener;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ScrambleBookPage {
    public List<GUIButtonScrambleRecipe> buttons = Lists.newArrayListWithCapacity(20);
    private GUIButtonScrambleRecipe hoveredButton;
    private GUIScrambleRecipeOverlay overlay = new GUIScrambleRecipeOverlay();
    private Minecraft minecraft;
    private List<IRecipeUpdateListener> listeners = Lists.newArrayList();
    private List<ScrambleList> recipeLists;
    private GuiButtonToggle forwardButton;
    private GuiButtonToggle backButton;
    private int totalPages;
    private int currentPage;
    private ScrambleBook scrambleBook;
    private IRecipe lastClickedRecipe;
    private ScrambleList lastClickedRecipeList;

    public ScrambleBookPage() {
        for(int i = 0; i < 20; ++i) buttons.add(new GUIButtonScrambleRecipe());

    }

    public void init(Minecraft mc, int x, int y) {
        this.minecraft = mc;
        this.scrambleBook = new ScrambleBook(mc.player.getRecipeBook());//Inject recipes here

        for(int i = 0; i < this.buttons.size(); ++i) {
            (this.buttons.get(i)).setPosition(x + 11 + 25 * (i % 5), y + 31 + 25 * (i / 5));
        }

        this.forwardButton = new GuiButtonToggle(0, x + 93, y + 137, 12, 17, false);
        this.forwardButton.initTextureValues(1, 208, 13, 18, GUIScrambleBook.RECIPE_BOOK);
        this.backButton = new GuiButtonToggle(0, x + 38, y + 137, 12, 17, true);
        this.backButton.initTextureValues(1, 208, 13, 18, GUIScrambleBook.RECIPE_BOOK);
    }

    public void addListener(GUIScrambleBook book) {
        this.listeners.remove(book);
        this.listeners.add(book);
    }

    public void updateLists(List<ScrambleList> recipeLists, boolean goToFirstPage) {
        this.recipeLists = recipeLists;
        this.totalPages = (int)Math.ceil((double)recipeLists.size() / 20.0D);
        if (this.totalPages <= this.currentPage || goToFirstPage) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for(int j = 0; j < this.buttons.size(); ++j) {
            GUIButtonScrambleRecipe guibuttonrecipe = this.buttons.get(j);
            if (i + j < this.recipeLists.size()) {
                ScrambleList recipelist = this.recipeLists.get(i + j);
                guibuttonrecipe.init(recipelist, this, this.scrambleBook);
                guibuttonrecipe.visible = true;
            } else {
                guibuttonrecipe.visible = false;
            }
        }

        this.updateArrowButtons();
    }

    private void updateArrowButtons() {
        this.forwardButton.visible = this.totalPages > 1 && this.currentPage < this.totalPages - 1;
        this.backButton.visible = this.totalPages > 1 && this.currentPage > 0;
    }

    public void render(int p_194191_1_, int p_194191_2_, int p_194191_3_, int p_194191_4_, float p_194191_5_) {
        if (this.totalPages > 1) {
            String s = this.currentPage + 1 + "/" + this.totalPages;
            int i = this.minecraft.fontRenderer.getStringWidth(s);
            this.minecraft.fontRenderer.drawString(s, p_194191_1_ - i / 2 + 73, p_194191_2_ + 141, -1);
        }

        RenderHelper.disableStandardItemLighting();
        this.hoveredButton = null;
        Iterator var8 = this.buttons.iterator();

        while(var8.hasNext()) {
            GUIButtonScrambleRecipe guibuttonrecipe = (GUIButtonScrambleRecipe) var8.next();//may error
            guibuttonrecipe.drawButton(this.minecraft, p_194191_3_, p_194191_4_, p_194191_5_);
            if (guibuttonrecipe.visible && guibuttonrecipe.isMouseOver()) {
                this.hoveredButton = guibuttonrecipe;
            }
        }

        this.backButton.drawButton(this.minecraft, p_194191_3_, p_194191_4_, p_194191_5_);
        this.forwardButton.drawButton(this.minecraft, p_194191_3_, p_194191_4_, p_194191_5_);
        this.overlay.render(p_194191_3_, p_194191_4_, p_194191_5_);
    }

    public void renderTooltip(int p_193721_1_, int p_193721_2_) {
        if (this.minecraft.currentScreen != null && this.hoveredButton != null && !this.overlay.isVisible()) {
            this.minecraft.currentScreen.drawHoveringText(this.hoveredButton.getToolTipText(this.minecraft.currentScreen), p_193721_1_, p_193721_2_);
        }

    }

    @Nullable
    public IRecipe getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public ScrambleList getLastClickedRecipeList() {
        return this.lastClickedRecipeList;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(int p_194196_1_, int p_194196_2_, int p_194196_3_, int p_194196_4_, int p_194196_5_, int p_194196_6_, int p_194196_7_) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeList = null;
        if (this.overlay.isVisible()) {
            if (this.overlay.buttonClicked(p_194196_1_, p_194196_2_, p_194196_3_)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeList = this.overlay.getRecipeList();
            } else {
                this.overlay.setVisible(false);
            }

            return true;
        } else if (this.forwardButton.mousePressed(this.minecraft, p_194196_1_, p_194196_2_) && p_194196_3_ == 0) {
            this.forwardButton.playPressSound(this.minecraft.getSoundHandler());
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else if (this.backButton.mousePressed(this.minecraft, p_194196_1_, p_194196_2_) && p_194196_3_ == 0) {
            this.backButton.playPressSound(this.minecraft.getSoundHandler());
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        } else {
            Iterator var8 = this.buttons.iterator();

            GUIButtonScrambleRecipe guibuttonrecipe;
            do {
                if (!var8.hasNext()) {
                    return false;
                }
                guibuttonrecipe = (GUIButtonScrambleRecipe)var8.next();

            } while(!guibuttonrecipe.mousePressed(this.minecraft, p_194196_1_, p_194196_2_));

            guibuttonrecipe.playPressSound(this.minecraft.getSoundHandler());
            if (p_194196_3_ == 0) {
                this.lastClickedRecipe = guibuttonrecipe.getRecipe();
                this.lastClickedRecipeList = guibuttonrecipe.getList();
            } else if (!this.overlay.isVisible() && !guibuttonrecipe.isOnlyOption()) {
                this.overlay.init(this.minecraft, guibuttonrecipe.getList(),
                        guibuttonrecipe.x, guibuttonrecipe.y,
                        p_194196_4_ + p_194196_6_ / 2, p_194196_5_ + 13 + p_194196_7_ / 2,
                        (float)guibuttonrecipe.getButtonWidth(),this.scrambleBook);
            }

            return true;
        }
    }

    public void recipesShown(List<IRecipe> recipes) {
        Iterator var2 = this.listeners.iterator();

        while(var2.hasNext()) {
            IRecipeUpdateListener irecipeupdatelistener = (IRecipeUpdateListener)var2.next();
            irecipeupdatelistener.recipesShown(recipes);
        }

    }
}