package xyz.hynse.foliaflow;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FoliaFlow extends JavaPlugin implements Listener {
    private final Vector velocity1 = new Vector(0, 0.5, -1);
    private final Vector velocity2 = new Vector(-1, 0.5, 0);
    private final Vector velocity3 = new Vector(0, 0.5, 1);
    private final Vector velocity4 = new Vector(1, 0.5, 0);
    private final Vector[] velocities = {velocity1, velocity2, velocity3, velocity4};
    private int counter = 0;
    private final Set<Location> movingBlocks = new HashSet<>();



    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        debug("Plugin started successfully!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        debug("Plugin stopped successfully!");
    }


    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock)) {
            return;
        }
        if (entity.getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }
        debug("Falling block spawned in the end at location " + entity.getLocation());
        Location loc = entity.getLocation();
        Vector vel = entity.getVelocity();
        Block movingTo = getBlockMovingTo(loc, vel);

        if (movingTo != null && movingTo.getType() == Material.END_PORTAL) {
            Location spawnLoc = movingTo.getLocation();
            spawnLoc.setX(spawnLoc.getX() + 0.5);
            spawnLoc.setY(spawnLoc.getY() + 0.5);
            spawnLoc.setZ(spawnLoc.getZ() + 0.5);

            debug("Creating dummy falling block at location " + spawnLoc + " with velocity " + vel);
            FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
            Vector dummyVel = vel.clone();
            dummyVel.setY(-dummyVel.getY());
            dummyVel.multiply(new Vector(2, 2, 2));

            dummyVel.add(new Vector(0, -0.2, 0));

            dummy.setVelocity(dummyVel);
            entity.remove();
        }
    }


    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock)) {
            return;
        }
        if (entity.getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }
        Location blockLoc = event.getBlock().getLocation();
        if (movingBlocks.contains(blockLoc)) {
            return;
        }
        debug("EntityChangeBlock event received for falling block at location " + entity.getLocation());
        entity.remove();
        movingBlocks.add(blockLoc);
        Location block = new Location(Bukkit.getWorld("world_the_end"), 100, 49, 0);
        block.getBlock().setType(Material.AIR);
        World world = entity.getWorld();
        Location location = entity.getLocation();
        byte data = ((FallingBlock) entity).getBlockData().getAsString().getBytes()[0];
        Material material = ((FallingBlock) entity).getBlockData().getMaterial();

        int index = counter % 4;
        Vector velocity = velocities[index];
        counter++;
        debug("Spawning new falling block of material " + material + " at location " + location + " with velocity " + velocity.toString());
        FallingBlock newFallingBlock = world.spawnFallingBlock(location, material, data);
        newFallingBlock.setVelocity(velocity);

        // Remove the block from the movingBlocks set after a delay, to prevent it from being immediately moved again
        getServer().getScheduler().runTaskLater(this, () -> movingBlocks.remove(blockLoc), 20L);
    }


    Block getBlockMovingTo(Location loc, Vector vel) {
        double absMax = 0, max = 0;
        char dir = ' ';
        Block relative = null;
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
        switch (dir) {
            case 'x' -> relative = loc.getBlock().getRelative((int) Math.signum(max), 0, 0);
            case 'y' -> relative = loc.getBlock().getRelative(0, (int) Math.signum(max), 0);
            case 'z' -> relative = loc.getBlock().getRelative(0, 0, (int) Math.signum(max));
        }
        debug("Moving falling block from location " + loc.toString() + " to location " + dir);
        return relative;
    }

    private void debug(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[FoliaFlow] " + message);
    }
}