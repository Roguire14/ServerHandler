package fr.roguire.serverhandler.utils.inventory;

import com.google.gson.JsonObject;
import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.api.ApiCommunicator;
import fr.roguire.serverhandler.utils.inventory.models.GameHoster;
import fr.roguire.serverhandler.utils.inventory.models.HostItem;
import fr.roguire.serverhandler.utils.items.ItemCreator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static fr.roguire.serverhandler.utils.UsefullFunctions.getDisplayName;

public class HostMiniGameInventory extends CustomInventory implements HostInventory {

    private final ServerHandler plugin;
    private final Map<String, HostItem> items;
    private final ApiCommunicator communicator;
    private final GameHoster host;

    public HostMiniGameInventory(ServerHandler plugin) {
        super(9, Component.text("Mini-jeux à hoster")
            .color(NamedTextColor.DARK_GREEN)
            .decorate(TextDecoration.BOLD));
        this.plugin = plugin;
        this.communicator = new ApiCommunicator();
        items = new HashMap<>();
        host = new GameHoster("minigame");
        fillHostItems();
    }

    @Override
    public void fillHostItems() {
        fetchConfig("minigame", inventory, items, communicator);
    }

    @Override
    protected void handleValidClick(InventoryClickEvent event) {
        String blockName = getDisplayName(event.getCurrentItem().displayName());
        host.hostGame(items.get(blockName.toLowerCase()), (Player) event.getWhoClicked());
    }

}
