package fr.roguire.serverhandler.utils.models;

import com.google.gson.JsonObject;
import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static fr.roguire.serverhandler.utils.UsefullFunctions.isSword;
import static fr.roguire.serverhandler.utils.UsefullFunctions.setSwordAttributeModifiers;

public record HostedServer(String name, String category, String host, ItemStack block, String serverName) {

    public static HostedServer fromCsv(String in) {
        String[] parts = in.split("_");
        String type = String.join("_", Arrays.copyOfRange(parts, 2, parts.length - 2));
        String itemName = String.join(" ", type);
        String host = parts[parts.length - 1];
        String category = parts[1];
        AtomicReference<ItemStack> item = new AtomicReference<>();
        ServerHandler.getInstance().getApiCommunicator().sendGetRequest("config/get-config/" + category + "/" + itemName)
            .thenAccept(response -> {
                if (response == null) return;
                if (response.get("status").getAsInt() != 200) return;
                JsonObject config = response.get("message").getAsJsonObject();
                String materialName = config.get("block").getAsString().replace("Material.", "");
                item.set(new ItemStack(Material.valueOf(materialName)));
            }).join();
        ItemStack itemStack = item.get();
        ItemMeta meta = itemStack.getItemMeta();
        meta.lore(List.of(
            Component.text("Host: ", NamedTextColor.AQUA)
                .decorate(TextDecoration.BOLD)
                .append(
                    Component.text(host, NamedTextColor.GOLD)
                        .decorate(TextDecoration.ITALIC).decoration(TextDecoration.BOLD, false)
                )
        ));
        if (isSword(itemStack)) setSwordAttributeModifiers(itemStack, meta);
        meta.displayName(Component.text(itemName.toUpperCase()));
        meta.getPersistentDataContainer()
                .set(new NamespacedKey(ServerHandler.getInstance(),"server_name"),
            PersistentDataType.STRING,
            in);
        itemStack.setItemMeta(meta);
        return new HostedServer(itemName, category, host, itemStack, in);
    }

    @Override
    public String toString() {
        return "HostedServer{" +
            "name='" + name + '\'' +
            ", category='" + category + '\'' +
            ", host='" + host + '\'' +
            ", block=" + block + '\'' +
            ", serverName='" + serverName +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof HostedServer other)) return false;
        return this.serverName.equals(other.serverName);
    }
}
