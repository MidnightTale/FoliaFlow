package xyz.hynse.foliaflow;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.hynse.foliaflow.watcher.PortalWatcher;

public class FoliaFlow extends JavaPlugin {

    public static FoliaFlow instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new PortalWatcher(), this);
    }
}
