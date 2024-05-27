package xyz.hynse.foliaflow;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.hynse.foliaflow.command.ReloadCommand;
import xyz.hynse.foliaflow.util.CheckSafeTeleportation;
import xyz.hynse.foliaflow.watcher.PortalWatcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FoliaFlow extends JavaPlugin {
    private boolean isFirstEnable = true;
    //public boolean isUnSafeTeleport = false;
    public static FoliaFlow instance;
    public static double horizontalCoefficient;
    public static double verticalCoefficient;
    public static double spawnHeight;
    public static double TeleportOffset;
    //public static CheckSafeTeleportation checkSafeTeleportation;

    @Override
    public void onEnable() {
        instance = this;
        register();
        if (isFirstEnable) {
            isFirstEnable = false;
            copyDefaultConfig();
            setInitialConfigValues();
        }
        reload();
    }

    private void copyDefaultConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
    }

    private void setInitialConfigValues() {
        File configFile = new File(getDataFolder(), "config.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultConfigStream = getResource("config.yml");
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfigStream));
        config.options().copyDefaults(true);
        config.setDefaults(defaultConfig);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();
        horizontalCoefficient = getConfig().getDouble("horizontal_coefficient");
        verticalCoefficient = getConfig().getDouble("vertical_coefficient");
        spawnHeight = getConfig().getDouble("spawn_height");
        TeleportOffset = getConfig().getDouble("teleport_offset");
    }

    private void register() {
        getCommand("flowreload").setExecutor(new ReloadCommand());
//        checkSafeTeleportation = new CheckSafeTeleportation();
//        checkSafeTeleportation.updateSafeTeleportation();
        getServer().getPluginManager().registerEvents(new PortalWatcher(), this);
    }
}
