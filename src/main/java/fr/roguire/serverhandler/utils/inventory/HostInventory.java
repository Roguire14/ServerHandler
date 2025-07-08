package fr.roguire.serverhandler.utils.inventory;

import com.google.gson.JsonObject;
import fr.roguire.serverhandler.utils.api.ApiCommunicator;
import fr.roguire.serverhandler.utils.inventory.models.GameHoster;
import fr.roguire.serverhandler.utils.inventory.models.HostItem;
import fr.roguire.serverhandler.utils.items.ItemCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static fr.roguire.serverhandler.utils.UsefullFunctions.isSword;
import static fr.roguire.serverhandler.utils.UsefullFunctions.setSwordAttributeModifiers;

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
            if(isSword(item)) setSwordAttributeModifiers(item, item.getItemMeta());
            inventory.addItem(item);
            items.put(key.toLowerCase(), new HostItem(item, key));
        });
    }

    void fillHostItems();

    default void fetchConfig(String endpoint, Inventory inventory, Map<String, HostItem> items, ApiCommunicator communicator) {
        communicator.sendGetRequest("config/get-config/"+endpoint)
            .thenAccept(response -> {
                if(response == null) return;
                int statusCode = response.get("status").getAsInt();
                switch(statusCode) {
                    case 409:
                        fillHostItems();
                        break;
                    case 200:
                        JsonObject answer = response.get("message").getAsJsonObject();
                        addHostItems(answer, inventory, items);
                        break;
                }
            });
    }

    default boolean canHost(Player player){
        return Boolean.TRUE.equals(GameHoster.isPlayerGenerating.get(player));
    }
}
