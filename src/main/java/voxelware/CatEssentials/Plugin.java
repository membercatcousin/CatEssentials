package voxelware.CatEssentials;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Plugin extends JavaPlugin implements Listener {
    private static Plugin instance;
    private VaultManager vaultManager;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

        // Load configuration
        saveDefaultConfig();
        upgradeConfig();
        
        // Initialize managers
        if (getConfig().getBoolean("economy.enabled", true)) {
            this.vaultManager = new VaultManager(this);
        }

        // Register events and commands
        registerEvents();
        registerCommands();
        registerChannels();

        // Metrics
        if (getConfig().getBoolean("core.metrics", true)) {
            new Metrics(this, 12345); // Replace with your bStats ID
        }

        getComponentLogger().info(
            miniMessage.deserialize(
                "<green>Enabled v<yellow>" + getDescription().getVersion() + 
                "</yellow> in <yellow>" + (System.currentTimeMillis() - startTime) + "ms</yellow>"
            )
        );
    }

    @Override
    public void onDisable() {
        getComponentLogger().info(miniMessage.deserialize("<red>CatEssentials disabled"));
    }

    private void upgradeConfig() {
        // Future config migration logic here
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void registerCommands() {
        getCommand("catessentials").setExecutor(new CatEssentialsCommand(this));
        getCommand("gmc").setExecutor(new GameModeShortcut(this, GameMode.CREATIVE));
        getCommand("gms").setExecutor(new GameModeShortcut(this, GameMode.SURVIVAL));
        getCommand("gma").setExecutor(new GameModeShortcut(this, GameMode.ADVENTURE));
        getCommand("gmsp").setExecutor(new GameModeShortcut(this, GameMode.SPECTATOR));
        getCommand("hub").setExecutor(new SlashHub(this));
    }

    private void registerChannels() {
        if (getConfig().getString("hub.mode", "world").equalsIgnoreCase("proxy")) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!getConfig().getBoolean("join.welcome-message.enabled", true)) return;
        
        boolean authMePresent = Bukkit.getPluginManager().isPluginEnabled("AuthMe");
        if (getConfig().getBoolean("join.welcome-message.prefer-authme", true) && authMePresent) return;

        String message = getConfig().getString("join.welcome-message.text", "")
            .replace("{player}", event.getPlayer().getName())
            .replace("{version}", getDescription().getVersion());
        
        event.getPlayer().sendMessage(miniMessage.deserialize(message));
    }

    // Singleton access
    public static Plugin getInstance() {
        return instance;
    }

    // Manager getters
    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}
