package voxelware.CatEssentials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CatEssentialsCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final String[] authors = {"membercatcousin", "WatermanMC"}; // If you forked the plugin, add your name here

    public CatEssentialsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String version = plugin.getDescription().getVersion();
        String authorsList = String.join(", ", authors);
        sender.sendMessage("CatEssentials version " + version);
        sender.sendMessage("Authors: " + authorsList);
        return true;
    }
}
