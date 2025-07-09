package fr.roguire.serverhandler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.roguire.serverhandler.commands.PermissionCommand;
import fr.roguire.serverhandler.listener.BungeeListener;
import fr.roguire.serverhandler.listener.CustomItemManager;
import fr.roguire.serverhandler.listener.inventory.CompassInventoryListener;
import fr.roguire.serverhandler.listener.inventory.InventoryListeners;
import fr.roguire.serverhandler.listener.items.CompassListener;
import fr.roguire.serverhandler.listener.player.OnJoinEvent;
import fr.roguire.serverhandler.utils.BungeeCordCommunicator;
import fr.roguire.serverhandler.utils.EventsUtil;
import fr.roguire.serverhandler.utils.RunningServers;
import fr.roguire.serverhandler.utils.api.ApiCommunicator;
import fr.roguire.serverhandler.utils.api.ApiConfig;
import fr.roguire.serverhandler.utils.inventory.AdminInventory;
import fr.roguire.serverhandler.utils.inventory.CustomInventory;
import fr.roguire.serverhandler.utils.inventory.MiniGameInventory;
import fr.roguire.serverhandler.utils.inventory.UHCInventory;
import fr.roguire.serverhandler.utils.items.TeleporterCompass;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static fr.roguire.serverhandler.utils.UsefullFunctions.getDisplayName;

public final class ServerHandler extends JavaPlugin {
    private final BungeeCordCommunicator bungeeCordCommunicator;
    private final InventoryListeners inventoryListeners;
    private static ServerHandler instance;
    private ApiCommunicator apiCommunicator;
    private final RunningServers servers;
    private final Map<UUID, Set<String>> playerPermissions = new HashMap<>();
    private FileConfiguration permissionsConfig;

    public ServerHandler() {
        bungeeCordCommunicator = new BungeeCordCommunicator(this);
        inventoryListeners = new InventoryListeners(this);
        servers = new RunningServers(this);
    }

    @Override
    public void onEnable() {
        loadPermissions();

        instance = this;
        ApiConfig.initializeApiConfig("http://host.docker.internal",25550);
        apiCommunicator = new ApiCommunicator();
        apiCommunicator.loginToApi();

        CustomInventory adminInventory = new AdminInventory(this);
        CustomInventory miniGameInventory = new MiniGameInventory(this);
        CustomInventory uhcInventory = new UHCInventory(this);

        TeleporterCompass compass = new TeleporterCompass(this, adminInventory, miniGameInventory, uhcInventory);

        CustomItemManager.registerItem(getDisplayName(compass.getItem().displayName()), compass);

        inventoryListeners.registerInventories(adminInventory, miniGameInventory, uhcInventory);
        EventsUtil.registerListener(inventoryListeners, this);

        EventsUtil.registerListeners(this,
            new CustomItemManager(),
            new OnJoinEvent(this, compass),
            new CompassListener(compass),
            new CompassInventoryListener(compass.getNormalInventory(), compass.getHostInventory())
        );

        PermissionCommand permissionCommand = new PermissionCommand(this);
        getCommand("permission").setExecutor(permissionCommand);
        getCommand("permission").setTabCompleter(permissionCommand);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));

        getConfig().options().copyDefaults(true);
//        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        instance = null;
        Bukkit.getScheduler().cancelTasks(this);
        savePermissions();
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

    public ApiCommunicator getApiCommunicator() {
        return apiCommunicator;
    }

    public RunningServers getServers() {
        return servers;
    }

    public Set<String> getPermissions(UUID uuid){
        return playerPermissions.getOrDefault(uuid, Set.of());
    }

    public void loadPermissions() {
        File file = new File(getDataFolder(), "permissions.yml");
        if (!file.exists()) {
            try{
                if(!getDataFolder().exists()) {
                    getDataFolder().mkdirs();
                }
                FileWriter writer = new FileWriter(file);
                writer.write("# permissions:\n");
                writer.write("#  \"069a79f4-44e9-4726-a5be-fca90e38aaf5\":\n");
                writer.write("#    - serverhandler.host\n");
                writer.write("permissions: {}\n");
                writer.close();
                getLogger().info("permissions.yml vide créé");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        permissionsConfig = YamlConfiguration.loadConfiguration(file);
        for(String key: permissionsConfig.getConfigurationSection("permissions").getKeys(false)){
            try{
                UUID uuid = UUID.fromString(key);
                List<String> perms = permissionsConfig.getStringList("permissions." + key);
                playerPermissions.put(uuid, new HashSet<>(perms));
            } catch (IllegalArgumentException ex){
                getLogger().warning("UUID invalide : "+key);
            }
        }
    }

    public void addPermission(UUID uuid, String permission){
        playerPermissions.putIfAbsent(uuid, new HashSet<>());
        Player player =  Bukkit.getPlayer(uuid);
        if(player == null) return;
        Set<String> perms = playerPermissions.get(uuid);
        if(perms.add(permission)){
            player.addAttachment(this, permission, true);
            getLogger().info("Permission ajoutée: " +  permission + " pour " + uuid);
            savePermissions();
        }
    }

    public void removePermission(UUID uuid, String permission){
        Set<String> perms = playerPermissions.get(uuid);
        if(perms == null || !perms.remove(permission)) return;
        Player player = Bukkit.getPlayer(uuid);
        if(player != null) {
            player.addAttachment(this, permission, false);
            getLogger().info("Permission retirée: " +   permission + " pour " + uuid);
        }
        if(perms.isEmpty()) playerPermissions.remove(uuid);

        savePermissions();
    }

    public void savePermissions() {
        File file = new  File(getDataFolder(), "permissions.yml");
        YamlConfiguration config = new YamlConfiguration();
        for(Map.Entry<UUID, Set<String>> entry: playerPermissions.entrySet()){
            config.set("permissions." + entry.getKey().toString(), new ArrayList<>(entry.getValue()));
        }
        try{
            config.save(file);
        } catch (IOException e) {
            getLogger().severe("Erreur lors de la sauvegarde des permissions : "+e.getMessage());
        }
    }

}
