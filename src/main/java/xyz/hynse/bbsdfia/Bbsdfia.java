package xyz.hynse.bbsdfia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

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
            FallingBlock newBlock = world.spawnFallingBlock(block.getLocation(), ((FallingBlock) event.getEntity()).getBlockData());

            // Set the same velocity as the old block
            newBlock.setVelocity(((FallingBlock) event.getEntity()).getVelocity());
            // Set the same custom name as the old block
            newBlock.setCustomName(((FallingBlock) event.getEntity()).getCustomName());
        }
    }

    // This function returns the block that the entity is moving towards
    Block getBlockMovingTo(Location loc, Vector vel) {
        // Initialize variables to keep track of the maximum absolute velocity and its direction
        double absMax = 0, max = 0;
        char dir = ' ';
        Block relative = null;

        // Check which component of the velocity vector has the highest magnitude
        if (Math.abs(vel.getX()) > absMax) {
            max = vel.getX();
            absMax = Math.abs(vel.getX());
            dir = 'x';
        }
        if (Math.abs(vel.getY()) > absMax) {
            max = vel.getY();
            absMax = Math.abs(vel.getY());
            dir = 'y';
        }
        if (Math.abs(vel.getZ()) > absMax) {
            max = vel.getZ();
            dir = 'z';
        }

        // Use the direction with the highest magnitude to determine the block the entity is moving towards
        switch (dir) {
            case 'x' -> relative = loc.getBlock().getRelative((int) Math.signum(max), 0, 0);
            case 'y' -> relative = loc.getBlock().getRelative(0, (int) Math.signum(max), 0);
            case 'z' -> relative = loc.getBlock().getRelative(0, 0, (int) Math.signum(max));
        }

        // Return the block the entity is moving towards
        return relative;
    }
}