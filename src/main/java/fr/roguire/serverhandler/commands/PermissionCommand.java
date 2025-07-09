package fr.roguire.serverhandler.commands;

import fr.roguire.serverhandler.ServerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionCommand implements CommandExecutor, TabCompleter {
    private final ServerHandler plugin;

    public PermissionCommand(ServerHandler serverHandler) {
        this.plugin = serverHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(Component.text("You do not have permission to use this command!").color(NamedTextColor.DARK_RED));
            return true;
        }
        if (args.length != 3) {
            return false;
        }
        Player target = plugin.getServer().getPlayer(args[1]);
        assert target != null;
        String perm = args[2];

        switch (args[0]) {
            case "add" -> addPermission(sender, target, perm);
            case "remove" -> removePermission(sender, target, perm);
        }
        return true;
    }

    private void addPermission(CommandSender sender, Player target, String perm){
        plugin.addPermission(target.getUniqueId(), perm);
        sender.sendMessage(Component.text("Permission ajoutée ("+perm+") à "+target.getName()).color(NamedTextColor.GREEN));
    }

    private void removePermission(CommandSender sender, Player target, String perm){
        plugin.removePermission(target.getUniqueId(), perm);
        sender.sendMessage(Component.text("Permission retirée ("+perm+") à "+target.getName()).color(NamedTextColor.GREEN));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1){
            return List.of("add", "remove");
        } else if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[1]))
                .collect(Collectors.toList());
        } else if (args.length == 3) {
            return List.of("serverhandler.host");
        }

        return Collections.emptyList();
    }
}
