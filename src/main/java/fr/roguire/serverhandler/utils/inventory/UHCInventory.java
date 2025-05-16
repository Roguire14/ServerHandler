package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.models.HostedServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Set;

public class UHCInventory extends CustomInventoryRefreshable{

    public UHCInventory(ServerHandler plugin) {
        super(plugin,9, Component.text("UHC")
            .color(NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD));
    }

    @Override
    protected void addNewServer(HostedServer server) {
        serversInInventory.add(server);
        inventory.addItem(server.block());
    }

    @Override
    protected Set<HostedServer> getActiveServers() {
        return plugin.getServers().getPvpServers();
    }
}
