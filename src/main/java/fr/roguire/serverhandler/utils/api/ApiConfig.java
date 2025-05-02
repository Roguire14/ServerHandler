package fr.roguire.serverhandler.utils.api;

public class ApiConfig {

    private static String fullAddress;
    private static ApiConfig instance;

    private ApiConfig(String address, int port) {
        fullAddress = address + ":" + port;
    }

    public static String getFullAddress() {
        return fullAddress;
    }

    public static void initializeApiConfig(String address, int port) {
        instance = new ApiConfig(address, port);
    }
}
