package com.denfop.ssp.items.armor;

import com.denfop.ssp.SuperSolarPanels;
import com.denfop.ssp.common.Configs;
import com.denfop.ssp.common.Constants;
import com.denfop.ssp.items.armorbase.ItemBoosts;
import com.denfop.ssp.keyboard.SSPKeys;
import ic2.api.item.ElectricItem;
import ic2.api.item.HudMode;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.util.StackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemArmorQuantumBoosts extends ItemBoosts {

    public ItemArmorQuantumBoosts() {
        super("graviBoosts", Configs.maxCharge3, Configs.transferLimit3, Configs.tier3);
        this.setCreativeTab(SuperSolarPanels.SSPTab);
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return Constants.MOD_ID + ":textures/armour/" + this.name + "Overlay" + ".png";
    }

    public void onArmorTick(World world, @Nonnull EntityPlayer player, @Nonnull ItemStack stack) {
        super.onArmorTick(world, player, stack);
        player.fallDistance = 0;
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbtData.getByte("toggleTimer");
        if (IC2.platform.isSimulating()) {
            boolean wasOnGround = (!nbtData.hasKey("wasOnGround") || nbtData.getBoolean("wasOnGround"));
            if (wasOnGround && !player.onGround && IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                ElectricItem.manager.use(stack, 4000.0D, null);
            }
            if (player.onGround != wasOnGround) {
                nbtData.setBoolean("wasOnGround", player.onGround);
            }
        }
        player.stepHeight = 1;
        boolean nightVision = nbtData.getBoolean("nightVision");
        short hubMode = nbtData.getShort("HudMode");
        if (SSPKeys.Isremovepoison1(player) && toggleTimer == 0) {
            toggleTimer = 10;
            nightVision = !nightVision;
            if (IC2.platform.isSimulating()) {
                nbtData.setBoolean("nightVision", nightVision);
                if (nightVision) {
                    IC2.platform.messagePlayer(player, "Effects enabled.");
                } else {
                    IC2.platform.messagePlayer(player, "Effects disabled.");
                }
            }
        }
        if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            if (hubMode == HudMode.getMaxMode()) {
                hubMode = 0;
            } else {
                ++hubMode;
            }
            if (IC2.platform.isSimulating()) {
                nbtData.setShort("HudMode", hubMode);
                IC2.platform.messagePlayer(player, Localization.translate(HudMode.getFromID(hubMode).getTranslationKey()));
            }
        }
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            final String s = "toggleTimer";
            --toggleTimer;
            nbtData.setByte(s, toggleTimer);
        }
        if (nightVision && IC2.platform.isSimulating() && ElectricItem.manager.use(stack, 1.0, player)) {
            if (Configs.canCraftMT) {
                player.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 300, 0, true, true));
            } else {
                return;
            }
            if (Configs.canCraftASP) {
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 300, 0, true, true));
            } else {
                return;
            }
            if (Configs.canCraftASH) {
                player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 300, 4, true, true));
            }
        }
    }

    public float getBaseThrust(ItemStack stack, boolean hover) {
        return hover ? 1.0F : 0.5F;
    }

    public float getBoostThrust(EntityPlayer player, ItemStack stack, boolean hover) {
        return (IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= 834.0D)
                ? (hover ? 0.1F : 0.3F)
                : 0.0F;
    }

    public boolean useBoostPower(ItemStack stack, float boostAmount) {
        return (ElectricItem.manager.discharge(stack, 834.0D, 2147483647, true, false, false) > 0.0D);
    }

    public float getHoverBoost(EntityPlayer player, ItemStack stack, boolean up) {
        if (IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= 834.0D) {
            if (!player.onGround) {
                ElectricItem.manager.discharge(stack, 834.0D, 2147483647, true, false, false);
            }
            return 3.0F;
        }
        return 1.0F;
    }

    public boolean drainEnergy(ItemStack pack, int amount) {
        return (ElectricItem.manager.discharge(pack, 278.0D, 2147483647, true, false, false) > 0.0D);
    }

    public float getPower(ItemStack stack) {
        return 1.5F;
    }

    public float getDropPercentage(ItemStack stack) {
        return 0.01F;
    }

    public float getHoverMultiplier(ItemStack stack, boolean upwards) {
        return 0.25F;
    }

    public float getWorldHeightDivisor(ItemStack stack) {
        return 0.91071427F;
    }

    public boolean canProvideEnergy(ItemStack stack) {
        return true;
    }

    public double getDamageAbsorptionRatio() {
        return 1.1D;
    }

    public int getEnergyPerDamage() {
        return 20000;
    }

    public boolean hasColor(@Nonnull ItemStack stack) {
        return (getColor(stack) != -1);
    }

    public int getColor(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = getDisplayNbt(stack, false);
        return (nbt == null || !nbt.hasKey("colour", 3)) ? -1 : nbt.getInteger("colour");
    }

    public void removeColor(@Nonnull ItemStack stack) {
        NBTTagCompound nbt = getDisplayNbt(stack, false);
        if (nbt == null || !nbt.hasKey("colour", 3)) {
            return;
        }
        nbt.removeTag("colour");
        if (nbt.hasNoTags() && stack.getTagCompound() != null) {
            stack.getTagCompound().removeTag("display");
        }
    }

    public void setColor(@Nonnull ItemStack stack, int colour) {
        getDisplayNbt(stack, true).setInteger("colour", colour);
    }

    protected NBTTagCompound getDisplayNbt(ItemStack stack, boolean create) {
        NBTTagCompound out, nbt = stack.getTagCompound();
        if (nbt == null) {
            if (!create) {
                return null;
            }
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if (!nbt.hasKey("display", 10)) {
            if (!create) {
                return null;
            }
            out = new NBTTagCompound();
            nbt.setTag("display", out);
        } else {
            out = nbt.getCompoundTag("display");
        }
        return out;
    }

    @Nonnull
    public EnumRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EnumRarity.EPIC;
    }

}
