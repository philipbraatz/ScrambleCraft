package com.doorfail.scramblecraft.recipe.recipe_book.gui;

import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleBookPage;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.doorfail.scramblecraft.util.Reference.MODID;

@SideOnly(Side.CLIENT)
public class GUIButtonScrambleRecipe extends GuiButton {
    private static final ResourceLocation RECIPE_BOOK = new ResourceLocation(MODID+":textures/gui/recipe_book.png");
    private RecipeBook book;
    private ScrambleList scrambleList;
    private float time;
    private float animationTime;
    private int currentIndex;

    public GUIButtonScrambleRecipe() {
        super(0, 0, 0, 25, 25, "");
    }

    public GUIButtonScrambleRecipe(GUIButtonScrambleRecipe button) {
        super(button.id, button.x, button.y, button.width, button.height, button.displayString);
        this.enabled = button.enabled;
        this.visible =  button.visible;
        this.packedFGColour = button.packedFGColour;
        this.scrambleList = button.getScrambleList();
    }

    public void init(ScrambleList recipeList, ScrambleBookPage bookPage, RecipeBook book) {
        this.scrambleList = recipeList;
        this.book = book;
        List<ModRecipe> list = recipeList.getRecipes(book.isFilteringCraftable());
        Iterator iRecipeIterator = list.iterator();

        while(iRecipeIterator.hasNext()) {
            IRecipe irecipe = (IRecipe)iRecipeIterator.next();
            if (true)//book.isNew(irecipe))//Error getting IDs for unique named color recipes
            {
                bookPage.recipesShown(list);
                this.animationTime = 15.0F;
                break;
            }
        }

    }

    public ScrambleList getScrambleList() {
        return this.scrambleList;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            if (!GuiScreen.isCtrlKeyDown()) {
                this.time += partialTicks;
            }

            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            RenderHelper.enableGUIStandardItemLighting();
            mc.getTextureManager().bindTexture(RECIPE_BOOK);
            GlStateManager.disableLighting();
            int i = 29;
            if (this.scrambleList != null &&
                    !this.scrambleList.containsCraftableRecipes()) {
                i += 25;
            }

            int j = 206;
            if (this.scrambleList != null &&
                    this.scrambleList.getRecipes(this.book.isFilteringCraftable()).size() > 1) {
                j += 25;
            }

            boolean flag = this.animationTime > 0.0F;
            if (flag) {
                float f = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(this.x + 8), (float)(this.y + 12), 0.0F);
                GlStateManager.scale(f, f, 1.0F);
                GlStateManager.translate((float)(-(this.x + 8)), (float)(-(this.y + 12)), 0.0F);
                this.animationTime -= partialTicks;
            }

            //draw subgroup
            this.drawTexturedModalRect(this.x, this.y, i, j, this.width, this.height);
            List<IRecipe> list = this.getOrderedRecipes();
            if(list.size() >0) {
                this.currentIndex = MathHelper.floor(this.time / 30.0F) % list.size();
                ItemStack itemstack = ( list.get(this.currentIndex)).getRecipeOutput();
                int k = 4;
                if (this.scrambleList != null &&
                        this.scrambleList.hasSingleResultItem() && this.getOrderedRecipes().size() > 1) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, this.x + k + 1, this.y + k + 1);
                    --k;
                }

                mc.getRenderItem().renderItemAndEffectIntoGUI(itemstack, this.x + k, this.y + k);
                if (flag) {
                    GlStateManager.popMatrix();
                }
            }
            else
            {
                //throw error
            }

            GlStateManager.enableLighting();
            RenderHelper.disableStandardItemLighting();
        }

    }

    //Display Methods of crafting
    private List<IRecipe> getOrderedRecipes() {
        if(this.scrambleList != null) {
            List<IRecipe> list = this.scrambleList.getDisplayRecipes(true);
            if (!this.book.isFilteringCraftable()) {
                list.addAll(this.scrambleList.getDisplayRecipes(false));
            }
            return list;
        }
        else return new ArrayList<>();


    }

    public boolean isOnlyOption() {
        return this.getOrderedRecipes().size() == 1;
    }

    public IRecipe getRecipe() {
        if (this.getOrderedRecipes().size() >this.currentIndex) {
            List<IRecipe> list = this.getOrderedRecipes();
            return (IRecipe) list.get(this.currentIndex);
        }
        else
            return null;
    }

    public List<String> getToolTipText(GuiScreen guiScreen) {
        if (this.getOrderedRecipes().size() >this.currentIndex)
        {
            ItemStack itemstack = ((IRecipe) this.getOrderedRecipes().get(this.currentIndex)).getRecipeOutput();
            List<String> list = guiScreen.getItemToolTip(itemstack);
            if (this.scrambleList != null &&
                    this.scrambleList.getRecipes(this.book.isFilteringCraftable()).size() > 1) {
                list.add(I18n.format("gui.recipebook.moreRecipes", new Object[0]));
            }

            return list;
        }
        else
            return new ArrayList<String>();
    }

    public int getButtonWidth() {
        return 25;
    }
}
