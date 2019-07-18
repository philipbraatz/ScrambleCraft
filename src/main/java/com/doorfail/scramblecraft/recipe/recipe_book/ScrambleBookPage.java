package com.doorfail.scramblecraft.recipe.recipe_book;

import com.doorfail.scramblecraft.block.scramble_bench.ContainerScrambleBench;
import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.doorfail.scramblecraft.recipe.recipe_book.gui.GUIButtonScrambleRecipe;
import com.doorfail.scramblecraft.recipe.recipe_book.gui.GUIScrambleBook;
import com.doorfail.scramblecraft.recipe.recipe_book.gui.GUIScrambleRecipeMiniWindow;
import com.doorfail.scramblecraft.util.ServerScrambleBookHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.gui.recipebook.IRecipeUpdateListener;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.stats.RecipeBook;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
public class ScrambleBookPage {
    public List<GUIButtonScrambleRecipe> buttons = Lists.newArrayListWithCapacity(20);
    private GUIButtonScrambleRecipe hoveredButton;
    private GUIScrambleRecipeMiniWindow overlay = new GUIScrambleRecipeMiniWindow();
    private Minecraft minecraft;
    private List<IRecipeUpdateListener> listeners = Lists.newArrayList();
    private List<ScrambleSubRecipes> recipeLists;
    private GuiButtonToggle forwardButton;
    private GuiButtonToggle backButton;
    private int totalPages;
    private int currentPage;
    private RecipeBook scrambleBook;
    private ModRecipe lastClickedRecipe;
    private ScrambleSubRecipes lastClickedRecipeList;
    private ServerScrambleBookHelper serverHelper;

    public ScrambleBookPage() {
        for(int i = 0; i < 20; ++i) buttons.add(new GUIButtonScrambleRecipe());

    }

    public void init(Minecraft mc, int x, int y, ServerScrambleBookHelper helper) {
        this.minecraft = mc;
        this.scrambleBook = mc.player.getRecipeBook();//Inject recipes here
        this.serverHelper =helper;

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

    //Tab Change
    public void updateLists(List<ScrambleSubRecipes> recipeLists, boolean goToFirstPage) {
        this.recipeLists = recipeLists;
        this.totalPages = (int)Math.ceil((double)recipeLists.size() / 20.0D);
        if (this.totalPages <= this.currentPage || goToFirstPage) {
            this.currentPage = 0;
        }

        this.updateButtonsForPage();
    }

    //Page Change
    private void updateButtonsForPage() {
        int i = 20 * this.currentPage;

        for(int j = 0; j < this.buttons.size(); ++j) {
            GUIButtonScrambleRecipe guibuttonrecipe = this.buttons.get(j);
            if (this.recipeLists != null &&
                    i + j < this.recipeLists.size()) {
                ScrambleSubRecipes recipelist = this.recipeLists.get(i + j);
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
    public ModRecipe getLastClickedRecipe() {
        return this.lastClickedRecipe;
    }

    @Nullable
    public ScrambleSubRecipes getLastClickedRecipeList() {
        return this.lastClickedRecipeList;
    }

    public void setInvisible() {
        this.overlay.setVisible(false);
    }

    public boolean mouseClicked(int x, int y, int time, int f1In, int f5In, int f1Half, int f5Half) {
        this.lastClickedRecipe = null;
        this.lastClickedRecipeList = null;
        if (this.overlay.isVisible())
        {
            if (this.overlay.buttonClicked(x, y, time)) {
                this.lastClickedRecipe = this.overlay.getLastRecipeClicked();
                this.lastClickedRecipeList = this.overlay.getRecipeList();
            } else this.overlay.setVisible(false);

            return true;
        }
        else if (this.forwardButton.mousePressed(this.minecraft, x, y) && time == 0) //Forward
        {
            this.forwardButton.playPressSound(this.minecraft.getSoundHandler());
            ++this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        else if (this.backButton.mousePressed(this.minecraft, x, y) && time == 0) //Back
        {
            this.backButton.playPressSound(this.minecraft.getSoundHandler());
            --this.currentPage;
            this.updateButtonsForPage();
            return true;
        }
        else
        {
            Iterator var8 = this.buttons.iterator();

            GUIButtonScrambleRecipe guibuttonrecipe;
            do {
                if (!var8.hasNext()) {
                    return false;
                }
                guibuttonrecipe = (GUIButtonScrambleRecipe)var8.next();

            } while(!guibuttonrecipe.mousePressed(this.minecraft, x, y));

            guibuttonrecipe.playPressSound(this.minecraft.getSoundHandler());
            serverHelper.placeRecipe(ContainerScrambleBench.getEntityPlayerMP(minecraft.player.getUniqueID()),guibuttonrecipe.getRecipe(),false);

            if (time == 0) {
                this.lastClickedRecipe = ModRecipeRegistry.getMatchingModRecipe(
                        Minecraft.getMinecraft().player.getUniqueID(),
                        ModBlocks.SCRAMBLE_BENCH.getRegistryName(),guibuttonrecipe.getRecipe());
                this.lastClickedRecipeList = guibuttonrecipe.getScrambleSubRecipes();
            } else if (!this.overlay.isVisible() && !guibuttonrecipe.isOnlyOption()) {
                this.overlay.init(this.minecraft, guibuttonrecipe.getScrambleSubRecipes(),
                        guibuttonrecipe.x, guibuttonrecipe.y,
                        f1In + f1Half / 2, f5In + 13 + f5Half / 2,
                        (float)guibuttonrecipe.getButtonWidth(),this.scrambleBook);
            }

            return true;
        }
    }

    public void recipesShown(List<ModRecipe> recipes) {
        Iterator var2 = this.listeners.iterator();

        while(var2.hasNext()) {
            IRecipeUpdateListener irecipeupdatelistener = (IRecipeUpdateListener)var2.next();
            irecipeupdatelistener.recipesShown(ModRecipeRegistry.getIRecipeList(recipes));
        }

    }
}