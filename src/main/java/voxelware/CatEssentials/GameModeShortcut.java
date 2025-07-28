package voxelware.CatEssentials;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameModeShortcut implements CommandExecutor {
    private final GameMode mode;

    public GameModeShortcut(GameMode mode) {
        this.mode = mode;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        player.setGameMode(mode);
        player.sendMessage("Your gamemode has been set to " + mode.name().toLowerCase() + ".");
        return true;
    }
}
