package com.doorfail.scramblecraft.plugin.jei.ingredient;

import com.google.common.collect.Multimap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.BlockEntityTag;
import net.minecraft.util.datafix.walkers.EntityTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DynamicItemStack {
    private ItemStack itemStack;
    private IRegistryDelegate<Item> delegate;
    private CapabilityDispatcher capabilities;

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public DynamicItemStack(ItemStack itemStackIn) {
        itemStack = itemStackIn;
    }
    public DynamicItemStack(Block blockIn) {
        this(blockIn, 1);
    }

    public DynamicItemStack(Block blockIn, int amount) {
        this(blockIn, amount, 0);
    }

    public DynamicItemStack(Block blockIn, int amount, int meta) {
        this(Item.getItemFromBlock(blockIn), amount, meta);
    }

    public DynamicItemStack(Item itemIn) {
        this(itemIn, 1);
    }

    public DynamicItemStack(Item itemIn, int amount) {
        this(itemIn, amount, 0);
    }

    public DynamicItemStack(Item itemIn, int amount, int meta) {
        this(itemIn, amount, meta, null);
    }

    public DynamicItemStack(Item itemIn, int amount, int meta, @Nullable NBTTagCompound capNBT) {
        itemStack = new ItemStack( itemIn, amount,  meta, capNBT);
    }

    public DynamicItemStack(NBTTagCompound compound) {
        itemStack = new ItemStack(compound);
    }

    public boolean isEmpty() {
        return itemStack.isEmpty();
    }

    public static void registerFixes(DataFixer fixer) {
        fixer.registerWalker(FixTypes.ITEM_INSTANCE, new BlockEntityTag());
        fixer.registerWalker(FixTypes.ITEM_INSTANCE, new EntityTag());
    }

    public DynamicItemStack splitStack(int amount) {
        return new DynamicItemStack( itemStack.splitStack(amount));
    }

    public Item getItem() {
        return itemStack.getItem();
    }

    public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
       return itemStack.onItemUse( playerIn,  worldIn,  pos,  hand,  side,  hitX,  hitY,  hitZ);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        return itemStack.onItemUseFirst(playerIn,  worldIn,  pos,  hand,  side,  hitX,  hitY,  hitZ);
    }

    public float getDestroySpeed(IBlockState blockIn) {
        return itemStack.getDestroySpeed(blockIn);
    }

    public DynamicItemStack onItemUseFinish(World worldIn, EntityLivingBase entityLiving) {
        return new DynamicItemStack( itemStack.getItem().onItemUseFinish(itemStack, worldIn, entityLiving));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return itemStack.writeToNBT(nbt);
    }

    public int getMaxStackSize() {
        return itemStack.getMaxStackSize();
    }

    public boolean isStackable() {
        return itemStack.isStackable();
    }

    public boolean isItemStackDamageable() {
        return itemStack.isItemStackDamageable();
    }

    public boolean getHasSubtypes() {
        return itemStack.getItem().getHasSubtypes();
    }

    public boolean isItemDamaged() {
        return itemStack.isItemStackDamageable() && itemStack.getItem().isDamaged(itemStack);
    }

    public int getItemDamage() {
        return itemStack.getItem().getDamage(itemStack);
    }

    public int getMetadata() {
        return itemStack.getItem().getMetadata(itemStack);
    }

    public void setItemDamage(int meta) {
        itemStack.getItem().setDamage(itemStack, meta);
    }

    public int getMaxDamage() {
        return itemStack.getItem().getMaxDamage(itemStack);
    }

    public boolean attemptDamageItem(int amount, Random rand, @Nullable EntityPlayerMP damager) {
        if (!itemStack.isItemStackDamageable()) {
            return false;
        } else {
            if (amount > 0) {
                int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, itemStack);
                int j = 0;

                for(int k = 0; i > 0 && k < amount; ++k) {
                    if (EnchantmentDurability.negateDamage(itemStack, i, rand)) {
                        ++j;
                    }
                }

                amount -= j;
                if (amount <= 0) {
                    return false;
                }
            }

            if (damager != null && amount != 0) {
                CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(damager, itemStack, itemStack.getItemDamage() + amount);
            }

            itemStack.setItemDamage(itemStack.getItemDamage() + amount);
            return itemStack.getItemDamage() > itemStack.getMaxDamage();
        }
    }

    public void damageItem(int amount, EntityLivingBase entityIn) {
        itemStack.damageItem(amount,entityIn);

    }

    public void hitEntity(EntityLivingBase entityIn, EntityPlayer playerIn) {
        boolean flag = itemStack.getItem().hitEntity(itemStack, entityIn, playerIn);
        if (flag) {
            playerIn.addStat(StatList.getObjectUseStats(itemStack.getItem()));
        }

    }

    public void onBlockDestroyed(World worldIn, IBlockState blockIn, BlockPos pos, EntityPlayer playerIn) {
        boolean flag = itemStack.getItem().onBlockDestroyed(itemStack, worldIn, blockIn, pos, playerIn);
        if (flag) {
            playerIn.addStat(StatList.getObjectUseStats(itemStack.getItem()));
        }

    }

    public boolean canHarvestBlock(IBlockState blockIn) {
        return itemStack.getItem().canHarvestBlock(blockIn, itemStack);
    }

    public boolean interactWithEntity(EntityPlayer playerIn, EntityLivingBase entityIn, EnumHand hand) {
        return itemStack.getItem().itemInteractionForEntity(itemStack, playerIn, entityIn, hand);
    }

    public DynamicItemStack copy() {
        DynamicItemStack DynamicItemStack = new DynamicItemStack(itemStack.getItem(), itemStack.getCount(), itemStack.getItemDamage());
        DynamicItemStack.setAnimationsToGo(itemStack.getAnimationsToGo());
        if (itemStack.getTagCompound() != null) {
            DynamicItemStack.setTagCompound(itemStack.getTagCompound().copy());
        }

        return DynamicItemStack;
    }

    public static boolean areDynamicItemStackTagsEqual(DynamicItemStack stackA, DynamicItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else if (!stackA.isEmpty() && !stackB.isEmpty()) {
            if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
                return false;
            } else {
                return (stackA.getTagCompound() == null || stackA.getTagCompound().equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
            }
        } else {
            return false;
        }
    }

    public static boolean areDynamicItemStacksEqual(DynamicItemStack stackA, DynamicItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else {
            return !stackA.isEmpty() && !stackB.isEmpty() ? stackA.isDynamicItemStackEqual(stackB) : false;
        }
    }

    private boolean isDynamicItemStackEqual(DynamicItemStack other) {
        if (itemStack.getCount() != other.getCount()) {
            return false;
        } else if (itemStack.getItem() != other.getItem()) {
            return false;
        } else if (itemStack.getItemDamage() != other.getItemDamage()) {
            return false;
        } else if (itemStack.getTagCompound() == null && other.getTagCompound() != null) {
            return false;
        } else {
            return (itemStack.getTagCompound()) == null || itemStack.getTagCompound().equals(other.getTagCompound()) && itemStack.areCapsCompatible( other.itemStack);
        }
    }

    public static boolean areItemsEqual(DynamicItemStack stackA, DynamicItemStack stackB) {
        if (stackA == stackB) {
            return true;
        } else {
            return !stackA.isEmpty() && !stackB.isEmpty() ? stackA.isItemEqual(stackB) : false;
        }
    }

    public static boolean areItemsEqualIgnoreDurability(DynamicItemStack stackA, DynamicItemStack stackB) {
        if (stackA == stackB) {
            return true;
        } else {
            return !stackA.isEmpty() && !stackB.isEmpty() ? stackA.isItemEqualIgnoreDurability(stackB) : false;
        }
    }

    public boolean isItemEqual(DynamicItemStack other) {
        return !other.isEmpty() && itemStack.getItem() == other.getItem() && itemStack.getItemDamage() == other.getItemDamage();
    }

    public boolean isItemEqualIgnoreDurability(DynamicItemStack stack) {
        if (!itemStack.isItemStackDamageable()) {
            return itemStack.isItemEqual(stack.itemStack);
        } else {
            return !stack.isEmpty() && itemStack.getItem() == stack.getItem();
        }
    }

    public String getTranslationKey() {
        return itemStack.getItem().getTranslationKey(itemStack);
    }

    public String toString() {
        return itemStack.getCount() + "x" + itemStack.getItem().getTranslationKey() + "@" + itemStack.getItemDamage();
    }

    public void updateAnimation(World worldIn, Entity entityIn, int inventorySlot, boolean isCurrentItem) {
        itemStack.updateAnimation(worldIn,entityIn,inventorySlot,isCurrentItem);
    }

    public void onCrafting(World worldIn, EntityPlayer playerIn, int amount) {
        playerIn.addStat(StatList.getCraftStats(itemStack.getItem()), amount);
        itemStack.getItem().onCreated(itemStack, worldIn, playerIn);
    }

    public int getMaxItemUseDuration() {
        return itemStack.getItem().getMaxItemUseDuration(itemStack);
    }

    public EnumAction getItemUseAction() {
        return itemStack.getItem().getItemUseAction(itemStack);
    }

    public void onPlayerStoppedUsing(World worldIn, EntityLivingBase entityLiving, int timeLeft) {
        itemStack.getItem().onPlayerStoppedUsing(itemStack, worldIn, entityLiving, timeLeft);
    }

    public boolean hasTagCompound() {
        return !itemStack.isEmpty() && itemStack.getTagCompound() != null;
    }

    @Nullable
    public NBTTagCompound getTagCompound() {
        return itemStack.getTagCompound();
    }

    public NBTTagCompound getOrCreateSubCompound(String key) {
        if (itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey(key, 10)) {
            return itemStack.getTagCompound().getCompoundTag(key);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            itemStack.setTagInfo(key, nbttagcompound);
            return nbttagcompound;
        }
    }

    @Nullable
    public NBTTagCompound getSubCompound(String key) {
        return itemStack.getTagCompound() != null && itemStack.getTagCompound().hasKey(key, 10) ? itemStack.getTagCompound().getCompoundTag(key) : null;
    }

    public void removeSubCompound(String key) {
        itemStack.removeSubCompound(key);

    }

    public NBTTagList getEnchantmentTagList() {
        return itemStack.getTagCompound() != null ? itemStack.getTagCompound().getTagList("ench", 10) : new NBTTagList();
    }

    public void setTagCompound(@Nullable NBTTagCompound nbt) {
        itemStack.setTagCompound(nbt);
    }

    public String getDisplayName() {
        return itemStack.getDisplayName();
    }

    public DynamicItemStack setTranslatableName(String p_190924_1_) {
        itemStack.getOrCreateSubCompound("display").setString("LocName", p_190924_1_);
        return this;
    }

    public DynamicItemStack setStackDisplayName(String displayName) {
        itemStack.getOrCreateSubCompound("display").setString("Name", displayName);
        return this;
    }

    public void clearCustomName() {
        itemStack.clearCustomName();

    }

    public boolean hasDisplayName() {
        NBTTagCompound nbttagcompound = itemStack.getSubCompound("display");
        return nbttagcompound != null && nbttagcompound.hasKey("Name", 8);
    }

    @SideOnly(Side.CLIENT)
    public List<String> getTooltip(@Nullable EntityPlayer playerIn, ITooltipFlag advanced) {
       return itemStack.getTooltip(playerIn,advanced);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect() {
        return itemStack.getItem().hasEffect(itemStack);
    }

    /** @deprecated */
    @Deprecated
    public EnumRarity getRarity() {
        return itemStack.getItem().getRarity(itemStack);
    }

    public boolean isItemEnchantable() {
        if (!itemStack.getItem().isEnchantable(itemStack)) {
            return false;
        } else {
            return !itemStack.isItemEnchanted();
        }
    }

    public void addEnchantment(Enchantment ench, int level) {
        itemStack.addEnchantment(ench,level);
    }

    public boolean isItemEnchanted() {
        return itemStack.isItemEnchanted();
    }

    public void setTagInfo(String key, NBTBase value) {
        if (itemStack.getTagCompound() == null) {
            itemStack.setTagCompound(new NBTTagCompound());
        }

        itemStack.getTagCompound().setTag(key, value);
    }

    public boolean canEditBlocks() {
        return itemStack.getItem().canItemEditBlocks();
    }

    public boolean isOnItemFrame() {
        return itemStack.isOnItemFrame();
    }

    public void setItemFrame(EntityItemFrame frame) {
        itemStack.setItemFrame( frame);
    }

    @Nullable
    public EntityItemFrame getItemFrame() {
        return itemStack.getItemFrame();
    }

    public int getRepairCost() {
        return itemStack.getRepairCost();
    }

    public void setRepairCost(int cost) {
        itemStack.setRepairCost(cost);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return itemStack.getAttributeModifiers(equipmentSlot);
    }

    public void addAttributeModifier(String attributeName, AttributeModifier modifier, @Nullable EntityEquipmentSlot equipmentSlot) {
        itemStack.addAttributeModifier(attributeName,  modifier, equipmentSlot);
    }

    public ITextComponent getTextComponent() {
        return itemStack.getTextComponent();
    }

    public boolean canDestroy(Block blockIn) {
        return itemStack.canDestroy(blockIn);
    }

    public boolean canPlaceOn(Block blockIn) {
        return itemStack.canPlaceOn(blockIn);
    }

    public int getAnimationsToGo() {
        return itemStack.getAnimationsToGo();
    }

    public void setAnimationsToGo(int animations) {
        itemStack.setAnimationsToGo(animations);
    }

    public int getCount() {
        return itemStack.getCount();
    }

    public void setCount(int size) {
        itemStack.setCount(size);
    }

    public void grow(int quantity) {
        itemStack.grow(quantity);
    }

    public void shrink(int quantity) {
        itemStack.grow(-quantity);
    }

    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return itemStack.hasCapability(capability,facing);
    }

    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return itemStack.getCapability(capability,facing);
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        itemStack.deserializeNBT(nbt);
    }

    public NBTTagCompound serializeNBT() {
        return  itemStack.serializeNBT();
    }

    public boolean areCapsCompatible(ItemStack other) {
        return itemStack.areCapsCompatible(other);
    }
    public boolean areCapsCompatible(DynamicItemStack other) {
        return itemStack.areCapsCompatible(other.itemStack);
    }

    private void forgeInit() {
        Item item = itemStack.getItem();
        if (item != null) {
            this.delegate = item.delegate;
            ICapabilityProvider provider = item.initCapabilities(this.itemStack,this.getTagCompound());
            capabilities = ForgeEventFactory.gatherCapabilities(this.itemStack, provider);
            if (itemStack.getTagCompound() != null && capabilities != null) {
                capabilities.deserializeNBT(itemStack.getTagCompound());
            }
        }

    }

    @Nullable
    private Item getItemRaw() {
        return itemStack.getItem();
    }

    public static boolean areDynamicItemStacksEqualUsingNBTShareTag(DynamicItemStack stackA, DynamicItemStack stackB) {
        if (stackA.isEmpty()) {
            return stackB.isEmpty();
        } else {
            return !stackB.isEmpty() && stackA.isDynamicItemStackEqualUsingNBTShareTag(stackB);
        }
    }

    private boolean isDynamicItemStackEqualUsingNBTShareTag(DynamicItemStack other) {
        return itemStack.getCount() == other.getCount() && itemStack.getItem() == other.getItem() && itemStack.getItemDamage() == other.getItemDamage() && areDynamicItemStackShareTagsEqual(this, other);
    }

    public static boolean areDynamicItemStackShareTagsEqual(DynamicItemStack stackA, DynamicItemStack stackB) {
        return areDynamicItemStackShareTagsEqual(stackA.itemStack,stackB.itemStack);
    }
    public static boolean areDynamicItemStackShareTagsEqual(ItemStack stackA, ItemStack stackB) {
        NBTTagCompound shareTagA = stackA.getItem().getNBTShareTag(stackA);
        NBTTagCompound shareTagB = stackB.getItem().getNBTShareTag(stackB);
        if (shareTagA == null) {
            return shareTagB == null;
        } else {
            return shareTagB != null && shareTagA.equals(shareTagB);
        }
    }

    public boolean doesSneakBypassUse(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        return itemStack.isEmpty() || itemStack.getItem().doesSneakBypassUse(this.itemStack, world, pos, player);
    }
    
}
