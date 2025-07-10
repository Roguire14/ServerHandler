package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.models.HostedServer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CustomInventoryRefreshable extends CustomInventory {

    protected final ServerHandler plugin;
    protected BukkitTask refreshingTask;
    protected final Set<HostedServer> serversInInventory = new HashSet<>();
    protected final List<Player> playersSeeingInventory;

    public CustomInventoryRefreshable(ServerHandler plugin, int size, Component component) {
        super(size, component);
        this.plugin = plugin;
        playersSeeingInventory = new ArrayList<>();
    }

    public void startRefreshing() {
        refreshingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshInventory, 0, 15L * 1);
    }

    public void stopRefreshing() {
        if (refreshingTask != null) {
            refreshingTask.cancel();
        }
    }

    public void open(Player player) {
        startRefreshing();
        playersSeeingInventory.add(player);
        super.open(player);
    }

    public void handleClosing(InventoryCloseEvent event) {
        playersSeeingInventory.remove(event.getPlayer());
        if (playersSeeingInventory.isEmpty()) stopRefreshing();
    }

    @Override
    protected void handleValidClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "server_name");
        if (!container.has(key, PersistentDataType.STRING)) return;
        String serverName = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

        plugin.sendToServer((Player) event.getWhoClicked(), serverName);
    }

    private void refreshInventory() {
        plugin.getBungeeCordCommunicator().refreshServer();
        Set<HostedServer> activeServers = getActiveServers();

        for (HostedServer server : activeServers) {
            if (serversInInventory.add(server)) addNewServer(server);
        }

        serversInInventory.removeIf(server -> !activeServers.contains(server));

        int neededSlots = serversInInventory.size();
        int requiredSize = Math.max(9, ((neededSlots + 8) / 9) * 9);
        int currentSize = inventory.getSize();

        if (requiredSize != currentSize) {
            List<Player> viewers = inventory.getViewers().stream()
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .toList();

            viewers.forEach(Player::closeInventory);
            recreateInventory(requiredSize);
            fillInventory();
            viewers.forEach(this::open);
        } else {
            inventory.clear();
            fillInventory();
        }
    }

    private void fillInventory() {
        serversInInventory.forEach(s -> {
            inventory.addItem(s.block());
        });
    }

    protected abstract void addNewServer(HostedServer server);

    protected abstract Set<HostedServer> getActiveServers();

}
