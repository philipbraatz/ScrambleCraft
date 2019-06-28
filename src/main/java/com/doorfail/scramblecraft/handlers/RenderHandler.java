package com.doorfail.scramblecraft.handlers;

public class RenderHandler
{
    public static void registerCustomMeshesAndStates()
    {
        //ModelLoader.setCustomMeshDefinition(Item.getItemFromBlock(ModBlocks.WHITE_WATER_FLUID), new ItemMeshDefinition()
        //{
           // @Override
            //public ModelResourceLocation getModelLocation(ItemStack stack)
            //{
            //    return new ModelResourceLocation(Reference.MOD_ID + ":white_water", "fluid");
            //}
        //});

        //ModelLoader.setCustomStateMapper(ModBlocks.WHITE_WATER_FLUID, new StateMapperBase()
        //{
        //    @Override
        //    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
        //    {
        //        return new ModelResourceLocation(Reference.MOD_ID + ":white_water", "fluid");
        //    }
        //});
    }

    public static void registerEntityRenders()
    {
        //RenderingRegistry.registerEntityRenderingHandler(EntityTest.class, new IRenderFactory<EntityTest>()
        //{
        //    @Override
        //    public Render<? super EntityTest> createRenderFor(RenderManager manager)
       //     {
       //         return new RenderTest(manager);
        //    }
       // });

       // RenderingRegistry.registerEntityRenderingHandler(EntityDiamondArrow.class, new IRenderFactory<EntityDiamondArrow>()
       // {
        //    @Override
        //    public Render<? super EntityDiamondArrow> createRenderFor(RenderManager manager)
       //     {
       //         return new RenderDiamondArrow(manager);
       //     }
       // });
    }
}
