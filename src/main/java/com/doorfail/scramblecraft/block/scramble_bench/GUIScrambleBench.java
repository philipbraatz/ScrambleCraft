package com.doorfail.scramblecraft.block.scramble_bench;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.recipe_book.gui.GUIScrambleBook;
import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.UUID;

public class GUIScrambleBench extends GuiContainer implements IRecipeShownListener {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    private static final ResourceLocation SCRAMBLE_BENCH_TEXTURE = new ResourceLocation(Reference.MODID + ":textures/gui/scramble_bench.png");
    private final InventoryPlayer playerInv;
    private final TileEntityScrambleBench te;
    private final UUID playerId;

    //RecipeBook
    private GuiButtonImage recipeButton;
    private final GUIScrambleBook recipeBookGui;
    private boolean widthTooNarrow;

    public GUIScrambleBench(InventoryPlayer playerInv, TileEntityScrambleBench benchInv, EntityPlayer player)
    {
        super(new ContainerScrambleBench(player.world, player.getPosition(),benchInv,player));
        this.playerInv = playerInv;
        this.playerId = player.getUniqueID();
        this.te = benchInv;

        this.recipeBookGui = new GUIScrambleBook();

        //size of
        this.xSize = 175;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.mc, this.widthTooNarrow, ((ContainerScrambleBench)this.inventorySlots).craftMatrix);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
        this.recipeButton = new GuiButtonImage(10, this.guiLeft + 5, this.height / 2 - 49, 20, 19, 0, 168, 19, SCRAMBLE_BENCH_TEXTURE);
        this.buttonList.add(this.recipeButton);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        this.recipeBookGui.tick();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookGui.render(mouseX, mouseY, partialTicks);
            super.drawScreen(mouseX, mouseY, partialTicks);
            this.recipeBookGui.renderGhostRecipe(this.guiLeft, this.guiTop, true, partialTicks);
        }

        this.renderHoveredToolTip(mouseX, mouseY);
        this.recipeBookGui.renderTooltip(this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(SCRAMBLE_BENCH_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 000000);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, 72, 000000);
    }

    @Override
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!this.recipeBookGui.mouseClicked(mouseX, mouseY, mouseButton) && (!this.widthTooNarrow || !this.recipeBookGui.isVisible())) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

    }

    @Override
    protected boolean hasClickedOutside(int xin, int yin, int xout, int yout) {
        boolean flag = xin < xout || yin < yout || xin >= xout + this.xSize || yin >= yout + this.ySize;
        return this.recipeBookGui.hasClickedOutside(xin, yin, this.guiLeft, this.guiTop, this.xSize, this.ySize) && flag;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 10) {
            this.recipeBookGui.initVisuals(this.widthTooNarrow, ((ContainerScrambleBench)this.inventorySlots).craftMatrix, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            this.recipeButton.setPosition(this.guiLeft + 5, this.height / 2 - 49);
        }

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!this.recipeBookGui.keyPressed(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }

    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    public void onGuiClosed() {
        this.recipeBookGui.removed();
        super.onGuiClosed();
    }

    //doesnt actually do anything
    @Override
    public GuiRecipeBook func_194310_f() {
       return new GuiRecipeBook();
    }
}
