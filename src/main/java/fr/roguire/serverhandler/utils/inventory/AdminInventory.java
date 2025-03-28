package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminInventory extends CustomInventoryBordered {

    private final ServerHandler plugin;

    public AdminInventory(ServerHandler plugin) {
        super(27, Material.RED_STAINED_GLASS_PANE, Component.text("Admin").color(NamedTextColor.RED).decorate(TextDecoration.BOLD));
        this.plugin = plugin;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {

    }
}
