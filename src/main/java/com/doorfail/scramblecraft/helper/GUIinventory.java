package com.doorfail.scramblecraft.helper;

import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//TODO use class for all GUI inventories
public class GUIinventory extends GuiContainer {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    private static ResourceLocation INTERFACE_TEXTURE;// = new ResourceLocation("NA:GUInull");
    private final InventoryPlayer playerInv;
    private final TileEntity te;

    public GUIinventory(Container container,InventoryPlayer player,TileEntity tileEntity, ResourceLocation texture)
    {
        super(container);
        this.playerInv = player;
        this.te = tileEntity;//used for display name

        this.xSize = 175;
        this.ySize = 222;

        this.INTERFACE_TEXTURE =texture;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(INTERFACE_TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 000000);
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 92, 000000);
    }
}
