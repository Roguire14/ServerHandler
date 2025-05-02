package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static fr.roguire.serverhandler.utils.UsefullFunctions.isSword;
import static fr.roguire.serverhandler.utils.UsefullFunctions.setSwordAttributeModifiers;

public class AdminInventory extends CustomInventoryBordered {

    private final ServerHandler plugin;
    private final CustomInventory miniGameInventory;
    private final ItemStack miniGameItem;
    private final ItemStack uhcItem;
    private final CustomInventory uhcInventory;

    public AdminInventory(ServerHandler plugin) {
        super(9, Material.RED_STAINED_GLASS_PANE, Component.text("Admin").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        this.plugin = plugin;
        miniGameItem = createItem(Material.CLOCK, "Créer un mini-jeu", NamedTextColor.DARK_GREEN);
        uhcItem = createItem(Material.IRON_SWORD, "Créer un UHC", NamedTextColor.DARK_AQUA);
        addItems();
        miniGameInventory = new HostMiniGameInventory(plugin);
        uhcInventory = new HostUhcInventory(plugin);
        plugin.getInventoryListeners().registerInventories(miniGameInventory, uhcInventory);
    }

    private void addItems() {
        inventory.setItem(3, miniGameItem);
        inventory.setItem(5, uhcItem);
    }

    private ItemStack createItem(Material material, String displayName, NamedTextColor color) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(isSword(material)) setSwordAttributeModifiers(item, meta);
        meta.displayName(Component.text(displayName).color(color).decoration(TextDecoration.ITALIC,false));
        item.setItemMeta(meta);
        return item;
    }

    @Override
    protected void handleValidClick(InventoryClickEvent event) {
        if(event.getCurrentItem().equals(miniGameItem)) {
            inventory.close();
            miniGameInventory.open((Player) event.getWhoClicked());
        } else if (event.getCurrentItem().equals(uhcItem)) {
            inventory.close();
            uhcInventory.open((Player) event.getWhoClicked());
        }
    }
}
