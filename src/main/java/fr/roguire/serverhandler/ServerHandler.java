package fr.roguire.serverhandler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.roguire.serverhandler.listener.BungeeListener;
import fr.roguire.serverhandler.listener.CustomItemManager;
import fr.roguire.serverhandler.listener.inventory.CompassInventoryListener;
import fr.roguire.serverhandler.listener.inventory.InventoryListeners;
import fr.roguire.serverhandler.listener.items.CompassListener;
import fr.roguire.serverhandler.listener.player.OnJoinEvent;
import fr.roguire.serverhandler.utils.BungeeCordCommunicator;
import fr.roguire.serverhandler.utils.EventsUtil;
import fr.roguire.serverhandler.utils.inventory.*;
import fr.roguire.serverhandler.utils.items.TeleporterCompass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static fr.roguire.serverhandler.utils.UsefullFonctions.getDisplayName;

public final class ServerHandler extends JavaPlugin {

    private final Set<String> miniGameServers;
    private final Set<String> miniUHCServers;
    private final BungeeCordCommunicator bungeeCordCommunicator;

    public ServerHandler() {
        miniGameServers = new HashSet<>();
        miniUHCServers = new HashSet<>();
        bungeeCordCommunicator = new BungeeCordCommunicator(this);
    }

    @Override
    public void onEnable() {
        CustomInventory adminInventory = new AdminInventory(this);
        CustomInventory miniGameInventory = new MiniGameInventory(this);
        CustomInventory uhcInventory = new UHCInventory(this);

        TeleporterCompass compass = new TeleporterCompass(this, adminInventory, miniGameInventory, uhcInventory);

        CustomItemManager.registerItem(getDisplayName(compass.getItem().displayName()), compass);

        InventoryListeners inventoryListeners = new InventoryListeners(this);
        inventoryListeners.registerInventories(adminInventory, miniGameInventory, uhcInventory);
        EventsUtil.registerListener(inventoryListeners, this);

        EventsUtil.registerListeners(this,
            new CustomItemManager(),
            new OnJoinEvent(compass),
            new CompassListener(compass),
            new CompassInventoryListener(compass.getCustomInventory())
        );

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));

        getConfig().options().copyDefaults(true);
//        getCommand("start").setExecutor(new StartCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Set<String> getMiniGameServers() {
        return miniGameServers;
    }

    public Set<String> getUHCServers() {
        return miniUHCServers;
    }

    public void setMiniGameServers(Set<String> miniGameServers) {
        this.miniGameServers.clear();
        this.miniGameServers.addAll(miniGameServers);
    }

    public void setUHCServers(Set<String> miniUHCServers) {
        this.miniUHCServers.clear();
        this.miniUHCServers.addAll(miniUHCServers);
    }

    public void addMiniGameServer(String server) {
        miniGameServers.add(server);
    }

    public void addUHCServer(String server) {
        miniUHCServers.add(server);
    }

    public void removeMiniGameServer(String server) {
        miniGameServers.remove(server);
    }

    public void removeUHCServer(String server) {
        miniUHCServers.remove(server);
    }

    public BungeeCordCommunicator getBungeeCordCommunicator() {
        return bungeeCordCommunicator;
    }

    public void sendToServer(Player player, String serverName){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
    }
}
