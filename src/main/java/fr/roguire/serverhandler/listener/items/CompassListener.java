package fr.roguire.serverhandler.listener.items;

import fr.roguire.serverhandler.utils.items.TeleporterCompass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class CompassListener implements Listener {

    private final TeleporterCompass compass;

    public CompassListener(TeleporterCompass compass) {
        this.compass = compass;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getItem() == null) return;
        if(event.getItem().displayName().equals(compass.getItem().displayName())){
            compass.onInteract(event);
        }
    }
}
