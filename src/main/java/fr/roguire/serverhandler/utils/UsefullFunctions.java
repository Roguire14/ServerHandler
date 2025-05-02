package fr.roguire.serverhandler.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class UsefullFunctions {

    public static void setSwordAttributeModifiers(ItemStack sword, ItemMeta swordMeta){
        NamespacedKey namespace = new NamespacedKey("plugin", "attack_speed");
        AttributeModifier attackSpeedModifier = new AttributeModifier(namespace, 0.0, AttributeModifier.Operation.ADD_NUMBER);
        swordMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, attackSpeedModifier);
        swordMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        sword.setItemMeta(swordMeta);
    }

    public static boolean isSword(Material material){
        return material == Material.WOODEN_SWORD ||
            material == Material.IRON_SWORD ||
            material == Material.GOLDEN_SWORD ||
            material == Material.DIAMOND_SWORD ||
            material == Material.NETHERITE_SWORD;
    }

    public static boolean isSword(ItemStack sword){
        return isSword(sword.getType());
    }

    public static boolean isGlassPane(Material material){
        return material.name().endsWith("GLASS_PANE");
    }

    public static boolean isGlassPane(ItemStack item){
        return isGlassPane(item.getType());
    }

    public static String getDisplayName(Component itemComponent){
        return PlainTextComponentSerializer.plainText().serialize(itemComponent).replaceAll("^\\[|]$","");
    }

    public static void setUnstackable(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer()
            .set(new NamespacedKey("plugin","unique_id"),
                PersistentDataType.STRING,
                UUID.randomUUID().toString());
        item.setItemMeta(meta);
    }
}
