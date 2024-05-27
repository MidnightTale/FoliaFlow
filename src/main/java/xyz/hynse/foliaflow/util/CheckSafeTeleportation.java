package xyz.hynse.foliaflow.util;

import org.yaml.snakeyaml.Yaml;
import xyz.hynse.foliaflow.FoliaFlow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class CheckSafeTeleportation {

    public boolean checkUnsafeEndPortalTeleportation() {
        File configFile = new File(getServer().getWorldContainer(), "config/paper-global.yml");
        if (!configFile.exists()) {
            FoliaFlow.instance.getLogger().warning("paper-global.yml not found!");
            return false;
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(fis);

            Map<String, Object> unsupportedSettings = (Map<String, Object>) config.get("unsupported-settings");
            if (unsupportedSettings != null) {
                Boolean setting = (Boolean) unsupportedSettings.get("allow-unsafe-end-portal-teleportation");
                return setting != null && setting;
            }
        } catch (IOException e) {
            FoliaFlow.instance.getLogger().severe("Failed to read paper-global.yml: " + e.getMessage());
        }

        return false;
    }


//    public void updateSafeTeleportation() {
//        boolean safeTeleportation = checkUnsafeEndPortalTeleportation();
//        if (safeTeleportation) {
//            FoliaFlow.instance.isUnSafeTeleport = true;
//            FoliaFlow.instance.getLogger().info("Unsafe End Portal Teleportation is true.");
//        } else {
//            FoliaFlow.instance.isUnSafeTeleport = false;
//            FoliaFlow.instance.getLogger().info("Unsafe End Portal Teleportation is false.");
//        }
//    }
}
