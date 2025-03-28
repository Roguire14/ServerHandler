package fr.roguire.serverhandler.utils;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class EventsUtil {
    public static void registerListener(Listener listener, Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    public static void registerListeners(Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners)
            registerListener(listener, plugin);
    }

    public static void unregisterEvent(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
