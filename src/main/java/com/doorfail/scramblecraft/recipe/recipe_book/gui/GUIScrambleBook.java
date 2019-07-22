package com.doorfail.scramblecraft.recipe.recipe_book.gui;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.recipe.ModRecipe;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleBookClient;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleBookPage;
import com.doorfail.scramblecraft.recipe.recipe_book.ScrambleSubRecipes;
import com.doorfail.scramblecraft.util.Reference;
import com.doorfail.scramblecraft.util.ServerScrambleBookHelper;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButtonToggle;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.recipebook.GhostRecipe.GhostIngredient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.RecipeItemHelper;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

//Main scrambleBook
@SideOnly(Side.CLIENT)
public class GUIScrambleBook extends Gui implements net.minecraft.client.gui.recipebook.IRecipeUpdateListener {
    public static final ResourceLocation RECIPE_BOOK = new ResourceLocation(Reference.MODID + ":textures/recipe_book/recipe_book.png");
    private int xOffset;
    private int width;
    private int height;
    private final net.minecraft.client.gui.recipebook.GhostRecipe ghostRecipe = new net.minecraft.client.gui.recipebook.GhostRecipe();
    private final List<net.minecraft.client.gui.recipebook.GuiButtonRecipeTab> recipeTabs;
    private net.minecraft.client.gui.recipebook.GuiButtonRecipeTab currentTab;
    private GuiButtonToggle toggleRecipesBtn;
    private InventoryCrafting craftingSlots;
    private Minecraft mc;
    private GuiTextField searchBar;
    private String lastSearch;
    private RecipeBook recipeBook;
    private final ScrambleBookPage scrambleBookPage;
    private RecipeItemHelper stackedContents;
    private int timesInventoryChanged;

    private ServerScrambleBookHelper serverHelper=new ServerScrambleBookHelper();

    public GUIScrambleBook() {
        this.recipeTabs = Lists.newArrayList(
                new net.minecraft.client.gui.recipebook.GuiButtonRecipeTab(0, CreativeTabs.SEARCH),
                new net.minecraft.client.gui.recipebook.GuiButtonRecipeTab(0, CreativeTabs.TOOLS),
                new net.minecraft.client.gui.recipebook.GuiButtonRecipeTab(0, CreativeTabs.BUILDING_BLOCKS),
                new net.minecraft.client.gui.recipebook.GuiButtonRecipeTab(0, CreativeTabs.MISC),
                new net.minecraft.client.gui.recipebook.GuiButtonRecipeTab(0, CreativeTabs.REDSTONE));
        this.lastSearch = "";
        this.scrambleBookPage = new ScrambleBookPage();
        this.stackedContents = new RecipeItemHelper();
    }

    public void init(int width, int height, Minecraft mc, boolean someBool, InventoryCrafting inventory) {
        this.mc = mc;
        this.width = width;
        this.height = height;
        this.craftingSlots = inventory;
        this.recipeBook = mc.player.getRecipeBook();
        this.timesInventoryChanged = mc.player.inventory.getTimesChanged();
        this.currentTab = this.recipeTabs.get(0);
        this.currentTab.setStateTriggered(true);
        if (this.isVisible()) {
            this.initVisuals(someBool, inventory, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
        }

        Keyboard.enableRepeatEvents(true);
    }

    public void initVisuals(boolean p_193014_1_, InventoryCrafting p_193014_2_, ResourceLocation registryName) {
        this.xOffset = p_193014_1_ ? 0 : 86;
        int i = (this.width - 147) / 2 - this.xOffset;
        int j = (this.height - 166) / 2;
        this.stackedContents.clear();
        this.mc.player.inventory.fillStackedContents(this.stackedContents, false);
        p_193014_2_.fillStackedContents(this.stackedContents);
        this.searchBar = new GuiTextField(0, this.mc.fontRenderer, i + 25, j + 14, 80, this.mc.fontRenderer.FONT_HEIGHT + 5);
        this.searchBar.setMaxStringLength(3);//DEBUG normal 50
        this.searchBar.setEnableBackgroundDrawing(false);
        this.searchBar.setVisible(true);
        this.searchBar.setTextColor(16777215);
        this.searchBar.setText("WIP");
        this.lastSearch = "WIP";
        this.scrambleBookPage.init(this.mc, i, j,serverHelper);
        this.scrambleBookPage.addListener(this);
        this.toggleRecipesBtn = new GuiButtonToggle(0, i + 110, j + 12, 26, 16, this.recipeBook.isFilteringCraftable());
        this.toggleRecipesBtn.initTextureValues(152, 41, 28, 18, RECIPE_BOOK);
        this.updateCollections(false, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
        this.updateTabs();
    }

    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    public int updateScreenPosition(boolean p_193011_1_, int p_193011_2_, int p_193011_3_) {
        int i;
        if (this.isVisible() && !p_193011_1_) {
            i = 177 + (p_193011_2_ - p_193011_3_ - 200) / 2;
        } else {
            i = (p_193011_2_ - p_193011_3_) / 2;
        }

        return i;
    }

    public void toggleVisibility() {
        this.setVisible(!this.isVisible());
    }

    public boolean isVisible() {
        return this.recipeBook.isGuiOpen();
    }

    private void setVisible(boolean visible) {
        this.recipeBook.setGuiOpen(visible);
        if (!visible) {
            this.scrambleBookPage.setInvisible();
        }

        this.sendUpdateSettings();
    }

    public void slotClicked(@Nullable Slot slotIn) {
        if (slotIn != null && slotIn.slotNumber <= 9) {
            this.ghostRecipe.clear();
            if (this.isVisible()) {
                this.updateStackedContents();
            }
        }

    }

    //UPDATER
    private void updateCollections(boolean firstPage, ResourceLocation craftingBlock) {
        //this.currentTab = recipeTabs.get(2);
        if(ScrambleBookClient.RECIPES_BY_TAB.containsKey(this.currentTab.getCategory()))
        {

            List<ScrambleSubRecipes> list = ScrambleBookClient.RECIPES_BY_TAB.get(this.currentTab.getCategory());

            //Dont know how this become empty
            if(list.size() ==0) {
                ScrambleBookClient.rebuildTable();
                list = ScrambleBookClient.RECIPES_BY_TAB.get(this.currentTab.getCategory());
            }
            List<ScrambleSubRecipes> list1 = Lists.newArrayList(list);

            boolean refreshed =false;
            Iterator iterList = list.iterator();
            while (iterList.hasNext())
            {
                ScrambleSubRecipes scrambleSubRecipes =(ScrambleSubRecipes) iterList.next();
                for (ModRecipe r: scrambleSubRecipes.getRecipes())
                    if (!refreshed && r.checkResult().get(0).getItem() == Items.AIR) {
                        ScrambleBookClient.rebuildTable();//update recipe all outputs every time AIR is show as craftable
                        list1 = ScrambleBookClient.RECIPES_BY_TAB.get(this.currentTab.getCategory());//update local list
                        refreshed =true;
                    }

                scrambleSubRecipes.canCraft(this.stackedContents, this.craftingSlots.getWidth(), this.craftingSlots.getHeight(), this.recipeBook);
            }

            //Search Bar Sorting
            String s = this.searchBar.getText();
            if (false)//!s.isEmpty())//TODO Reimplement search functionality
            {
                ObjectSet<ScrambleSubRecipes> objectset = new ObjectLinkedOpenHashSet(this.mc.getSearchTree(SearchTreeManager.RECIPES).search(s.toLowerCase(Locale.ROOT)));
                list1.removeIf((recipe) -> {
                    return !objectset.contains(recipe);
                });
            }

            if (this.recipeBook.isFilteringCraftable()) {
                list1.removeIf((recipe) -> {
                    return !recipe.containsCraftableRecipes();
                });
            }

            this.scrambleBookPage.updateLists(list1, firstPage);
        }
    }

    private void updateTabs() {
        int i = (this.width - 147) / 2 - this.xOffset - 30;
        int j = (this.height - 166) / 2 + 3;
        int k = 1;
        int l = 0;
        Iterator var5 = this.recipeTabs.iterator();

        while(var5.hasNext()) {
            net.minecraft.client.gui.recipebook.GuiButtonRecipeTab guibuttonrecipetab = (net.minecraft.client.gui.recipebook.GuiButtonRecipeTab)var5.next();
            CreativeTabs creativetabs = guibuttonrecipetab.getCategory();
            if (creativetabs == CreativeTabs.SEARCH) {
                guibuttonrecipetab.visible = true;
                guibuttonrecipetab.setPosition(i, j + 27 * l++);
            } else if (guibuttonrecipetab.updateVisibility()) {
                guibuttonrecipetab.setPosition(i, j + 27 * l++);
                guibuttonrecipetab.startAnimation(this.mc);
            }
        }

    }

    public void tick() {
        if (this.isVisible() && this.timesInventoryChanged != this.mc.player.inventory.getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.mc.player.inventory.getTimesChanged();
        }

    }

    private void updateStackedContents() {
        this.stackedContents.clear();
        this.mc.player.inventory.fillStackedContents(this.stackedContents, false);
        this.craftingSlots.fillStackedContents(this.stackedContents);
        this.updateCollections(false, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
    }

    //Draws RecipeBook Window and tabs
    public void render(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 100.0F);
            this.mc.getTextureManager().bindTexture(RECIPE_BOOK);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.width - 147) / 2 - this.xOffset;
            int j = (this.height - 166) / 2;
            this.drawTexturedModalRect(i, j, 1, 1, 147, 166);
            this.searchBar.drawTextBox();
            RenderHelper.disableStandardItemLighting();

            //Tabs
            Iterator var6 = this.recipeTabs.iterator();
            while(var6.hasNext()) {
                net.minecraft.client.gui.recipebook.GuiButtonRecipeTab guiRecipeTabButton = (net.minecraft.client.gui.recipebook.GuiButtonRecipeTab)var6.next();
                guiRecipeTabButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
            }

            this.toggleRecipesBtn.drawButton(this.mc, mouseX, mouseY, partialTicks);
            this.scrambleBookPage.render(i, j, mouseX, mouseY, partialTicks);
            GlStateManager.popMatrix();
        }

    }

    public void renderTooltip(int xIn, int yIn, int width, int height) {
        if (this.isVisible()) {
            this.scrambleBookPage.renderTooltip(width, height);
            if (this.toggleRecipesBtn.isMouseOver()) {
                String s1 = I18n.format(this.toggleRecipesBtn.isStateTriggered() ? "Craftable" : "All", new Object[0]);
                if (this.mc.currentScreen != null) {
                    this.mc.currentScreen.drawHoveringText(s1, width, height);
                }
            }

            this.renderGhostRecipeTooltip(xIn, yIn, width, height);
        }

    }

    private void renderGhostRecipeTooltip(int p_193015_1_, int p_193015_2_, int p_193015_3_, int p_193015_4_) {
        ItemStack itemstack = null;

        for(int i = 0; i < this.ghostRecipe.size(); ++i) {
            GhostIngredient ghostIngredient = this.ghostRecipe.get(i);
            int j = ghostIngredient.getX() + p_193015_1_;
            int k = ghostIngredient.getY() + p_193015_2_;
            if (p_193015_3_ >= j && p_193015_4_ >= k && p_193015_3_ < j + 16 && p_193015_4_ < k + 16) {
                itemstack = ghostIngredient.getItem();
            }
        }

        if (itemstack != null && this.mc.currentScreen != null) {
            this.mc.currentScreen.drawHoveringText(this.mc.currentScreen.getItemToolTip(itemstack), p_193015_3_, p_193015_4_);
        }

    }

    public void renderGhostRecipe(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
        this.ghostRecipe.render(this.mc, p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
    }

    public boolean mouseClicked(int x, int y, int time) {
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (this.scrambleBookPage.mouseClicked(
                    x, y, time,
                    (this.width - 147) / 2 - this.xOffset,
                    (this.height - 166) / 2,
                    147, 166))
            {
                IRecipe irecipe = this.scrambleBookPage.getLastClickedRecipe();
                ScrambleSubRecipes recipelist = this.scrambleBookPage.getLastClickedRecipeList();
                if (irecipe != null && recipelist != null) {
                    if (!recipelist.isCraftable(irecipe) && this.ghostRecipe.getRecipe() == irecipe) {
                        return false;
                    }

                    this.ghostRecipe.clear();
                    this.mc.playerController.func_194338_a(this.mc.player.openContainer.windowId, irecipe, GuiScreen.isShiftKeyDown(), this.mc.player);
                    if (!this.isOffsetNextToMainGUI() && time == 0) {
                        this.setVisible(false);
                    }
                }

                return true;
            } else if (time != 0)
                return false;
            else if (this.searchBar.mouseClicked(x, y, time))
                return true;
            else if (this.toggleRecipesBtn.mousePressed(this.mc, x, y))
            {
                boolean flag = !this.recipeBook.isFilteringCraftable();
                this.recipeBook.setFilteringCraftable(flag);
                this.toggleRecipesBtn.setStateTriggered(flag);
                this.toggleRecipesBtn.playPressSound(this.mc.getSoundHandler());
                this.sendUpdateSettings();
                this.updateCollections(false, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
                return true;
            } else {
                Iterator var4 = this.recipeTabs.iterator();

                net.minecraft.client.gui.recipebook.GuiButtonRecipeTab guibuttonrecipetab;
                do {
                    if (!var4.hasNext()) {
                        return false;
                    }

                    guibuttonrecipetab = (net.minecraft.client.gui.recipebook.GuiButtonRecipeTab)var4.next();
                } while(!guibuttonrecipetab.mousePressed(this.mc, x, y));

                if (this.currentTab != guibuttonrecipetab) {
                    guibuttonrecipetab.playPressSound(this.mc.getSoundHandler());
                    this.currentTab.setStateTriggered(false);
                    this.currentTab = guibuttonrecipetab;
                    this.currentTab.setStateTriggered(true);
                    this.updateCollections(true, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean hasClickedOutside(int x1, int y1, int width, int height, int a, int b) {
        if (!this.isVisible()) {
            return true;
        } else {
            boolean flag = x1 < width ||
                    y1 < height ||
                    x1 >= width + a ||
                    y1 >= height + b;
            boolean flag1 = width - 147 < x1 &&
                    x1 < width &&
                    height < y1 &&
                    y1 < height + b;
            return flag && !flag1 && !this.currentTab.mousePressed(this.mc, x1, y1);
        }
    }

    public boolean keyPressed(char typedChar, int keycode) {
        if (this.isVisible() && !this.mc.player.isSpectator()) {
            if (keycode == 1 && !this.isOffsetNextToMainGUI()) {
                this.setVisible(false);
                return true;
            } else if(false) {
                if (GameSettings.isKeyDown(this.mc.gameSettings.keyBindChat) && !this.searchBar.isFocused())
                {
                    this.searchBar.setFocused(true);
                } else if (this.searchBar.textboxKeyTyped(typedChar, keycode)) {
                    String s1 = this.searchBar.getText().toLowerCase(Locale.ROOT);
                    this.pirateRecipe(s1);
                    if (!s1.equals(this.lastSearch)) {
                        this.updateCollections(false, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
                        this.lastSearch = s1;
                    }

                    return true;
                }

                return false;
            }
        } else {
            return false;
        }
        return false;
    }

    private void pirateRecipe(String text) {
        if ("excitedze".equals(text)) {
            LanguageManager languagemanager = this.mc.getLanguageManager();
            Language language = languagemanager.getLanguage("en_pt");
            if (languagemanager.getCurrentLanguage().compareTo(language) == 0) {
                return;
            }

            languagemanager.setCurrentLanguage(language);
            this.mc.gameSettings.language = language.getLanguageCode();
            FMLClientHandler.instance().refreshResources(new IResourceType[]{VanillaResourceType.LANGUAGES});
            this.mc.fontRenderer.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.mc.gameSettings.forceUnicodeFont);
            this.mc.fontRenderer.setBidiFlag(languagemanager.isCurrentLanguageBidirectional());
            this.mc.gameSettings.saveOptions();
        }

    }

    private boolean isOffsetNextToMainGUI() {
        return this.xOffset == 86;
    }

    public void recipesUpdated() {
        this.updateTabs();
        if (this.isVisible()) {
            this.updateCollections(false, ModBlocks.SCRAMBLE_BENCH.getRegistryName());
        }

    }

    public void recipesShown(List<IRecipe> recipes) {
        Iterator var2 = recipes.iterator();

        while(var2.hasNext()) {
            IRecipe irecipe = (IRecipe)var2.next();
            try {
                this.mc.player.removeRecipeHighlight(irecipe);
            }catch (Exception e)
            {

            }
        }

    }

    public void setupGhostRecipe(IRecipe iRecipe, List<Slot> slots) {
        ItemStack itemstack = iRecipe.getRecipeOutput();
        this.ghostRecipe.setRecipe(iRecipe);
        this.ghostRecipe.addIngredient(Ingredient.fromStacks(new ItemStack[]{itemstack}), (slots.get(0)).xPos, (slots.get(0)).yPos);
        int i = this.craftingSlots.getWidth();
        int j = this.craftingSlots.getHeight();
        int k = iRecipe instanceof IShapedRecipe ? ((IShapedRecipe)iRecipe).getRecipeWidth() : i;
        int l = 1;
        Iterator<Ingredient> iterator = iRecipe.getIngredients().iterator();

        for(int i1 = 0; i1 < j; ++i1) {
            for(int j1 = 0; j1 < k; ++j1) {
                if (!iterator.hasNext()) {
                    return;
                }

                Ingredient ingredient = (Ingredient)iterator.next();
                if (ingredient.getMatchingStacks().length > 0) {
                    Slot slot = (Slot)slots.get(l);
                    this.ghostRecipe.addIngredient(ingredient, slot.xPos, slot.yPos);
                }

                ++l;
            }

            if (k < i) {
                l += i - k;
            }
        }

    }

    private void sendUpdateSettings() {
        if (this.mc.getConnection() != null) {
            this.mc.getConnection().sendPacket(new CPacketRecipeInfo(this.isVisible(), this.recipeBook.isFilteringCraftable()));
        }

    }
}
