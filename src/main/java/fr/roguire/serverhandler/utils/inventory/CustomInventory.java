package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.utils.inventory.models.GameHoster;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static fr.roguire.serverhandler.utils.UsefullFunctions.getDisplayName;
import static fr.roguire.serverhandler.utils.UsefullFunctions.isGlassPane;

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

    public void handleClick(InventoryClickEvent event){
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getItemMeta() == null || isGlassPane(item)) return;
        handleValidClick(event);
    }

    protected abstract void handleValidClick(InventoryClickEvent event);

    public void handleClosing(InventoryCloseEvent event) {}

    public String getInventoryName() {
        return inventoryName;
    }
}
