package fr.roguire.serverhandler.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.roguire.serverhandler.ServerHandler;
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
        if(!content.equals("GetServers")) return;
        String[] server = in.readUTF().split(", ");
        Set<String> uhcServers = new HashSet<>();
        Set<String> miniGameServers = new HashSet<>();
        for (String s : server) {
            if(s.startsWith("minecraft-pvp"))
                uhcServers.add(s);
            else if (s.startsWith("minecraft-minigame")) {
                miniGameServers.add(s);
            }
        }

        serverHandler.getUHCServers().addAll(uhcServers);
        serverHandler.getMiniGameServers().addAll(miniGameServers);

        serverHandler.getUHCServers().removeIf(serverName -> !uhcServers.contains(serverName));
        serverHandler.getMiniGameServers().removeIf(miniGameServer -> !miniGameServers.contains(miniGameServer));
    }
}
