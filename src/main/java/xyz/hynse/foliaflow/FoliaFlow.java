package xyz.hynse.foliaflow;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.hynse.foliaflow.command.ReloadCommand;
import xyz.hynse.foliaflow.watcher.PortalWatcher;

public class FoliaFlow extends JavaPlugin {

    public static FoliaFlow instance;
    public static double horizontalCoefficient;
    public static double verticalCoefficient;

    @Override
    public void onEnable() {
        instance = this;
        register();
        reload();
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();
        horizontalCoefficient = getConfig().getDouble("horizontal_coefficient");
        verticalCoefficient = getConfig().getDouble("vertical_coefficient");
    }

    private void register() {
        getCommand("flowreload").setExecutor(new ReloadCommand());
        getServer().getPluginManager().registerEvents(new PortalWatcher(), this);
    }
}
