package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.api.ApiCommunicator;
import fr.roguire.serverhandler.utils.inventory.models.GameHoster;
import fr.roguire.serverhandler.utils.inventory.models.HostItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;

import static fr.roguire.serverhandler.utils.UsefullFunctions.getDisplayName;

public class HostUhcInventory extends CustomInventory implements HostInventory {
    private final ServerHandler plugin;
    private final ApiCommunicator communicator;
    private final GameHoster host;
    private final Map<String, HostItem> items;

    public HostUhcInventory(ServerHandler plugin) {
        super(9, Component.text("UHC à hoster")
            .color(NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD));
        this.plugin = plugin;
        communicator = new ApiCommunicator();
        host = new GameHoster("pvp");
        items = new HashMap<>();
        fillHostItems();
    }

    @Override
    public void fillHostItems() {
        fetchConfig("pvp", inventory, items, communicator);
    }

    @Override
    protected void handleValidClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (Boolean.TRUE.equals(GameHoster.isPlayerGenerating.get(player))) {
            player.sendMessage(Component.text("Vous avez déjà un serveur en cours de création /!\\").color(NamedTextColor.RED));
            return;
        }
        String blockName = getDisplayName(event.getCurrentItem().displayName());
        host.hostGame(items.get(blockName.toLowerCase()), (Player) event.getWhoClicked());
    }
}
