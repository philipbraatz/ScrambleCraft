package com.doorfail.scramblecraft.block.scramble_bench;

import com.doorfail.scramblecraft.ScrambleCraft;
import com.doorfail.scramblecraft.init.ModBlocks;
import com.doorfail.scramblecraft.init.ModItems;
import com.doorfail.scramblecraft.recipe.ModRecipeRegistry;
import com.doorfail.scramblecraft.util.Reference;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class BlockScrambleBench extends BlockContainer
{
    private static Logger logger = LogManager.getLogger(Reference.MODID);
    private static final PropertyDirection FACING = BlockHorizontal.FACING;

    private static List<UUID> playersLoaded = new ArrayList<>();

    public BlockScrambleBench(String name)
    {

        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setTranslationKey(name);
        setRegistryName(name);
        setHardness(2.0f);
        setHarvestLevel("axe", 0);
        setResistance(20.0f);

        this.setDefaultState(this.getBlockState().getBaseState().withProperty(FACING, EnumFacing.NORTH));

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));

        this.setCreativeTab(CreativeTabs.DECORATIONS);

        //ScrambleCraft.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityScrambleBench();
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }


    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        addCraftingTableRecipes();
        playerIn.openGui(ScrambleCraft.instance, Reference.GUI_SCRAMBLE_BENCH, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    public void addCraftingTableRecipes() {
        UUID player =Minecraft.getMinecraft().player.getUniqueID();
        if(!playersLoaded.contains(player)) {//only needs to be done once per server session
            Iterator craftingRecipeIter = CraftingManager.REGISTRY.iterator();
            logger.info("Replacing all recipes");
            logger.info("Expect log spam from FML!");

            IShapedRecipe entryShaped;
            IRecipe entry;
            Object temp=null;
            do {
                try {
                    temp = craftingRecipeIter.next();
                    try {
                        entryShaped = (IShapedRecipe) temp;

                        ModRecipeRegistry.addDefaultRecipe(
                                player,
                                entryShaped.getIngredients(), entryShaped.getRecipeOutput(),
                                ModBlocks.SCRAMBLE_BENCH.getRegistryName(), entryShaped.getRecipeWidth(), entryShaped.getRecipeHeight());

                        playersLoaded.add(player);

                    } catch (Exception e) {
                        entry = (IRecipe) temp;
                        ModRecipeRegistry.addDefaultRecipe(
                                Minecraft.getMinecraft().player.getUniqueID(),
                                entry.getIngredients(), entry.getRecipeOutput(),
                                ModBlocks.SCRAMBLE_BENCH.getRegistryName(), 0, 0);
                    }


                } catch (Exception e) {
                    logger.info("Skipping Recipe: "+((IRecipe)temp).getRegistryName());//ect. armor/shield/banner recipes
                }


            } while (craftingRecipeIter.hasNext());
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (!worldIn.isRemote)
        {
            super.updateTick( worldIn,  pos,  state,  rand);
            //this.(worldIn, pos);
        }
    }


    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()));
        if (stack.hasDisplayName())
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityScrambleBench)
            {
                ((TileEntityScrambleBench)tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityScrambleBench)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityScrambleBench)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos);
    }

    //@Override
    //public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
    //    return new ItemStack(ModBlocks.SCRAMBLE_BENCH);
   //}

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
                enumfacing = EnumFacing.SOUTH;
            else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
                enumfacing = EnumFacing.NORTH;
            else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
                enumfacing = EnumFacing.EAST;
            else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
                enumfacing = EnumFacing.WEST;

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.byIndex(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING)).getIndex();
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate( state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withRotation(mirrorIn.toRotation( state.getValue(FACING)));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{FACING});
    }

}