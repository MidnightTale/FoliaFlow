package xyz.hynse.bbsdfia;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Bbsdfia extends JavaPlugin implements Listener {

    // This method is called when the plugin is enabled
    @Override
    public void onEnable() {
        super.onEnable();
        // Get a reference to the world_the_end world

        // Register the plugin to listen to events
        getServer().getPluginManager().registerEvents(this, this);
        // Log that the plugin has started
        getServer().getLogger().info("Bbsdfia plugin started");
    }

    // This method is called when the plugin is disabled
    @Override
    public void onDisable() {
        super.onDisable();
        // Log that the plugin has stopped
        getServer().getLogger().info("Bbsdfia plugin stopped");
    }

    // This method is called when a falling block entity changes its block
    // Register the method as an event handler for the EntityChangeBlockEvent
    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent event) {
        // Get the block that was changed
        Block block = event.getBlock();

        // Check if it was changed from a falling block
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            // Spawn a new falling block at the same location as the old one
            World world = Bukkit.getWorld("world_the_end"); // specify the world to spawn in
            FallingBlock newBlock = Objects.requireNonNull(world).spawnFallingBlock(block.getLocation(), ((FallingBlock) event.getEntity()).getBlockData());

            // Set the same velocity as the old block
            newBlock.setVelocity(event.getEntity().getVelocity());
            // Set the same custom name as the old block
            newBlock.setCustomName(event.getEntity().getCustomName());
        }
    }

}