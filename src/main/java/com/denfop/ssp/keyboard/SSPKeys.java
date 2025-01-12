package com.denfop.ssp.keyboard;

import ic2.core.IC2;
import ic2.core.util.Keyboard;
import ic2.core.util.ReflectionUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Set;

public final class SSPKeys extends Keyboard {

    public static final Keyboard.IKeyWatcher FLY_KEY;
    public static final Keyboard.IKeyWatcher poison1;
    public static final Keyboard.IKeyWatcher poison;
    public static final Keyboard.IKeyWatcher poison2;

    static {
        FLY_KEY = new KeyWatcher(SSPKey.fly);
        poison = new KeyWatcher(SSPKey.poison);
        poison1 = new KeyWatcher(SSPKey.poison1);
        poison2 = new KeyWatcher(SSPKey.poison2);
    }

    public SSPKeys() {
    }

    public static void addFlyKey() {
        IC2.keyboard.addKeyWatcher(SSPKeys.FLY_KEY);
        IC2.keyboard.addKeyWatcher(SSPKeys.poison1);
        IC2.keyboard.addKeyWatcher(SSPKeys.poison2);
        IC2.keyboard.addKeyWatcher(SSPKeys.poison);
    }

    public static boolean Isremovepoison(final EntityPlayer player) {
        return IC2.keyboard.isKeyDown(player, SSPKeys.poison);
    }

    public static boolean Isremovepoison1(final EntityPlayer player) {
        return IC2.keyboard.isKeyDown(player, SSPKeys.poison1);
    }

    public static boolean Isremovepoison2(final EntityPlayer player) {
        return IC2.keyboard.isKeyDown(player, SSPKeys.poison2);
    }

    public static boolean isFlyKeyDown(final EntityPlayer player) {
        return IC2.keyboard.isKeyDown(player, SSPKeys.FLY_KEY);
    }

    public enum SSPKey {
        fly(33, "Fly Key"),
        poison(34, "Poison Leggings"),
        poison1(35, "Poison Boots"),
        poison2(36, "Poison Chestplate");

        public final Keyboard.Key key;
        @SideOnly(Side.CLIENT)
        public KeyBinding binding;

        SSPKey(final int keyID, final String description) {
            this.key = this.addKey(this.name());
            if (IC2.platform.isRendering()) {
                ClientRegistry.registerKeyBinding(this.binding = new KeyBinding(
                        description,
                        keyID,
                        "SuperSolarPanels".substring(0, 1).toUpperCase(Locale.ENGLISH) + "SuperSolarPanels".substring(1)
                ));
            }
        }

        public static Field getKeysField() {
            try {
                final Field field = ReflectionUtil.getField(Key.class, "keys");
                ReflectionUtil.getField(Field.class, new String[]{"modifiers"}).setInt(field, field.getModifiers() & 0xFFFFFFEF);
                return field;
            } catch (Exception e) {
                throw new RuntimeException("Error reflecting keys field!", e);
            }
        }

        private Keyboard.Key addKey(final String name) {
            final Keyboard.Key key = EnumHelper.addEnum(Key.class, name, new Class[0]);
            ReflectionUtil.setValue(null, getKeysField(), ArrayUtils.add(Key.keys, key));
            return key;
        }
    }

    public static class KeyWatcher implements Keyboard.IKeyWatcher {

        public final SSPKey key;

        public KeyWatcher(final SSPKey key) {
            this.key = key;
        }

        @SideOnly(Side.CLIENT)
        public void checkForKey(final Set<Keyboard.Key> pressedKeys) {
            if (GameSettings.isKeyDown(this.key.binding)) {
                pressedKeys.add(this.getRepresentation());
            }
        }

        public Keyboard.Key getRepresentation() {
            return this.key.key;
        }

    }

}
