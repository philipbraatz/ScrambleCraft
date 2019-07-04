package com.doorfail.scramblecraft.recipe;

import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.init.ModItems;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.ForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.doorfail.scramblecraft.util.Reference.MODID;

//Register all recipes that are added by the plugin here per player
public class ModRecipeRegistry {
    private static Logger logger = LogManager.getLogger(MODID);

    private static Map<UUID, List<ModRecipe>> playerRecipeList = new HashMap<>();//playerID,recipes
    public static ModCraftingManager craftingManager = new ModCraftingManager();

    private static int newIndex =-1;
    private static int oldIndex =-1;
    public static int previousStackSize =222;//big stack number

    private static boolean isPlayerInRegistry(UUID playerId, boolean warn) {
        if (isPlayerInRegistry(playerId))
            return true;
        else
        {
            if(warn)
                logger.warn("Player with ID:"+playerId.toString()+" does not exist");
            return false;
        }

    }
    private static boolean isPlayerInRegistry(UUID playerId)
    {
        List<ModRecipe> recipes;
        if(playerRecipeList.size() == 0)
            return false;//empty list
        else
            return playerRecipeList.get(playerId) != null;//player not in list
    }

    private static boolean isPlayerMachineInRegistry(UUID playerId,ResourceLocation craftingMachine)
    {
        if(!isPlayerInRegistry(playerId))
            return false;
        else {
            for (ModRecipe r : playerRecipeList.get(playerId)) {
                if (r.craftingMachine == craftingMachine)
                    return true;
            }
            return false;
        }
    }
    public static void addRecipe(int index,UUID playerId,ModRecipe recipe)
    {
        List<ModRecipe> recipes =new ArrayList<>();
        if(isPlayerInRegistry(playerId)) {
            recipes = playerRecipeList.get(playerId);//old
        }
        recipes.add(index, recipe);//+ new
        playerRecipeList.replace(playerId, recipes);//update


    }
    public static void addRecipe(UUID playerId,Block input, ItemStack output, ResourceLocation craftingMachine)
    {
        addRecipe(playerId,Item.getItemFromBlock(input), output, craftingMachine);
    }

    //Item IN
    public static void addRecipe(UUID playerId,Item input, ItemStack output, ResourceLocation craftingMachine)
    {
        addRecipe(playerId,new ItemStack(input, 1, OreDictionary.WILDCARD_VALUE), output, craftingMachine);
    }

    //Stacks
    public static void addRecipe(UUID playerId,ItemStack input, ItemStack output, ResourceLocation craftingMachine)
    {
        List<ItemStack> ingredients = new ArrayList<>();
        List<ItemStack> results = new ArrayList<>();
        ingredients.add(input);
        results.add(output);
        addRecipe( playerId,ingredients,results,craftingMachine);
    }

    //IN List
    public static void addRecipe(UUID playerId,List<ItemStack> input, ItemStack output, ResourceLocation craftingMachine)
    {
        List<ItemStack> results = new ArrayList<>();
        results.add(output);
        addRecipe( playerId,input,results,craftingMachine);
    }
    //OUT List
    public static void addRecipe(UUID playerId,ItemStack input, List<ItemStack> output, ResourceLocation craftingMachine)
    {
        List<ItemStack> ingredients = new ArrayList<>();
        ingredients.add(input);
        addRecipe( playerId,ingredients,output,craftingMachine);
    }

    //Lists
    public static void addRecipe(UUID playerId,List<ItemStack> input, List<ItemStack> output,ResourceLocation craftingMachine)
    {
        for (ItemStack it:output)
        {
            if(it == ItemStack.EMPTY)
            {
                net.minecraftforge.fml.common.FMLLog.log.info("Ignored {} recipe with empty output: {}", craftingMachine.toString(), output); return;
            }
        }
        if (checkResult(input,playerId,craftingMachine) !=new ArrayList<ItemStack>())
        {
            net.minecraftforge.fml.common.FMLLog.log.info("Ignored {} recipe with conflicting input: {} = {}",craftingMachine.toString(), input, output); return;
        }

        List<ModRecipe> recipes = new ArrayList<>();
        if(isPlayerInRegistry(playerId,true)) {
            recipes = playerRecipeList.get(playerId);//old
        }
        recipes.add(new ModRecipe(craftingMachine, input, output, true));//+new
        playerRecipeList.put(playerId, recipes);//update
    }

    public static void addDefaultRecipe(UUID playerId,ItemStack input, ItemStack output,ResourceLocation craftingMachine) {
        List<ItemStack> in = new ArrayList<>();
        List<ItemStack> out = new ArrayList<>();
        in.add(input);
        out.add(output);
        addDefaultRecipe(playerId,in,out,craftingMachine);
    }
    //only added if it doesnt already exist
    public static void addDefaultRecipe(UUID playerId,List<ItemStack> input, List<ItemStack> output,ResourceLocation craftingMachine)
    {
        if(checkResult(input,playerId,craftingMachine).size() ==0)//new recipe
        {
            List<ModRecipe> recipes = new ArrayList<>();
            if(isPlayerInRegistry(playerId,true)) {
                recipes = playerRecipeList.get(playerId);//old
            }

            recipes.add(new ModRecipe(craftingMachine, input, output, true));
            playerRecipeList.put(playerId, recipes);
        }
    }

    public static List<ItemStack> checkResult(List<ItemStack> inputs, UUID playerId, ResourceLocation craftingMachine)
    {
        List<ModRecipe> recipes =new ArrayList<>();
        if(isPlayerInRegistry(playerId,false)) {
            recipes = playerRecipeList.get(playerId);
            for (int i = 0; i < recipes.size(); i++)
                if (craftingMachine == recipes.get(i).craftingMachine)//correct machine
                    if (areRecipesSame(inputs, recipes.get(i).getInputItemStacks()))//correct recipe items
                        return recipes.get(i).checkResult();
        }
        else
            return new ArrayList<>();//failed
        return new ArrayList<>();
    }
    public static List<ItemStack> craftResult(List<ItemStack> inputs, UUID playerId, ResourceLocation craftingMachine)
    {
        if(isPlayerInRegistry(playerId,false)) {
            List<ModRecipe> recipes = playerRecipeList.get(playerId);

            for (int i = 0; i < recipes.size(); i++)
                if (craftingMachine == recipes.get(i).craftingMachine)//correct machine
                    if (areRecipesSame(inputs, recipes.get(i).getInputItemStacks()))//correct recipe items
                        return recipes.get(i).craftItem();
        }
        return new ArrayList<>();//failed
    }

    private static boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        if(stack2.getItem() == stack1.getItem() &&
                (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata()))
            return true;
        else
        {
            logger.info(stack1.getItem().getRegistryName() +"!="+stack2.getItem().getRegistryName() );
            return false;
        }
    }
    public static boolean areRecipesSame(List<ItemStack> stack1, List<ItemStack> stack2)
    {
        if(stack1.size() != stack2.size())
            return false;

        for (int i = 0; i < stack1.size(); i++) {
            if (!compareItemStacks(stack1.get(i), stack2.get(i)))
                return false;//item doesn't match
        }

        return true;
    }

    public static List<ModRecipe> getRecipeList(UUID playerId,ResourceLocation craftingMachine)
    {
        List<ModRecipe> recipes =new ArrayList<>();
        if(isPlayerInRegistry(playerId,true))
            recipes = playerRecipeList.get(playerId);

        if(recipes.size() >0) {
            List<ModRecipe> filter = new ArrayList<>();
            for (ModRecipe recipe : recipes
            ) {
                if (craftingMachine == recipe.craftingMachine)
                    filter.add(recipe);
            }
            return filter;
        }
        else
            return recipes;
    }
    public static ModRecipe getRecipe(UUID playerId,ResourceLocation craftingMachine,List<ItemStack> ingredients)
    {
        if(isPlayerMachineInRegistry(playerId,craftingMachine)) {
            int index = getMatchIndex(playerId, craftingMachine, ingredients);
            if(index >-1)
                return playerRecipeList.get(playerId).get(index);
            //else
            //    logger.warn("Player {}: Could not find Recipe. Output will be empty", playerId.toString());
        }
        return ModRecipe.EMTPY_INPUT(craftingMachine,ingredients);//give Recipe Shell
    }

    public static int getMatchIndex(UUID playerId,ResourceLocation craftingMachine,List<ItemStack> ingredients)
    {
        if(!isPlayerMachineInRegistry(playerId,craftingMachine))
            return -1;

        for (int i = 0; i < playerRecipeList.size() ; i++) {
            List<ResourceLocation> ingItems = new ArrayList<>();
            List<ResourceLocation> sourceItems = new ArrayList<>();

            for (ItemStack itemStack : ingredients)
                ingItems.add(itemStack.getItem().getRegistryName());

            //playerRecipeList.values();

            for (ModRecipe recipe : playerRecipeList.get(playerId))
                for (ItemStack it: recipe.getInputItemStacks())
                    sourceItems.add(it.getItem().getRegistryName());


            if (ingItems.size() == sourceItems.size()) //possible match
            {
                boolean match =true;
                for (int j = 0; j < ingItems.size(); j++)
                {
                    if (ingItems.get(j) != sourceItems.get(j))//ingredient[i] is not the same
                        match =false;//fail item
                }
                if(match)
                    return i;
            }
        }
        //new Item
        return -1;
    }





    public static NonNullList<Ingredient> getItemsAsIngredient(List<Item> ingredients)
    {
        NonNullList<Ingredient> converted =NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);
        int i =0;
        for (Item it:ingredients) {
            converted.set(i,Ingredient.fromItem(it));
            i++;
        }
        return converted;
    }

    public static List<Item> getIngredientAsItems(NonNullList<Ingredient> recipeItems) {
        List<Item> ingredients =new ArrayList<>();
        for (Ingredient it:recipeItems)
            if(it.getMatchingStacks().length >0)
                ingredients.add(it.getMatchingStacks()[0].getItem());//add recipe as a new itemstack from item
            else
                ingredients.add(Items.AIR);
        return ingredients;
    }
    public static List<ItemStack> getIngredientAsItemStacks(NonNullList<Ingredient> recipeItems) {
        List<ItemStack> ingredients =new ArrayList<>();
        for (Ingredient it:recipeItems)
            if(it.getMatchingStacks().length >0)
                ingredients.add(new ItemStack( it.getMatchingStacks()[0].getItem()));//add recipe as a new itemstack from item
            else
                ingredients.add(ItemStack.EMPTY);
        return ingredients;
    }

    public static void init() {
        craftingManager.init();
        //removeRecipes(Blocks.CRAFTING_TABLE);

        ForgeRegistry<IRecipe> recipeRegistry = (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
        ArrayList<IRecipe> recipes = Lists.newArrayList(recipeRegistry.getValuesCollection());
        for (IRecipe r : recipes)
        {
            ItemStack output = r.getRecipeOutput();
            if (output.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
            {
                recipeRegistry.remove(r.getRegistryName());
                recipeRegistry.register(DummyRecipe.from(r));
            }
        }

        //logger.info("Removed recipe for crafting table");
        GameRegistry.addSmelting(ModItems.RUBY, new ItemStack(ModBlocks.RUBY_BLOCK, 1), 1.5f);
        GameRegistry.addSmelting(ModBlocks.RUBY_BLOCK, new ItemStack(Blocks.DIAMOND_BLOCK, 2), 3.0f);
        GameRegistry.addSmelting(new ItemStack(Items.STICK), new ItemStack(Items.STRING,3),3);
        GameRegistry.addSmelting(new ItemStack(Blocks.OBSIDIAN), new ItemStack(ModItems.OBSIDIAN_INGOT), 0.4F);

        //ScrambleFurnaceRecipes.instance().addRecipe(ModBlocks.RUBY_ORE, new ItemStack(ModItems.RUBY), 0.3f);

    }

    public static List<ItemStack> tryToScramble(UUID playerId, ResourceLocation craftingBlock, InventoryCrafting inventoryCrafting, List<ItemStack> ingredient, boolean search)
    {
        List<ItemStack> newResultStack;
        boolean isSame;
        int overflow = 15;//number of times to try till failure
        int counter = 0;

        List<ModRecipe> recipeList =getRecipeList(playerId,craftingBlock);

        //loop until unique or till failure
        do {
            isSame = false;

            counter++;
            if(search)
                newIndex = new Random().nextInt(recipeList.size());//try to swap
            if (newIndex == oldIndex)
                isSame = true;

            newResultStack = recipeList.get(newIndex).checkResult();
            for (ItemStack itemStack:newResultStack
            )
                for (ItemStack it : ingredient) {
                    //old == new
                    if (itemStack.getItem().getRegistryName() == it.getItem().getRegistryName() ||//the same recipe
                            itemStack == new ItemStack(Items.AIR)//EMPTY!
                    )
                        isSame = true;
                }
        } while (isSame && counter < overflow);

        if(!isSame) {
            //compare every old Item with every new Item
            for (ItemStack itNew: newResultStack)
                for (ItemStack itOld : recipeList.get(oldIndex).checkResult())
                    if (ModCraftingManager.areItemsRelated(inventoryCrafting, itNew.getItem(), itOld.getItem()))
                        return tryToScramble(playerId, craftingBlock, inventoryCrafting, ingredient, true);
                    else
                        return newResultStack;
        }
        else
            return new ArrayList<>();//overflowed
        return new ArrayList<>();//Why does java want this
    }

    public static void randomizeRecipe(IInventory craftGrid, EntityPlayer entityPlayer, ResourceLocation craftingBlock, List<ItemStack> ingredient)
    {
        //dont scramble the first 2 recipes created
        if(getRecipeList(entityPlayer.getUniqueID(),craftingBlock).size() >2)
        {
            oldIndex=ModRecipeRegistry.getMatchIndex(entityPlayer.getUniqueID(),craftingBlock,ingredient);
            List<ModRecipe> recipes = ModRecipeRegistry.getRecipeList(entityPlayer.getUniqueID(),craftingBlock);
            if(
                    oldIndex != -1 &&
                            recipes.get(oldIndex).IsReady()
            ) {
                List<ItemStack> newResultStack = tryToScramble(entityPlayer.getUniqueID(),craftingBlock,(InventoryCrafting) craftGrid,ingredient,true);
                List<ItemStack> oldResultStack = recipes.get(oldIndex).checkResult();

                //get Ingredients for recipes that use the output item
                if (newResultStack !=new ArrayList<ItemStack>()) {
                    recipes.get(newIndex).setNewOutput(newResultStack);//current index -> change Item
                    recipes.get(oldIndex).setNewOutput(oldResultStack);//changed index -> current item
                    //logger.info("{} swapped with {}",oldIngredient.toString(), newIngredient.toString());
                }
                ModCraftingManager.updateRecipe(entityPlayer, craftingBlock, recipes.get(oldIndex), oldIndex);
                ModCraftingManager.updateRecipe(entityPlayer, craftingBlock, recipes.get(newIndex), newIndex);
            }
        }
    }

    public static boolean recipeExists(UUID playerId, ResourceLocation craftingBlock,List<ItemStack> ingredients)
    {
        List<ModRecipe> recipeList =ModRecipeRegistry.getRecipeList(playerId,craftingBlock);
        for (ModRecipe check:recipeList) {
            List<ItemStack> checkStack = check.getInputItemStacks();
            if (ingredients.size() == checkStack.size()) {
                boolean match = true;
                for (int i = 0; i < checkStack.size(); i++) {
                    if (ingredients.get(i) != checkStack.get(i)) {
                        match = false;//recipe not the same
                        break;
                    }
                }
                if (match)
                    return true;//match
            }
        }
        return false;//no matching sizes
    }
}
