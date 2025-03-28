package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public abstract class CustomInventoryRefreshable extends CustomInventory{

    private final ServerHandler plugin;
    protected BukkitTask refreshingTask;
    protected final Map<String, ItemStack> items = new HashMap<>();

    public CustomInventoryRefreshable(ServerHandler plugin, int size, Component component) {
        super(size, component);
        this.plugin = plugin;
    }

    public void startRefreshing() {
        refreshingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshInventory, 0, 15L*1);
    }

    private void refreshInventory() {
        Set<String> activeServers = getActiveServers();
        Set<String> newServers = new HashSet<>();

        for(String entry: activeServers) {
            newServers.add(entry);
            if(!items.containsKey(entry)) addNewItem(entry);
        }

        List<String> toRemove = new ArrayList<>();
        items.keySet().forEach(s -> {
            if(!activeServers.contains(s)) toRemove.add(s);
        });
        toRemove.forEach(items::remove);
        inventory.clear();
        fillInventory();
    }

    private void fillInventory() {
        items.values().forEach(inventory::addItem);
    }

    protected abstract void addNewItem(String entry);

    protected abstract Set<String> getActiveServers();


}
