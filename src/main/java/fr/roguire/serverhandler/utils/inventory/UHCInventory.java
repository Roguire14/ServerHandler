package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.UsefullFonctions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

import static fr.roguire.serverhandler.utils.UsefullFonctions.getDisplayName;

public class UHCInventory extends CustomInventoryRefreshable{

    private final List<Player> players;

    public UHCInventory(ServerHandler plugin) {
        super(plugin,9, Component.text("UHC")
            .color(NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD));
        players = new ArrayList<>();
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        if(item.getItemMeta() == null) return;
        plugin.sendToServer((Player) event.getWhoClicked(), getDisplayName(item.displayName()));
    }

    @Override
    protected void addNewItem(String name) {
        ItemStack uhcItem = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = uhcItem.getItemMeta();
        UsefullFonctions.setSwordAttributeModifiers(uhcItem, meta);
        meta.displayName(Component.text(name));
        uhcItem.setItemMeta(meta);
        items.put(name, uhcItem);
        inventory.addItem(uhcItem);
    }

    @Override
    protected Set<String> getActiveServers() {
        plugin.getBungeeCordCommunicator().refreshServer();
        return plugin.getUHCServers();
    }
}
