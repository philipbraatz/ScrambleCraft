package com.doorfail.scramblecraft.block.scramble_bench;

import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GUIScrambleBench extends GuiContainer {
    private static Logger logger = LogManager.getLogger(Reference.MODID);

    private static final ResourceLocation SCRAMBLE_BENCH_TEXTURE = new ResourceLocation(Reference.MODID + ":textures/gui/scramble_bench.png");
    private final InventoryPlayer playerInv;
    private final TileEntityScrambleBench te;

    public GUIScrambleBench(InventoryPlayer playerInv, TileEntityScrambleBench benchInv, EntityPlayer player)
    {
        super(new ContainerScrambleBench(playerInv, benchInv, player));
        this.playerInv = playerInv;
        this.te = benchInv;

        this.xSize = 175;
        this.ySize = 222;
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
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 92, 000000);
    }
}
