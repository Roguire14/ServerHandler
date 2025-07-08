package fr.roguire.serverhandler.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.models.HostedServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BungeeListener implements PluginMessageListener {
    private final ServerHandler serverHandler;

    public BungeeListener(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if(!channel.equals("BungeeCord")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String content = in.readUTF();
        if(content.equals("GetServers")) readServers(in);
    }

    private void readServers(ByteArrayDataInput in) {
        String[] server = in.readUTF().split(", ");
        Set<HostedServer> pvpServers = new HashSet<>();
        Set<HostedServer> minigameServers = new HashSet<>();
        for(String s : server) {
            if(s.startsWith("minecraft_pvp")){
                pvpServers.add(HostedServer.fromCsv(s));
            }
            else if(s.startsWith("minecraft_minigame")){
                minigameServers.add(HostedServer.fromCsv(s));
            }
        }
        serverHandler.getServers().refreshServers(pvpServers, minigameServers);
    }
}
