package fr.roguire.serverhandler.utils.inventory;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import static fr.roguire.serverhandler.utils.UsefullFonctions.getDisplayName;

public abstract class CustomInventory {

    private final String inventoryName;
    protected Inventory inventory;
    protected final Component displayName;

    public CustomInventory(int size, Component component) {
        if(size % 9 != 0) throw new IllegalArgumentException("size must be a multiple of 9");
        if(size>54 || size<9) throw new IllegalArgumentException("size must be between 9 and 54");
        this.inventory = createInventory(size, component);
        inventoryName = getDisplayName(component);
        displayName = component;
    }

    private Inventory createInventory(int size, Component component) {
        return Bukkit.createInventory(null, size, component);
    }

    public void recreateInventory(int size) {
        inventory = createInventory(size, displayName);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public abstract void handleClick(InventoryClickEvent event);

    public void handleClosing(InventoryCloseEvent event) {}

    public String getInventoryName() {
        return inventoryName;
    }
}
