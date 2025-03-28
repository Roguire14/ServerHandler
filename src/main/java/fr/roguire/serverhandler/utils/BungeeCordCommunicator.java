package fr.roguire.serverhandler.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.roguire.serverhandler.ServerHandler;

public class BungeeCordCommunicator {

    private final ServerHandler serverHandler;

    public BungeeCordCommunicator(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    public void refreshServer(){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServers");
        serverHandler.getServer().sendPluginMessage(serverHandler, "BungeeCord", out.toByteArray());
    }

}
