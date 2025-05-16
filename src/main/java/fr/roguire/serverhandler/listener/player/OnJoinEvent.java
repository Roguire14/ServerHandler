package fr.roguire.serverhandler.listener.player;

import fr.roguire.serverhandler.utils.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinEvent implements Listener {

    private final CustomItem item;
    public OnJoinEvent(CustomItem compass) {
        this.item = compass;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.getInventory().clear();
        p.getInventory().setItem(4,item.getItem());
    }
}
