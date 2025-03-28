package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MiniGameInventory extends CustomInventory{

    public MiniGameInventory(ServerHandler plugin) {
        super(9, Component.text("Mini-Jeux")
            .color(NamedTextColor.AQUA)
            .decorate(TextDecoration.BOLD));
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null) return;
        if(item.getItemMeta() == null) return;
        if(event.getInventory().equals(getInventory())){
            System.out.println("ouaaais un clique");
        }
    }
}
