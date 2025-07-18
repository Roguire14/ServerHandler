package fr.roguire.serverhandler.utils.items;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.listener.inventory.InventoryListeners;
import fr.roguire.serverhandler.utils.inventory.CompassInventory;
import fr.roguire.serverhandler.utils.inventory.CustomInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TeleporterCompass extends CustomItem {

    private final CustomInventory inventory;
    private final CustomInventory hostInventory;
    private final ServerHandler plugin;

    public TeleporterCompass(ServerHandler plugin, CustomInventory adminInventory, CustomInventory miniGameInventory, CustomInventory uhcInventory) {
        this.plugin = plugin;
        inventory = new CompassInventory(plugin, adminInventory, miniGameInventory, uhcInventory);
        hostInventory = new CompassInventory(plugin, adminInventory, miniGameInventory, uhcInventory, true);
    }

    @Override
    protected ItemStack createItem() {
        ItemStack itemStack = ItemStack.of(Material.COMPASS);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text("Téléporteur", NamedTextColor.LIGHT_PURPLE));
        itemMeta.setEnchantmentGlintOverride(true);
        itemMeta.lore(Arrays.asList(
            Component.text("Pour pouvoir se téléporter entre les serveurs"),
            Component.text("Clique droit ou gauche pour se déplacer!"))
        );
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public CustomInventory getNormalInventory() {
        return inventory;
    }

    public CustomInventory getHostInventory() {
        return hostInventory;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("serverhandler.host") || player.isOp()) {
            hostInventory.open(player);
            return;
        }
        inventory.open(player);
    }

}
