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
import fr.roguire.serverhandler.utils.api.ApiConfig;
import fr.roguire.serverhandler.utils.inventory.AdminInventory;
import fr.roguire.serverhandler.utils.inventory.CustomInventory;
import fr.roguire.serverhandler.utils.inventory.MiniGameInventory;
import fr.roguire.serverhandler.utils.inventory.UHCInventory;
import fr.roguire.serverhandler.utils.items.TeleporterCompass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

import static fr.roguire.serverhandler.utils.UsefullFunctions.getDisplayName;

public final class ServerHandler extends JavaPlugin {

    private final Set<String> miniGameServers;
    private final Set<String> uhcServers;
    private final BungeeCordCommunicator bungeeCordCommunicator;
    private final InventoryListeners inventoryListeners;
    private static ServerHandler instance;

    public ServerHandler() {
        miniGameServers = new HashSet<>();
        uhcServers = new HashSet<>();
        bungeeCordCommunicator = new BungeeCordCommunicator(this);
        inventoryListeners = new InventoryListeners(this);
    }

    @Override
    public void onEnable() {
        instance = this;
        ApiConfig.initializeApiConfig("http://host.docker.internal",25550);

        CustomInventory adminInventory = new AdminInventory(this);
        CustomInventory miniGameInventory = new MiniGameInventory(this);
        CustomInventory uhcInventory = new UHCInventory(this);

        TeleporterCompass compass = new TeleporterCompass(this, adminInventory, miniGameInventory, uhcInventory);

        CustomItemManager.registerItem(getDisplayName(compass.getItem().displayName()), compass);

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
        instance = null;
        Bukkit.getScheduler().cancelTasks(this);
    }

    public Set<String> getMiniGameServers() {
        return miniGameServers;
    }

    public Set<String> getUHCServers() {
        return uhcServers;
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

    public InventoryListeners getInventoryListeners() {
        return inventoryListeners;
    }

    public static ServerHandler getInstance(){
        return instance;
    }
}
