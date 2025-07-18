package fr.roguire.serverhandler.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    private static final Dotenv dotenv = Dotenv.load();

    public static String getKey(String key) {
        return dotenv.get(key);
    }
}
