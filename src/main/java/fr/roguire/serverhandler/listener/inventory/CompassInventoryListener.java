package fr.roguire.serverhandler.listener.inventory;

import fr.roguire.serverhandler.utils.inventory.CustomInventory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Set;

public class CompassInventoryListener implements Listener {

    private final Set<CustomInventory> inventories;

    public CompassInventoryListener(CustomInventory... inventories) {
        this.inventories = Set.of(inventories);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        inventories.stream()
            .filter(inv -> event.getInventory().equals(inv.getInventory()))
            .findFirst()
            .ifPresent(inv -> inv.handleClick(event));
    }
}
