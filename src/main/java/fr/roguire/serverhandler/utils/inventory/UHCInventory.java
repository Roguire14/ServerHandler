package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.UsefullFonctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static fr.roguire.serverhandler.utils.UsefullFonctions.getDisplayName;

public class UHCInventory extends CustomInventory{

    private final ServerHandler plugin;
    private BukkitTask refreshingTask;
    private final List<Player> players;
    private final Map<String, ItemStack> uhcItems;

    public UHCInventory(ServerHandler plugin) {
        super(9, Component.text("UHC")
            .color(NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD));
        this.plugin = plugin;
        players = new ArrayList<>();
        uhcItems = new HashMap<>();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        if(item.getItemMeta() == null) return;
        plugin.sendToServer((Player) event.getWhoClicked(), getDisplayName(item.displayName()));
    }

    @Override
    public void open(Player player) {
        super.open(player);
        players.add(player);
        refreshInventory();
    }

    @Override
    public void handleClosing(InventoryCloseEvent event) {
        players.remove(event.getPlayer());
        if(refreshingTask != null && players.isEmpty()) refreshingTask.cancel();
    }

    private void refreshInventory() {
        refreshingTask = Bukkit.getScheduler().runTaskTimer(plugin,
            () -> {
                plugin.getBungeeCordCommunicator().refreshServer();
                Set<String> activeServers = plugin.getUHCServers();
                Set<String> servers = new HashSet<>();
                for (String uhcServer : activeServers) {
                    servers.add(uhcServer);
                    if(!uhcItems.containsKey(uhcServer))
                        addRegisteredUHC(uhcServer);
                }
                List<String> serverToRemove = new ArrayList<>();
                uhcItems.forEach((s, itemStack) -> {
                    if(!activeServers.contains(s)) serverToRemove.add(s);
                });
                serverToRemove.forEach(s ->
                    uhcItems.remove(s)
                );
                inventory.clear();
                fillInventory();
            },0,15L*1);
    }

    private void fillInventory(){
        uhcItems.forEach((s, itemStack) -> inventory.addItem(itemStack));
    }

    private void addRegisteredUHC(String uhcServer) {
        ItemStack uhcItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = uhcItem.getItemMeta();
        UsefullFonctions.setSwordAttributeModifiers(uhcItem, meta);
        meta.displayName(Component.text(uhcServer));
        uhcItem.setItemMeta(meta);
        uhcItems.put(uhcServer, uhcItem);
        inventory.addItem(uhcItem);
    }
}
