package fr.roguire.serverhandler.utils.inventory;

import com.google.gson.JsonObject;
import fr.roguire.serverhandler.utils.inventory.models.HostItem;
import fr.roguire.serverhandler.utils.items.ItemCreator;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface HostInventory extends ItemCreator {
    default void addHostItems(JsonObject answer, Inventory inventory, Map<String, HostItem> items) {
        answer.entrySet().forEach(entry -> {
            String key = entry.getKey();
            JsonObject currentItem = entry.getValue().getAsJsonObject();
            ItemStack item;
            if(currentItem.get("block") != null) {
                String blockName = currentItem.get("block").getAsString().replace("Material.","");
                item = createItem(Material.getMaterial(blockName), key.toUpperCase(), NamedTextColor.GRAY);
            }else{
                item = createItem(Material.WHITE_BANNER, key.toUpperCase(), NamedTextColor.GRAY);
            }

            inventory.addItem(item);
            items.put(key.toLowerCase(), new HostItem(item, key));
        });
    }

    void fillHostItems();
}
