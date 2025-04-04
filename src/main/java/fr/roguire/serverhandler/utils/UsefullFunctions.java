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

    public static String getDisplayName(Component itemComponent){
        return PlainTextComponentSerializer.plainText().serialize(itemComponent).replaceAll("^\\[|]$","");
    }
}
