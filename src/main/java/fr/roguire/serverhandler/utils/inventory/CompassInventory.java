package fr.roguire.serverhandler.utils.inventory;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static fr.roguire.serverhandler.utils.UsefullFunctions.*;

public class CompassInventory extends CustomInventoryBordered {

    private final String survivalServerName = "Serveur survie";
    private final ServerHandler plugin;

    private final CustomInventory adminInventory;
    private final CustomInventory miniGameInventory;
    private final CustomInventory uhcInventory;

    public CompassInventory(ServerHandler plugin, CustomInventory adminInventory, CustomInventory miniGameInventory, CustomInventory uhcInventory) {
        super(45, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Component.text("Téléporteur")
            .color(NamedTextColor.DARK_PURPLE)
            .decorate(TextDecoration.BOLD));
        this.plugin = plugin;
        this.adminInventory = adminInventory;
        this.miniGameInventory = miniGameInventory;
        this.uhcInventory = uhcInventory;
        addServers();
    }

    private void addServers() {
        inventory.setItem(22, createSurvivalServerItem());
        if(plugin.getMiniGameServers().isEmpty()) inventory.setItem(20, createMiniGamesItem());
        if(plugin.getUHCServers().isEmpty()) inventory.setItem(24, createUHCItem());
    }

    private ItemStack createUHCItem() {
        return createCustomItem(Material.DIAMOND_SWORD, uhcInventory.displayName);
    }

    private ItemStack createMiniGamesItem() {
        return createCustomItem(Material.NETHER_STAR, miniGameInventory.displayName);
    }

    private ItemStack createAdminItem() {
        return createCustomItem(Material.COMMAND_BLOCK, adminInventory.displayName, false);
    }

    private ItemStack createSurvivalServerItem() {
        return createCustomItem(Material.GRASS_BLOCK, survivalServerName, NamedTextColor.DARK_GREEN, true);
    }

    private ItemStack createCustomItem(Material material, Component name){
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(name.decoration(TextDecoration.ITALIC, false));
        if(isSword(material)) setSwordAttributeModifiers(item, itemMeta);
        else item.setItemMeta(itemMeta);
        return item;
    }

    private ItemStack createCustomItem(Material material, Component name, boolean bold){
        Component component = name.decoration(TextDecoration.BOLD, bold).decoration(TextDecoration.ITALIC, false);
        return createCustomItem(material, component);
    }

    private ItemStack createCustomItem(Material material, String blockName, TextColor color, boolean bold) {
        return createCustomItem(material, Component.text(blockName).color(color), bold);
    }

    @Override
    protected void handleValidClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if(getDisplayName(item.displayName()).equals(survivalServerName)) {
            sendToSurvivalServer(event);
        }else if (getDisplayName(item.displayName()).equals(adminInventory.getInventoryName())) {
            inventory.close();
            adminInventory.open(player);
        }else if (getDisplayName(item.displayName()).equals(miniGameInventory.getInventoryName())) {
            inventory.close();
            miniGameInventory.open(player);
        }else if(getDisplayName(item.displayName()).equals(uhcInventory.getInventoryName())) {
            inventory.close();
            uhcInventory.open(player);
        }
    }

    @Override
    public void open(Player player) {
        if(player.hasPermission("serverhandler.host")) inventory.setItem(8, createAdminItem());
        else inventory.setItem(8, borderItem);
        super.open(player);
    }

    private void sendToSurvivalServer(InventoryClickEvent event) {
        plugin.sendToServer((Player)event.getWhoClicked(), "survie");
    }

}
