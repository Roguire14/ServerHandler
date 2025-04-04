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

public class MiniGameInventory extends CustomInventoryRefreshable{

    public MiniGameInventory(ServerHandler plugin) {
        super(plugin,9, Component.text("Mini-Jeux")
            .color(NamedTextColor.AQUA)
            .decorate(TextDecoration.BOLD));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        if(item.getItemMeta() == null) return;
        plugin.sendToServer((Player) event.getWhoClicked(), nameHandler.get(event.getCurrentItem()));
    }

    @Override
    protected void addNewItem(String entry) {
        ItemStack item = new ItemStack(Material.CLOCK);
        setBlockData(item, entry);
        nameHandler.put(item, entry);
        items.put(entry, item);
        inventory.addItem(item);
    }

    @Override
    protected Set<String> getActiveServers() {
        return plugin.getMiniGameServers();
    }
}
