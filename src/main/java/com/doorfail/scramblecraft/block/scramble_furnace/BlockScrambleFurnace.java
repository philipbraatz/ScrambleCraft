package com.doorfail.scramblecraft.block.scramble_furnace;

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
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockScrambleFurnace extends BlockContainer {
        //public static final PropertyDirection MY_FACING = BlockHorizontal.FACING;
        private final boolean isBurning;
        private static boolean keepInventory;

        public BlockScrambleFurnace(String name, boolean isBurning) {
            super(Material.ROCK);
            setTranslationKey(name);
            setRegistryName(name);
            setHardness(5.0f);
            setHarvestLevel("pickaxe", 2);
            setResistance(20.0f);
            setSoundType(SoundType.STONE);
            this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH));
            this.isBurning = isBurning;
            this.setCreativeTab(CreativeTabs.DECORATIONS);

            ModBlocks.BLOCKS.add(this);
            ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
            addFurnaceRecipes();
            playerIn.openGui(ScrambleCraft.instance, Reference.GUI_SCRAMBLE_FURNACE, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }

        public void addFurnaceRecipes() {
            for (Map.Entry entry:FurnaceRecipes.instance().getSmeltingList().entrySet()
                 ) {
                List<ItemStack> inputs = new ArrayList<>();
                inputs.add((ItemStack) entry.getKey());
                ModRecipeRegistry.addDefaultRecipe(
                        Minecraft.getMinecraft().player.getUniqueID(),
                        ModBlocks.SCRAMBLE_FURNACE_OFF.getRegistryName(),
                        (ItemStack) entry.getValue(),Ingredient.fromStacks((ItemStack) entry.getKey()),
                        1, 1);
            }
        }

        @Override
        public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
            this.setDefaultFacing(worldIn, pos, state);
        }

        private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
            if (!worldIn.isRemote) {
                IBlockState iblockstate = worldIn.getBlockState(pos.north());
                IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
                IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
                IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
                EnumFacing enumfacing = (EnumFacing) state.getValue(BlockHorizontal.FACING);

                if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock()) {
                    enumfacing = EnumFacing.SOUTH;
                } else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock()) {
                    enumfacing = EnumFacing.NORTH;
                } else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock()) {
                    enumfacing = EnumFacing.EAST;
                } else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock()) {
                    enumfacing = EnumFacing.WEST;
                }

                worldIn.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, enumfacing), 2);
            }
        }

        public static void setState(boolean active, World worldIn, BlockPos pos) {
            IBlockState iblockstate = worldIn.getBlockState(pos);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            keepInventory = true;

            if (active) {
                worldIn.setBlockState(pos, ModBlocks.SCRAMBLE_FURNACE_ON.getDefaultState().withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING)), 3);
                worldIn.setBlockState(pos, ModBlocks.SCRAMBLE_FURNACE_ON.getDefaultState().withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING)), 3);
            } else {
                worldIn.setBlockState(pos, ModBlocks.SCRAMBLE_FURNACE_OFF.getDefaultState().withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING)), 3);
                worldIn.setBlockState(pos, ModBlocks.SCRAMBLE_FURNACE_OFF.getDefaultState().withProperty(BlockHorizontal.FACING, iblockstate.getValue(BlockHorizontal.FACING)), 3);
            }

            keepInventory = false;

            if (tileentity != null) {
                tileentity.validate();
                worldIn.setTileEntity(pos, tileentity);
            }
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta) {
            return new TileEntityScrambleFurnace();
        }

        @Override
        public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
            return this.getDefaultState().withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite());
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
            worldIn.setBlockState(pos, state.withProperty(BlockHorizontal.FACING, placer.getHorizontalFacing().getOpposite()));
            if (stack.hasDisplayName()) {
                TileEntity tileentity = worldIn.getTileEntity(pos);
                if (tileentity instanceof TileEntityScrambleFurnace) {
                    ((TileEntityScrambleFurnace) tileentity).setCustomInventoryName(stack.getDisplayName());
                }
            }
        }

        @Override
        public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
            if (!keepInventory) {
                TileEntityScrambleFurnace tileentity = (TileEntityScrambleFurnace) worldIn.getTileEntity(pos);
                InventoryHelper.dropInventoryItems(worldIn, pos, tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
                super.breakBlock(worldIn, pos, state);
            }
        }

        @Override
        public boolean hasComparatorInputOverride(IBlockState state) {
            return true;
        }

        @Override
        public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
            return Container.calcRedstone(worldIn.getTileEntity(pos));
        }

        @Override
        public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
            return new ItemStack(ModBlocks.SCRAMBLE_FURNACE_OFF);
        }

        @Override
        public EnumBlockRenderType getRenderType(IBlockState state) {
            return EnumBlockRenderType.MODEL;
        }

        @Override
        public IBlockState getStateFromMeta(int meta) {
            EnumFacing enumfacing = EnumFacing.byIndex(meta);
            if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
                enumfacing = EnumFacing.NORTH;
            }
            return this.getDefaultState().withProperty(BlockHorizontal.FACING, enumfacing);
        }

        @Override
        public int getMetaFromState(IBlockState state) {
            return ((EnumFacing) state.getValue(BlockHorizontal.FACING)).getIndex();
        }

        @Override
        public IBlockState withRotation(IBlockState state, Rotation rot) {
            return state.withProperty(BlockHorizontal.FACING, rot.rotate((EnumFacing) state.getValue(BlockHorizontal.FACING)));
        }

        @Override
        public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
            return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(BlockHorizontal.FACING)));
        }

        @Override
        protected BlockStateContainer createBlockState() {
            return new BlockStateContainer(this, new IProperty[]{BlockHorizontal.FACING});
        }
    }