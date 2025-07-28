package voxelware.CatEssentials;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SlashHub implements CommandExecutor {
    private final JavaPlugin plugin;

    public SlashHub(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfig().getBoolean("enable-hub-command", true)) {
            sender.sendMessage("The /hub command is disabled by the server administrator.");
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        String mode = plugin.getConfig().getString("hub-mode", "world");

        switch (mode.toLowerCase()) {
            case "proxy":
                String server = plugin.getConfig().getString("hub-server", "lobby");
                player.sendMessage("Sending you to " + server + "...");
                // You must register the plugin messaging channel in onEnable for this to work!
                player.sendPluginMessage(plugin, "BungeeCord", ("Connect " + server).getBytes());
                break;
            case "transfer":
                String transferServer = plugin.getConfig().getString("hub-server", "lobby");
                player.sendMessage("Transferring you to " + transferServer + "...");
                player.transfer(transferServer); // Paper API 1.20+
                break;
            case "world":
            default:
                String worldName = plugin.getConfig().getString("hub-world", "hub");
                List<?> coords = plugin.getConfig().getList("hub-coords");
                double x = 0, y = 100, z = 0;
                if (coords != null && coords.size() == 3) {
                    x = ((Number) coords.get(0)).doubleValue();
                    y = ((Number) coords.get(1)).doubleValue();
                    z = ((Number) coords.get(2)).doubleValue();
                }
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    player.sendMessage("Hub world not found!");
                    return true;
                }
                player.teleport(world.getSpawnLocation().clone().add(x, y - world.getSpawnLocation().getY(), z));
                player.sendMessage("Teleported to the hub!");
                break;
        }
        return true;
    }
}
