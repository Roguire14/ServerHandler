package fr.roguire.serverhandler.listener.player;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.items.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;

public class OnJoinEvent implements Listener {

    private final ServerHandler plugin;
    private final CustomItem item;
    public OnJoinEvent(ServerHandler plugin, CustomItem compass) {
        this.plugin = plugin;
        this.item = compass;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        p.getInventory().clear();
        p.getInventory().setItem(4,item.getItem());
        Set<String> perms = plugin.getPermissions(p.getUniqueId());
        for(String perm : perms) {
            p.addAttachment(plugin, perm, true);
        }
    }
}
