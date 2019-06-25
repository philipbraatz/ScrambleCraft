package com.doorfail.scramblecraft.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Recipe
{
    private ItemStack air = new ItemStack(Items.AIR);

    public ItemStack tl;
    public ItemStack tm;
    public ItemStack tr;
    public ItemStack cl;
    public ItemStack cm;
    public ItemStack cr;
    public ItemStack bl;
    public ItemStack bm;
    public ItemStack br;
    public ItemStack result;

    public RecipeType shape =RecipeType.custom;

    public Recipe(Item fillIn,Item extra, RecipeType type, ItemStack output )
    {
        shape =type;
        switch (shape)
        {
            case full:
                tl =new ItemStack(fillIn);
                tm = new ItemStack(fillIn);
                tr =new ItemStack(fillIn);
                cl =new ItemStack(fillIn);
                cm=new ItemStack(fillIn);
                cr=new ItemStack(fillIn);
                bl=new ItemStack(fillIn);
                bm=new ItemStack(fillIn);
                br=new ItemStack(fillIn);
                result =output;
            case slab:
                tl =new ItemStack(Items.AIR);
                tm = new ItemStack(Items.AIR);
                tr =new ItemStack(Items.AIR);
                cl =new ItemStack(Items.AIR);
                cm=new ItemStack(Items.AIR);
                cr=new ItemStack(Items.AIR);
                bl=new ItemStack(fillIn);
                bm=new ItemStack(fillIn);
                br=new ItemStack(fillIn);
                result =output;
            case cross:
                tl =new ItemStack(fillIn);
                tm = new ItemStack(extra);
                tr =new ItemStack(fillIn);
                cl =new ItemStack(extra);
                cm=new ItemStack(fillIn);
                cr=new ItemStack(extra);
                bl=new ItemStack(fillIn);
                bm=new ItemStack(extra);
                br=new ItemStack(fillIn);
                result =output;
            case donut:
                tl =new ItemStack(fillIn);
                tm = new ItemStack(fillIn);
                tr =new ItemStack(fillIn);
                cl =new ItemStack(fillIn);
                cm=new ItemStack(extra);
                cr=new ItemStack(fillIn);
                bl=new ItemStack(fillIn);
                bm=new ItemStack(fillIn);
                br=new ItemStack(fillIn);
                result =output;
            case single:
                tl =new ItemStack(fillIn);
                tm = new ItemStack(Items.AIR);
                tr =new ItemStack(Items.AIR);
                cl =new ItemStack(Items.AIR);
                cm=new ItemStack(Items.AIR);
                cr=new ItemStack(Items.AIR);
                bl=new ItemStack(Items.AIR);
                bm=new ItemStack(Items.AIR);
                br=new ItemStack(Items.AIR);
                result =output;
            case shapeless:
                tl =new ItemStack(fillIn);
                tm = new ItemStack(extra);
                tr =new ItemStack(Items.AIR);
                cl =new ItemStack(Items.AIR);
                cm=new ItemStack(Items.AIR);
                cr=new ItemStack(Items.AIR);
                bl=new ItemStack(Items.AIR);
                bm=new ItemStack(Items.AIR);
                br=new ItemStack(Items.AIR);
                result =output;
        }

    }

    public Recipe(Item topLeft, Item top, Item topRight,
                  Item middleLeft, Item middle, Item middleRight,
                  Item bottomLeft, Item bottom, Item bottomRight
            , ItemStack output)
    {
        tl =new ItemStack(topLeft);
        tm = new ItemStack(top);
        tr =new ItemStack(topRight);
        cl =new ItemStack(middleLeft);
        cm=new ItemStack(middle);
        cr=new ItemStack(middleRight);
        bl=new ItemStack(bottomLeft);
        bm=new ItemStack(bottom);
        br=new ItemStack(bottomRight);
        result =output;

        shift();
    }
    public Recipe(ItemStack topLeft, ItemStack top, ItemStack topRight,
                       ItemStack middleLeft, ItemStack middle, ItemStack middleRight,
                       ItemStack bottomLeft, ItemStack bottom, ItemStack bottomRight
            , ItemStack output)
    {
        shape =RecipeType.custom;

         tl =topLeft;
         tm = top;
         tr =topRight;
         cl =middleLeft;
         cm=middle;
         cr=middleRight;
         bl=bottomLeft;
         bm=bottom;
         br=bottomRight;
        result =output;

        shift();
    }

    public void shift() {

        if (shape != RecipeType.shapeless) {
            //if not full
            if (tl == air || tm == air || tr == air ||
                    cl == air || cm == air || cr == air ||
                    bl == air || bm == air || br == air
            ) {
                //horizontal
                boolean topEmpty = tl == air && tm == air && tr == air;
                boolean midEmpty = cl == air && cm == air && cr == air;
                boolean botEmpty = bl == air && bm == air && br == air;

                //vertical
                boolean leftEmtpy = tl == air && cl == air && bl == air;
                boolean middleEmtpy = tm == air && cm == air && bm == air;
                boolean rightEmpty = tr == air && cr == air && br == air;

                boolean centerEmpty = cm == air;

                if (topEmpty) {
                    //pushes holes to the bottom
                    tl = cl;
                    tm = cm;
                    tr = cr;

                    cl = bl;
                    cm = bm;
                    cr = br;

                    bl = air;
                    bm = air;
                    br = air;

                    shift();
                }
                if (leftEmtpy) {
                    //pushes holes to right
                    tl = tm;
                    cl = cm;
                    bl = bm;

                    tm = tr;
                    cm = cr;
                    bm = br;

                    tr = air;
                    cr = air;
                    br = air;

                    shift();
                }

                if (tl != air)
                    shape = RecipeType.single;
                else if (midEmpty && botEmpty &&
                        !leftEmtpy && !middleEmtpy && !rightEmpty
                )
                    shape = RecipeType.slab;
                else if (centerEmpty &&
                        !topEmpty && !botEmpty && !leftEmtpy & !rightEmpty)
                    if (tl == air && tr == air &&
                            bl == air && br == air)
                        shape = RecipeType.cross;
                    else
                        shape = RecipeType.donut;
            } else if( tl ==tm && tl == tr &&
                    tl ==cl && tl == cm && tl ==cr &&
                    tl == bl && tl == bm && tl == br
            )
                shape = RecipeType.full;
            else
                shape = RecipeType.custom;
        }else
        {
            //pushes all holes to the end
            if(tl == air)
                tl = tm;
            if(tm ==air)
                tm = tr;
            if( tr == air)
                tr =cl;
            if(cl ==air)
                cl = cm;
            if(cm == air)
                cm =cr;
            if(cr ==air)
                cr =bl;
            if(bl ==air)
                bl =bm;
            if(bm == air)
                bm = br;

            shift();
        }
    }



    public boolean isEmpty() {
        if(tl ==air && tm ==air && tr ==air && cl ==air && cm ==air && cr ==air && bl ==air && bm ==air && br ==air)
            return true;
        else
            return false;
    }
}
