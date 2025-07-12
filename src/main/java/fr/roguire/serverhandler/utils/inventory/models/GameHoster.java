package fr.roguire.serverhandler.utils.inventory.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.api.ApiCommunicator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameHoster {

    public static final Map<Player, Boolean> isPlayerGenerating = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(GameHoster.class);
    private final ServerHandler plugin;
    private final ApiCommunicator communicator;
    private final String category;

    public GameHoster(String category) {
        this.category = category;
        this.plugin = ServerHandler.getInstance();
        this.communicator = new ApiCommunicator();
    }

    public void hostGame(HostItem hostItem, Player player) {
        String type = hostItem.type();
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("category", category);
        jsonBody.addProperty("type", type);
        jsonBody.addProperty("host", player.getName());

        communicator.sendPostRequest("start-server", jsonBody)
            .thenAccept(jsonResponse -> {
                if (jsonResponse == null) return;
                int statusCode = jsonResponse.get("status").getAsInt();
                String message = jsonResponse.get("message").getAsString();
                switch (statusCode) {
                    case 200:
                        player.sendMessage(Component.text("Serveur en cours de création.. Connexion dans un instant, veuillez patienter").color(NamedTextColor.GREEN));
                        String serverName = JsonParser.parseString(message).getAsJsonObject().get("name").getAsString();
                        createServerInDB(player, serverName);
                        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> tryToSendPlayer(player, serverName, 60), 100L);
                        break;
                    case 400:
                        player.sendMessage(Component.text(message).color(NamedTextColor.RED));
                        break;
                    case 409:
                        hostGame(hostItem, player);
                        break;
                }
            })
            .exceptionally(ex -> {
                ex.printStackTrace();
                player.sendMessage(Component.text("Une erreur est survenue :/").color(NamedTextColor.RED));
                return null;
            });
    }

    private void createServerInDB(Player player, String serverName) {
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("name", serverName);
        communicator.sendPostRequest("create-server", jsonBody)
            .thenAccept(jsonResponse -> {
                if (jsonResponse == null) return;
                int statusCode = jsonResponse.get("code").getAsInt();
                if (statusCode == 200) isPlayerGenerating.put(player, true);
            });
    }

    private void tryToSendPlayer(Player player, String serverName, int maxTries) {
        if (maxTries <= 0) return;
        if (!player.isOnline()) return;
        isServerRunning(serverName).thenAccept(isRunning -> {
            if (isRunning) {
                isPlayerGenerating.put(player, false);
                plugin.sendToServer(player, serverName);
            } else {
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                    tryToSendPlayer(player, serverName, maxTries - 1);
                });
            }
        });
    }

    private CompletableFuture<Boolean> isServerRunning(String serverName) {
        return communicator.sendGetRequest("get-server/" + serverName)
            .thenApply(json -> {
                int code = json.get("code").getAsInt();
                if (code == 200) {
                    return json.getAsJsonObject("server").get("created").getAsBoolean();
                }
                return false;
            });
    }



}
