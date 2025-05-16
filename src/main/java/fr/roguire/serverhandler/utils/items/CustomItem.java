package fr.roguire.serverhandler.utils.items;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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