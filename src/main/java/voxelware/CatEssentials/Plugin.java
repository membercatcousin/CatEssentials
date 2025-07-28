package voxelware.CatEssentials;

import org.bukkit.GameMode;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);

        // Register /hub command executor
        getCommand("hub").setExecutor(new SlashHub(this));

        // Register BungeeCord plugin messaging channel for proxy support
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getCommand("catessentials").setExecutor(new CatEssentialsCommand(this));
        getCommand("gmc").setExecutor(new GameModeShortcut(GameMode.CREATIVE));
        getCommand("gms").setExecutor(new GameModeShortcut(GameMode.SURVIVAL));
        getCommand("gma").setExecutor(new GameModeShortcut(GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new GameModeShortcut(GameMode.SPECTATOR));
        getLogger().info("CatEssentials enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean showWelcome = getConfig().getBoolean("show-welcome-message", true);
        boolean preferAuthMe = getConfig().getBoolean("prefer-authme-messages", true);
        boolean authMePresent = getServer().getPluginManager().isPluginEnabled("AuthMe");

        if (!showWelcome) return;
        if (preferAuthMe && authMePresent) return;

        String welcome = getConfig().getString("welcome-message", "Welcome to CatEssentials!");
        event.getPlayer().sendMessage(welcome);
    }
}

