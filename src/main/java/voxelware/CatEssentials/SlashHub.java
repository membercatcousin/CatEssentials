package voxelware.CatEssentials;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;

import java.util.List;
import java.util.Objects;

public class SlashHub implements CommandExecutor, TabCompleter {
    private final Plugin plugin;
    private final String permissionBase = "catessentials.hub";

    public SlashHub(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Handle console executing on players
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(plugin.getMiniMessage().deserialize(
                plugin.getConfig().getString("messages.command-usage", "<red>Usage: /hub <player>")
            ));
            return true;
        }

        // Change other player's hub
        if (args.length > 0) {
            return handleOtherPlayer(sender, args[0]);
        }

        // Handle self teleport
        Player player = (Player) sender;
        if (!checkPermission(player, permissionBase)) return true;

        Location hubLocation = getHubLocation();
        if (hubLocation == null) {
            player.sendMessage(plugin.getMiniMessage().deserialize(
                plugin.getConfig().getString("messages.hub.world-not-configured", "<red>Hub world is not configured!")
            ));
            return true;
        }

        int delay = plugin.getConfig().getInt("hub.teleport-delay", 3);
        if (delay <= 0 || player.hasPermission(permissionBase + ".instant")) {
            executeTeleport(player, hubLocation);
        } else {
            startTeleportCountdown(player, hubLocation, delay);
        }
        return true;
    }

    private boolean handleOtherPlayer(CommandSender sender, String targetName) {
        if (!checkPermission(sender, permissionBase + ".others")) return true;

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize(
                plugin.getConfig().getString("messages.player-not-found", "<red>Player not found!")
            ));
            return true;
        }

        Location hubLocation = getHubLocation();
        if (hubLocation == null) {
            sender.sendMessage(plugin.getMiniMessage().deserialize(
                plugin.getConfig().getString("messages.hub.world-not-configured", "<red>Hub world is not configured!")
            ));
            return true;
        }

        executeTeleport(target, hubLocation);
        sender.sendMessage(plugin.getMiniMessage().deserialize(
            "<green>Teleported <white>" + target.getName() + "</white> to hub!"
        ));
        return true;
    }

    private Location getHubLocation() {
        String mode = plugin.getConfig().getString("hub.mode", "world");
        
        // Handle proxy/transfer modes
        if (mode.equalsIgnoreCase("proxy") || mode.equalsIgnoreCase("transfer")) {
            return null; // Handled by BungeeCord
        }

        // Handle world mode
        String worldName = plugin.getConfig().getString("hub.world");
        List<Double> coords = plugin.getConfig().getDoubleList("hub.coordinates");
        
        if (worldName == null || coords.size() < 3) {
            return null;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }

        return new Location(
            world,
            coords.get(0),
            coords.get(1),
            coords.get(2),
            coords.size() > 3 ? coords.get(3).floatValue() : 0,
            coords.size() > 4 ? coords.get(4).floatValue() : 0
        );
    }

    private void startTeleportCountdown(Player player, Location hubLocation, int delay) {
        new BukkitRunnable() {
            int remaining = delay;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                if (remaining <= 0) {
                    executeTeleport(player, hubLocation);
                    cancel();
                    return;
                }

                // Show countdown title
                player.showTitle(Title.title(
                    Component.text(remaining, plugin.getMiniMessage().deserialize("<gradient:red:yellow>")),
                    Component.text("Teleporting to hub...", NamedTextColor.GRAY),
                    Title.Times.times(
                        Ticks.duration(5),
                        Ticks.duration(20),
                        Ticks.duration(5)
                ));

                // Play countdown sound
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);

                remaining--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void executeTeleport(Player player, Location location) {
        if (plugin.getConfig().getBoolean("hub.safe-teleport", true)) {
            location = findSafeLocation(location);
        }

        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                player.sendMessage(plugin.getMiniMessage().deserialize(
                    plugin.getConfig().getString("messages.hub.arrived", "<green>Welcome to the hub!")
                ));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
            } else {
                player.sendMessage(plugin.getMiniMessage().deserialize(
                    "<red>Teleportation failed!"
                ));
            }
        });
    }

    private Location findSafeLocation(Location location) {
        if (location.getWorld() == null) return location;
        
        Location safeLocation = location.clone();
        if (safeLocation.getBlock().getType().isSolid()) {
            safeLocation.setY(safeLocation.getWorld().getHighestBlockYAt(safeLocation) + 1);
        }
        return safeLocation;
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
        return List.of();
    }
}
