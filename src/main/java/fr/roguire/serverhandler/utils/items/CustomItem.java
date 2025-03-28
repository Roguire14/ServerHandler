package fr.roguire.serverhandler.utils.items;

import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomItem {
    private final ItemStack item;

    public CustomItem() {
        this.item = createItem();
    }

    protected abstract ItemStack createItem();

    public ItemStack getItem() {
        return item;
    }

    public abstract void onInteract(final PlayerInteractEvent event);
}