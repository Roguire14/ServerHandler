package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class UHCInventory extends CustomInventoryRefreshable{

    public UHCInventory(ServerHandler plugin) {
        super(plugin,9, Component.text("UHC")
            .color(NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        if(item.getItemMeta() == null) return;
        String serverName = nameHandler.get(event.getCurrentItem());
        plugin.sendToServer((Player) event.getWhoClicked(), serverName);
    }

    @Override
    protected void addNewItem(String name) {
        ItemStack uhcItem = new ItemStack(Material.IRON_SWORD);
        setBlockData(uhcItem, name);
        nameHandler.put(uhcItem, name);
        items.put(name, uhcItem);
        inventory.addItem(uhcItem);
    }

    @Override
    protected Set<String> getActiveServers() {
        return plugin.getUHCServers();
    }
}
