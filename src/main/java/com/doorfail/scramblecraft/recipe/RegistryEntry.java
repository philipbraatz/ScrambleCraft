package com.doorfail.scramblecraft.recipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;

import java.util.List;
import java.util.UUID;


//Struct
public class RegistryEntry {
    public UUID id =null;
    public ResourceLocation location=null;
    public ModRecipe recipe;

    public RegistryEntry(ResourceLocation loc)
    {
        location=loc;
        recipe =ModRecipe.EMPTY(location);
    }
    public RegistryEntry(UUID id, ResourceLocation location)
    {
        this.id =id;
        this.location = location;
        recipe = ModRecipe.EMPTY(location);
    }
    public RegistryEntry(UUID id, ResourceLocation location,ModRecipe recipe)
    {
        this.id =id;
        this.location = location;
        this.recipe = recipe;
    }
    public RegistryEntry(UUID id, ResourceLocation location, IRecipe recipe,int width,int height)
    {
        this.id =id;
        this.location = location;
        this.recipe = new ModRecipe(location,recipe.getIngredients(),recipe.getRecipeOutput(),width,height);
    }
    public RegistryEntry(UUID id, ResourceLocation location, List<Ingredient> recipe,int width,int height)
    {
        this.id =id;
        this.location = location;
        this.recipe = ModRecipe.EMTPY_INPUT(location,recipe,width,height);
    }

    public void insert(boolean warn)
    {
        ModRecipeRegistry.addRecipe(this.id,this.location,this.recipe.getIngredients(),recipe.checkResult(),recipe.getRecipeWidth(),recipe.getRecipeHeight(),warn);
    }
    public void update()
    {
        ModRecipeRegistry.updateModRecipe(this.id,this.location,this.recipe.getIngredients());
    }
    //public void delete()
    //{
        //ModRecipeRegistry.d
    //}
    public ModRecipe load()
    {
        return ModRecipeRegistry.getMatchingModRecipe(this.id,this.location,this.recipe);
    }
}
