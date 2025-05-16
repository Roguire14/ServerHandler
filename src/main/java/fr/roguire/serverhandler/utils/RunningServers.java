package fr.roguire.serverhandler.utils;

import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.models.HostedServer;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class RunningServers {

    private final ServerHandler server;
    private final Set<HostedServer> minigameServers;
    private final Set<HostedServer> pvpServers;
    private final Set<HostedServer> specialServers;

    public RunningServers(ServerHandler server) {
        this.server = server;
        specialServers = new HashSet<>();
        pvpServers = new HashSet<>();
        minigameServers = new HashSet<>();
    }

    public void refreshServers(Set<HostedServer> pvpServers, Set<HostedServer> minigameServers){
        this.pvpServers.clear();
        this.pvpServers.addAll(pvpServers);

        this.minigameServers.clear();
        this.minigameServers.addAll(minigameServers);
    }

    public Set<HostedServer> getMinigameServers() {
        return minigameServers;
    }

    public Set<HostedServer> getPvpServers() {
        return pvpServers;
    }

    public Set<HostedServer> getSpecialServers() {
        return specialServers;
    }
}
