package com.doorfail.scramblecraft.plugin.jei.ingredient;

import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class DynamicItemStackRenderer  implements IIngredientRenderer<DynamicItemStack> {

    @Override
    public void render(Minecraft minecraft, int xPosition, int yPosition, @Nullable DynamicItemStack ingredient) {
        if (ingredient != null) {
            GlStateManager.enableDepth();
            RenderHelper.enableGUIStandardItemLighting();
            FontRenderer font = getFontRenderer(minecraft, ingredient);
            minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, ingredient.getItemStack(), xPosition, yPosition);
            minecraft.getRenderItem().renderItemOverlayIntoGUI(font, ingredient.getItemStack(), xPosition, yPosition, null);
            GlStateManager.disableBlend();
            RenderHelper.disableStandardItemLighting();
        }
    }

    @Override
    public List<String> getTooltip(Minecraft minecraft, DynamicItemStack ingredient, ITooltipFlag tooltipFlag) {
        EntityPlayer player = minecraft.player;
        List<String> list;
        try {
            list = ingredient.getTooltip(player, tooltipFlag);
        } catch (RuntimeException | LinkageError e) {
            //String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient);
            //Log.get().error("Failed to get tooltip: {}", itemStackInfo, e);
            list = new ArrayList<>();
            list.add(TextFormatting.RED + "scramblecraft.tooltip.error.crash");
            return list;
        }

        IRarity rarity;
        try {
            rarity = ingredient.getItem().getForgeRarity(ingredient.getItemStack());
        } catch (RuntimeException | LinkageError e) {
            //String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient);
            //Log.get().error("Failed to get rarity: {}", itemStackInfo, e);
            rarity = EnumRarity.COMMON;
        }

        for (int k = 0; k < list.size(); ++k) {
            if (k == 0) {
                list.set(k, rarity.getColor() + list.get(k));
            } else {
                list.set(k, TextFormatting.GRAY + list.get(k));
            }
        }

        return list;
    }
}
