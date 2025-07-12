package fr.roguire.serverhandler.utils.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.roguire.serverhandler.ServerHandler;
import fr.roguire.serverhandler.utils.Env;
import org.bukkit.Bukkit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiCommunicator {
    private final String fullAddress;
    private final HttpClient client;
    private final Gson gson;
    private final AtomicBoolean reauthInProgress = new AtomicBoolean(false);
    private final Logger logger;
    private String token;

    public ApiCommunicator() {
        this.fullAddress = ApiConfig.getFullAddress();
        gson = new Gson();
        client = HttpClient.newHttpClient();
        logger = Bukkit.getLogger();
        token = "";
    }

    public CompletableFuture<JsonObject> sendPostRequest(String endPoint, JsonObject body){
        HttpRequest request = buildPostRequest(endPoint, body);
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenCompose(this::handleResponse);
    }

    public CompletableFuture<JsonObject> sendGetRequest(String endPoint){
        HttpRequest request = buildGetRequest(endPoint);
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenCompose(this::handleResponse);
    }

    private CompletableFuture<JsonObject> handleResponse(HttpResponse<String> response) {
        String body = response.body();
        return switch (response.statusCode()) {
            case 200, 400, 404 -> CompletableFuture.completedFuture(gson.fromJson(body, JsonObject.class));
            case 401 -> handleUnauthorized(response);
            default -> {
                logger.log(Level.SEVERE, "Erreur API (" + response.statusCode() + "): " + body);
                yield null;
            }
        };
    }

    private CompletableFuture<JsonObject> handleUnauthorized(HttpResponse<String> response) {
        logger.log(Level.WARNING, "401 - Unauthorized: "+response.body());
        if(!reauthInProgress.compareAndSet(false, true)){
            return CompletableFuture.completedFuture(createRetryLaterResponse());
        }
        return loginToApi()
            .thenApply(v -> createRetryLaterResponse())
            .whenComplete((r, ex) -> reauthInProgress.set(false));
    }

    private JsonObject createRetryLaterResponse() {
        Map<String, String> response = new HashMap<>();
        response.put("message","Send the request again");
        response.put("status", "409");
        return gson.toJsonTree(response).getAsJsonObject();
    }

    private HttpRequest buildGetRequest(String endpoint){
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(this.fullAddress +"/" + endpoint))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .GET();
        if(token != null && !token.isEmpty()){
            builder.header("Authorization", "Bearer "+token);
        }
        return builder.build();
    }

    private HttpRequest buildPostRequest(String endPoint, JsonObject body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(this.fullAddress+"/"+endPoint))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)));
        if(token != null && !token.isEmpty()){
            builder.header("Authorization", "Bearer "+token);
        }
        return builder.build();
    }

    public CompletableFuture<Void> loginToApi(){
        Map<String, String> body = new HashMap<>();
        body.put("username", Env.getKey("API_USERNAME"));
        body.put("password", Env.getKey("API_PASSWORD"));
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(this.fullAddress+"/login"))
            .headers("Content-Type", "application/json", "Accept", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
            .build();
        logger.log(Level.INFO, "Login to API");
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                int statusCode = response.statusCode();
                if(statusCode == 200){
                    JsonObject responseBody = gson.fromJson(response.body(), JsonObject.class);
                    token = responseBody.get("token").getAsString();
                    logger.log(Level.INFO, "Successfully logged in");
                }else if(statusCode == 401){
                    throw new RuntimeException("Wrong logging");
                }else{
                    throw new RuntimeException("Why the fuck am I being displayed");
                }
            }).exceptionally(e->{
                logger.log(Level.SEVERE, "Error logging in", e);
                return null;
            });
    }
}
