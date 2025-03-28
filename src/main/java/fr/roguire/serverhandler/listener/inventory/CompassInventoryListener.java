package fr.roguire.serverhandler.listener.inventory;

import fr.roguire.serverhandler.utils.inventory.CustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CompassInventoryListener implements Listener {

    private final CustomInventory customInventory;

    public CompassInventoryListener(final CustomInventory inventory) {
        this.customInventory = inventory;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if(event.getInventory().equals(customInventory.getInventory())){
            this.customInventory.handleClick(event);
        }
    }
}
