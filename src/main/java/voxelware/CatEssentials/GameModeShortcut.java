package voxelware.CatEssentials;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collections;
import java.util.List;

public class GameModeShortcut implements CommandExecutor, org.bukkit.command.TabCompleter {
    private final Plugin plugin;
    private final GameMode mode;
    private final String permissionBase;

    public GameModeShortcut(Plugin plugin, GameMode mode) {
        this.plugin = plugin;
        this.mode = mode;
        this.permissionBase = "catessentials.gamemode." + mode.name().toLowerCase();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Console changing other players' gamemode
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(Component.text("Usage: /" + label + " <player>", NamedTextColor.RED));
            return true;
        }

        // Change own gamemode
        if (args.length == 0) {
            if (!(sender instanceof Player player)) return false;
            
            if (!checkPermission(sender, permissionBase)) return true;
            
            changeGameMode(player, player);
            return true;
        }

        // Change other player's gamemode
        if (!checkPermission(sender, permissionBase + ".others")) return true;
        
        Player target = plugin.getServer().getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize(
                plugin.getConfig().getString("messages.player-not-found", "<red>Player not found!")
            ));
            return true;
        }

        changeGameMode(sender, target);
        return true;
    }

    private void changeGameMode(CommandSender sender, Player target) {
        target.setGameMode(mode);
        
        Component message = Component.text()
            .append(Component.text("Gamemode set to ", NamedTextColor.GREEN))
            .append(Component.text(mode.name().toLowerCase(), NamedTextColor.YELLOW))
            .build();
        
        if (sender.equals(target)) {
            target.sendMessage(message);
        } else {
            target.sendMessage(message.append(
                Component.text(" by ", NamedTextColor.GREEN)
                .append(Component.text(sender.getName(), NamedTextColor.YELLOW))
            ));
            sender.sendMessage(Component.text()
                .append(Component.text("Set ", NamedTextColor.GREEN))
                .append(Component.text(target.getName(), NamedTextColor.YELLOW))
                .append(Component.text("'s gamemode to ", NamedTextColor.GREEN))
                .append(Component.text(mode.name().toLowerCase(), NamedTextColor.YELLOW))
                .build());
        }
    }

    private boolean checkPermission(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        
        sender.sendMessage(plugin.getMiniMessage().deserialize(
            plugin.getConfig().getString("messages.no-permission", "<red>No permission!")
        ));
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1 && sender.hasPermission(permissionBase + ".others")) {
            return null; // Default player list
        }
        return Collections.emptyList();
    }
}
