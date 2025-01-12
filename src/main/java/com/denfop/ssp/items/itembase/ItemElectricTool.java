package com.denfop.ssp.items.itembase;

import com.denfop.ssp.SuperSolarPanels;
import com.denfop.ssp.common.Constants;
import com.google.common.base.CaseFormat;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.IPseudoDamageItem;
import ic2.core.item.ItemToolIC2;
import ic2.core.item.tool.HarvestLevel;
import ic2.core.item.tool.ToolClass;
import ic2.core.ref.ItemName;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class ItemElectricTool extends ItemToolIC2 implements IPseudoDamageItem, IElectricItem, IItemHudInfo {

    public static int maxCharge;
    public static int transferLimit;
    public static int tier;
    public static String name;
    public final double operationEnergyCost;
    protected AudioSource audioSource;
    protected boolean wasEquipped;

    protected ItemElectricTool(
            String name,
            int operationEnergyCost,
            HarvestLevel harvestLevel,
            ToolClass toolClasses,
            int maxCharge,
            int transferLimit,
            int tier,
            int damage2,
            int damage1
    ) {
        this(name, operationEnergyCost, Collections.emptySet(), maxCharge, transferLimit, tier);
    }

    protected ItemElectricTool(
            String name,
            int operationEnergyCost,
            Set<ToolClass> toolClasses,
            int maxCharge,
            int transferLimit,
            int tier
    ) {
        this(name, operationEnergyCost, toolClasses, new HashSet<>(), maxCharge, transferLimit, tier);
    }

    private ItemElectricTool(
            String name,
            int operationEnergyCost,
            Set<ToolClass> toolClasses,
            Set<Block> mineableBlocks,
            int maxCharge,
            int transferLimit,
            int tier
    ) {
        super(null, (float) 2.0, (float) -3.0, HarvestLevel.Iron, toolClasses, mineableBlocks);
        this.operationEnergyCost = operationEnergyCost;
        setMaxDamage(27);
        setNoRepair();
        BlocksItems.registerItem(this, SuperSolarPanels.getIdentifier(ItemElectricTool.name = name)).setUnlocalizedName(name);

    }


    public String getUnlocalizedName() {
        return Constants.MOD_ID + "." + super.getUnlocalizedName().substring(4);
    }

    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        if (!ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            return 1.0F;
        }
        return super.getDestroySpeed(stack, state);
    }

    @SideOnly(Side.CLIENT)
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation(
                this,
                0,
                new ModelResourceLocation(Constants.MOD_ID + ":" + CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.LOWER_UNDERSCORE,
                        ItemElectricTool.name
                ), null)
        );
    }

    @Nonnull
    public EnumActionResult onItemUse(
            @Nonnull EntityPlayer player,
            @Nonnull World world,
            @Nonnull BlockPos pos,
            @Nonnull EnumHand hand,
            @Nonnull EnumFacing side,
            float xOffset,
            float yOffset,
            float zOffset
    ) {
        ElectricItem.manager.use(StackUtil.get(player, hand), 0.0D, player);
        return super.onItemUse(player, world, pos, hand, side, xOffset, yOffset, zOffset);
    }

    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
        ElectricItem.manager.use(StackUtil.get(player, hand), 0.0D, player);
        return super.onItemRightClick(world, player, hand);
    }

    public void onUpdate(@Nonnull ItemStack itemstack, @Nonnull World world, @Nonnull Entity entity, int i, boolean flag) {
        boolean isEquipped = (flag && entity instanceof EntityLivingBase);
        if (IC2.platform.isRendering()) {
            if (isEquipped && !this.wasEquipped) {
                if (this.audioSource == null) {
                    String sound = getIdleSound((EntityLivingBase) entity, itemstack);
                    if (sound != null) {
                        this.audioSource = IC2.audioManager.createSource(
                                entity,
                                PositionSpec.Hand,
                                sound,
                                true,
                                false,
                                IC2.audioManager.getDefaultVolume()
                        );
                    }
                }
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
                String initSound = getStartSound((EntityLivingBase) entity, itemstack);
                if (initSound != null) {
                    IC2.audioManager.playOnce(entity, PositionSpec.Hand, initSound, true, IC2.audioManager.getDefaultVolume());
                }
            } else if (!isEquipped && this.audioSource != null) {
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase theEntity = (EntityLivingBase) entity;
                    ItemStack stack = theEntity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
                    if (stack == null || stack.getItem() != this || stack == itemstack) {
                        removeAudioSource();
                        String sound = getStopSound(theEntity, itemstack);
                        if (sound != null) {
                            IC2.audioManager.playOnce(
                                    entity,
                                    PositionSpec.Hand,
                                    sound,
                                    true,
                                    IC2.audioManager.getDefaultVolume()
                            );
                        }
                    }
                }
            } else if (this.audioSource != null) {
                this.audioSource.updatePosition();
            }
            this.wasEquipped = isEquipped;
        }
    }

    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        ElectricItemManager.addChargeVariants(this, subItems);
    }

    public boolean onDroppedByPlayer(@Nonnull ItemStack item, @Nonnull EntityPlayer player) {
        removeAudioSource();
        return true;
    }

    public boolean isRepairable() {
        return false;
    }

    public boolean isBookEnchantable(@Nonnull ItemStack itemstack1, @Nonnull ItemStack itemstack2) {
        return false;
    }

    public void setDamage(@Nonnull ItemStack stack, int damage) {
        int prev = getDamage(stack);
        if (damage != prev && BaseElectricItem.logIncorrectItemDamaging) {
            IC2.log.warn(LogCategory.Armor, new Throwable(), "Detected invalid armor damage application (%d):", damage - prev);
        }
    }

    protected String getIdleSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    protected String getStartSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    protected void removeAudioSource() {
        if (this.audioSource != null) {
            this.audioSource.stop();
            this.audioSource.remove();
            this.audioSource = null;
        }
    }

    protected String getStopSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    public boolean hitEntity(
            @Nonnull ItemStack itemstack,
            @Nonnull EntityLivingBase entityliving,
            @Nonnull EntityLivingBase entityliving1
    ) {
        return true;
    }

    public boolean onBlockDestroyed(
            @Nonnull ItemStack stack,
            @Nonnull World world,
            IBlockState state,
            @Nonnull BlockPos pos,
            @Nonnull EntityLivingBase user
    ) {
        if (state.getBlockHardness(world, pos) != 0.0F) {
            if (user != null) {
                ElectricItem.manager.use(stack, this.operationEnergyCost, user);
            } else {
                ElectricItem.manager.discharge(stack, this.operationEnergyCost, tier, true, false, false);
            }
        }
        return true;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean getIsRepairable(@Nonnull ItemStack par1ItemStack, @Nonnull ItemStack par2ItemStack) {
        return false;
    }

    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    public double getMaxCharge(ItemStack stack) {
        return maxCharge;
    }

    public int getTier(ItemStack stack) {
        return tier;
    }

    public double getTransferLimit(ItemStack stack) {
        return transferLimit;
    }

    public List<String> getHudInfo(ItemStack stack, boolean advanced) {
        List<String> info = new LinkedList<>();
        info.add(ElectricItem.manager.getToolTip(stack));
        info.add(Localization.translate("ic2.item.tooltip.PowerTier", tier));
        return info;
    }

    protected ItemStack getItemStack(double charge) {
        ItemStack ret = new ItemStack(this);
        ElectricItem.manager.charge(ret, charge, 2147483647, true, false);
        return ret;
    }

    public void setStackDamage(ItemStack stack, int damage) {
        super.setDamage(stack, damage);
    }

}
