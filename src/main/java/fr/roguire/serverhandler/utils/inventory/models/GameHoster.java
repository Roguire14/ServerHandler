package fr.roguire.serverhandler.utils.inventory.models;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.api.ApiCommunicator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class GameHoster {

    private final ServerHandler plugin;
    private final ApiCommunicator communicator;
    private final String category;

    public GameHoster(String category){
        this.category = category;
        this.plugin = ServerHandler.getInstance();
        this.communicator = new ApiCommunicator();
    }

    public void hostGame(HostItem hostItem, Player player){
        String type = hostItem.type();
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("category", category);
        jsonBody.addProperty("type", type);
        jsonBody.addProperty("host", player.getName());

        communicator.sendPostRequest("start-server", jsonBody)
            .thenAccept(jsonResponse -> {
                if(jsonResponse == null) return;
                System.out.println(jsonResponse);
                int statusCode = jsonResponse.get("status").getAsInt();
                Object answer;
                try{
                    answer = jsonResponse.get("message").getAsString();
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
                String message = (String)answer;
                switch (statusCode){
                    case 200:
                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            plugin.sendToServer(player, JsonParser.parseString(message).getAsJsonObject().get("name").getAsString());
                        }, 5 * 20L);
                        player.sendMessage(Component.text("Serveur en cours de création.. Connexion dans un instant, veuillez patienter").color(NamedTextColor.GREEN));
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
}
