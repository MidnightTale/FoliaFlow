package xyz.hynse.foliaflow;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.bukkit.Bukkit.getScheduler;

public class FoliaFlow extends JavaPlugin implements Listener {
    private final Vector velocity1 = new Vector(0, 0.5, -1);
    private final Vector velocity2 = new Vector(-1, 0.5, 0);
    private final Vector velocity3 = new Vector(0, 0.5, 1);
    private final Vector velocity4 = new Vector(1, 0.5, 0);
    private final Vector[] velocities = {velocity1, velocity2, velocity3, velocity4};
    private int counter = 0;
    private final Set<Location> movingBlocks = new HashSet<>();
    private final Map<Entity, Vector> velocitiesMap = new HashMap<>(); // Create a map to store velocities
    private ScheduledTask task;

    private ScheduledTask blockktask;

    @Override
    public void onEnable() {
        super.onEnable();
        // Get the region scheduler for the server
        RegionScheduler schedulerblock = getServer().getRegionScheduler();

        // Schedule a repeating task to run every tick using runAtFixedRate() method
        blockktask = schedulerblock.runAtFixedRate(this, Bukkit.getWorld("world_the_end"), 1, 1, (schedulerTask) -> {
            Block block = Bukkit.getWorld("world_the_end").getBlockAt(100, 48, 0);
            if (block.getType() == Material.OBSIDIAN) {
                block.setType(Material.AIR);
            }
        }, 0L, 1L);
        AsyncScheduler scheduler = getServer().getAsyncScheduler();
        task = scheduler.runAtFixedRate(this, (scheduledTask) -> getScheduler().runTask(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity.getType() == EntityType.FALLING_BLOCK && entity.getWorld().getEnvironment() == World.Environment.THE_END) {
                        //Location loc = entity.getLocation();
                        //debug("Falling block spawned at location " + loc);

                        // Set the initial velocity of the falling block only if it doesn't have a velocity stored
                        if (!velocitiesMap.containsKey(entity)) {
                            int index = counter % 4;
                            counter++;
                            Vector velocity = velocities[index];
                            entity.setVelocity(velocity);
                            velocitiesMap.put(entity, velocity); // Store the velocity in the map
                            movingBlocks.add(entity.getLocation()); // Add the location to the set
                        }
                    }
                }
            }
        }), 0L, 1L, TimeUnit.MILLISECONDS);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getConsoleSender().sendMessage("");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "    ______________             ");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "   / ____/ ____/ /___ _      __");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "  / /_  / /_  / / __ \\ | /| / /");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + " / __/ / __/ / / /_/ / |/ |/ / ");
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "/_/   /_/   /_/\\____/|__/|__/  ");
        getServer().getConsoleSender().sendMessage("");
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "Plugin started successfully!");
        getServer().getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        task.isCancelled();
        blockktask.isCancelled();
        super.onDisable();
        debug("Plugin stopped successfully!");
    }


    /*
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Block obsidianBlock = obsidianLocation.getBlock();
        if (event.getWorld() == endWorld && obsidianBlock.getType() == Material.OBSIDIAN) { // check if the chunk is in the end and the block is obsidian
            obsidianBlock.setType(Material.AIR); // set the block to air
        }
    }

    @EventHandler
    public void onBlockChange(org.bukkit.event.block.BlockFromToEvent event) {
        if (event.getBlock().getLocation().equals(obsidianLocation)) { // check if the block change event is for the obsidian block location
            event.setCancelled(true); // cancel the event to prevent the obsidian block from changing
        }
    }*/

    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent e){
        if(e.getEntityType() == EntityType.FALLING_BLOCK){
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            Vector vel = entity.getVelocity();
            Block movingTo = getBlockMovingTo(loc, vel);

            if(movingTo != null && movingTo.getType() == Material.END_PORTAL){
                Location spawnLoc = movingTo.getLocation();
                spawnLoc.setX(spawnLoc.getX()+0.5);
                spawnLoc.setY(spawnLoc.getY()-0.25);
                spawnLoc.setZ(spawnLoc.getZ()+0.5);

                FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                dummy.setDropItem(false);
                dummy.setHurtEntities(false);
                dummy.setGravity(true);
                Vector dummyVel = vel.clone();
                dummyVel.setY(-dummyVel.getY());
                dummyVel.multiply(new Vector(2, 1, 2));

                dummyVel.add(new Vector(0, 1, 0));

                dummy.setVelocity(dummyVel);
            }
        }
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
        //debug("Moving falling block from location " + loc.toString() + " to location " + dir);
        return relative;
    }

    private void debug(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[FoliaFlow] " + message);
    }
}