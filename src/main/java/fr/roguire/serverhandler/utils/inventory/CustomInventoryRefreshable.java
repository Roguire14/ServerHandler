package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static fr.roguire.serverhandler.utils.UsefullFunctions.isSword;
import static fr.roguire.serverhandler.utils.UsefullFunctions.setSwordAttributeModifiers;

public abstract class CustomInventoryRefreshable extends CustomInventory{

    protected final ServerHandler plugin;
    protected BukkitTask refreshingTask;
    protected final Map<String, ItemStack> items = new HashMap<>();
    protected final Map<ItemStack, String> nameHandler = new HashMap<>();
    protected final List<Player> playersSeeingInventory;

    public CustomInventoryRefreshable(ServerHandler plugin, int size, Component component) {
        super(size, component);
        this.plugin = plugin;
        playersSeeingInventory = new ArrayList<>();
    }

    public void startRefreshing() {
        refreshingTask = Bukkit.getScheduler().runTaskTimer(plugin, this::refreshInventory, 0, 15L*1);
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
        if(playersSeeingInventory.isEmpty()) stopRefreshing();
    }

    private void refreshInventory() {
        plugin.getBungeeCordCommunicator().refreshServer();
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
        toRemove.forEach( x -> {
            nameHandler.remove(items.get(x));
            items.remove(x);
        });
        inventory.clear();
        fillInventory();
    }

    private void fillInventory() {
        items.values().forEach(inventory::addItem);
    }

    protected void setBlockData(ItemStack item, String fullName){
        ItemMeta meta = item.getItemMeta();
        String[] split = fullName.split("-");
        String[] itemName = Arrays.copyOfRange(split, 2, split.length-2);
        String itemNameJoined = String.join(" ", itemName);
        meta.lore(List.of(
            Component.text("Host: ", NamedTextColor.DARK_AQUA)
                .decorate(TextDecoration.BOLD)
                .append(
                    Component.text(split[split.length-1], NamedTextColor.GOLD)
                        .decorate(TextDecoration.ITALIC).decoration(TextDecoration.BOLD, false)
                )
        ));
        if(isSword(item)) setSwordAttributeModifiers(item, meta);
        meta.displayName(Component.text(itemNameJoined.toUpperCase()));
        item.setItemMeta(meta);
    }

    protected abstract void addNewItem(String entry);

    protected abstract Set<String> getActiveServers();

    protected record ItemClickable(String fullName, String displayName, ItemStack item) {
    }

}
