package voxelware.CatEssentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatEssentialsCommand implements CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private final String[] authors = {"membercatcousin", "WatermanMC"};

    public CatEssentialsCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendPluginInfo(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                return handleReload(sender);
            case "version":
                sendVersionInfo(sender);
                return true;
            case "authors":
                sendAuthorsList(sender);
                return true;
            default:
                sendHelpMenu(sender);
                return true;
        }
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("catessentials.admin")) {
            plugin.getMiniMessage().deserialize(
                plugin.getConfig().getString("messages.no-permission", "<red>No permission!")
            );
            return true;
        }

        plugin.reloadConfig();
        plugin.getVaultManager().setupEconomy(); // Reinitialize economy if config changed
        sender.sendMessage(
            plugin.getMiniMessage().deserialize("<green>Configuration reloaded successfully!")
        );
        return true;
    }

    private void sendPluginInfo(CommandSender sender) {
        Component message = plugin.getMiniMessage().deserialize(
            """
            <gradient:#5e4fa2:#f79459>CatEssentials</gradient>
            <gray>Version: <white>{version}
            <gray>Authors: <white>{authors}
            <gray>Use <white>/ce reload <gray>to reload config
            """.replace("{version}", plugin.getDescription().getVersion())
                .replace("{authors}", String.join(", ", authors))
        );
        sender.sendMessage(message);
    }

    private void sendVersionInfo(CommandSender sender) {
        sender.sendMessage(
            plugin.getMiniMessage().deserialize(
                "<gray>Running <white>CatEssentials v" + plugin.getDescription().getVersion()
            )
        );
    }

    private void sendAuthorsList(CommandSender sender) {
        sender.sendMessage(
            plugin.getMiniMessage().deserialize(
                "<gray>Authors: <white>" + String.join(", ", authors)
            )
        );
    }

    private void sendHelpMenu(CommandSender sender) {
        Component help = plugin.getMiniMessage().deserialize(
            """
            <gradient:#5e4fa2:#f79459>CatEssentials Help</gradient>
            <gray>/ce reload <white>- Reload plugin configuration
            <gray>/ce version <white>- Show plugin version
            <gray>/ce authors <white>- List plugin authors
            """
        );
        sender.sendMessage(help);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            List<String> commands = List.of("reload", "version", "authors");
            StringUtil.copyPartialMatches(args[0], commands, completions);
            Collections.sort(completions);
            return completions;
        }
        return Collections.emptyList();
    }
}
