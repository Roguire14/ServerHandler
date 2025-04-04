package fr.roguire.serverhandler.listener;

import fr.roguire.serverhandler.utils.items.CustomItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static fr.roguire.serverhandler.utils.UsefullFunctions.getDisplayName;

public class CustomItemManager implements Listener {
    private static final Map<String, CustomItem> customItems = new HashMap<>();

    public static void registerItem(String id, CustomItem item) {
        customItems.put(id, item);
    }

    public static CustomItem getItem(ItemStack item) {
        if(item == null) return null;
        return customItems.get(getDisplayName(item.displayName()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        CustomItem item = getItem(event.getItem());
        if(item != null) item.onInteract(event);
    }


}
