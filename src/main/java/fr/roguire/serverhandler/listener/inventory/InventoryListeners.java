package fr.roguire.serverhandler.listener.inventory;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.inventory.CustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

import static fr.roguire.serverhandler.utils.UsefullFonctions.getDisplayName;

public class InventoryListeners implements Listener {

    private final Map<String, CustomInventory> inventories;
    private final ServerHandler serverHandler;

    public InventoryListeners(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
        inventories = new HashMap<>();
    }

    public void registerInventory(CustomInventory inventory){
        inventories.put(inventory.getInventoryName(), inventory);
    }

    public void registerInventories(CustomInventory ...inventories){
        for (CustomInventory inventory : inventories) {
            registerInventory(inventory);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null) return;
        String inventoryName = getDisplayName(event.getView().title());
        if(!isAcknowledgable(inventoryName)) return;
        inventories.get(inventoryName).handleClick(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        String inventoryName = getDisplayName(event.getView().title());
        if(!isAcknowledgable(inventoryName)) return;
        inventories.get(inventoryName).handleClosing(event);
    }

    private boolean isAcknowledgable(String inventoryName) {
        return inventories.containsKey(inventoryName);
    }

}
