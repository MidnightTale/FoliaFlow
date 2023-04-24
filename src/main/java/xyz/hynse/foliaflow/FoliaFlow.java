package xyz.hynse.foliaflow;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public class FoliaFlow extends JavaPlugin implements Listener {
    private final double vh = 0.2;
    private final double vt = 0.8;
    private final Vector velocity1 = new Vector(0, vh, vt);
    private final Vector velocity2 = new Vector(vt, vh, 0);
    private final Vector velocity3 = new Vector(0, vh, -vt);
    private final Vector velocity4 = new Vector(-vt, vh, 0);

    private final Vector[] velocities = {velocity1, velocity2, velocity3, velocity4};
    private int counter = 0;
    private final Set<Location> movingBlocks = new HashSet<>();
    private final Map<Entity, Vector> velocitiesMap = new HashMap<>(); // Create a map to store velocities
    private ScheduledTask blockktask;


    @Override
    public void onEnable() {
        super.onEnable();

        // Get the region scheduler for the server
        try {
            RegionScheduler schedulerblock = getServer().getRegionScheduler();

            // Schedule a repeating task to run every tick using runAtFixedRate() method
            blockktask = schedulerblock.runAtFixedRate(this, Objects.requireNonNull(Bukkit.getWorld("world_the_end")), 1, 1, (schedulerTask) -> {
                Block block = Objects.requireNonNull(Bukkit.getWorld("world_the_end")).getBlockAt(100, 48, 0);
                if (block.getType() == Material.OBSIDIAN) {
                    block.setType(Material.COBBLED_DEEPSLATE_SLAB);
                    Slab slab = (Slab) block.getBlockData();
                    slab.setType(Slab.Type.BOTTOM);
                    block.setBlockData(slab);
                }
            }, 1L, 1L);
        } catch (NullPointerException block) {
            getServer().getLogger().info("Region Scheduler erorr (likly chunky it not load)");
        }
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
        blockktask.cancel();
        super.onDisable();
    }

//    @EventHandler
//    public void onFallingBlockLand(EntityChangeBlockEvent event) {
//        Entity entity = event.getEntity();
//        if (entity instanceof FallingBlock && entity.getWorld().getEnvironment() == World.Environment.THE_END) {
//            getLogger().info("found falling block" + entity);
//            Location centerLoc = new Location(entity.getWorld(), 100, 48.5, 0);
//            Location loc = entity.getLocation();
//            if (loc.distance(centerLoc) <= 1) {
//                if (!velocitiesMap.containsKey(entity)) {
//                    getLogger().info("add velocity falling block" + entity);
//                    int index = counter % 4;
//                    getLogger().info("[1]" + entity);
//                    counter++;
//                    getLogger().info("[2]" + entity);
//                    Vector velocity = velocities[index];
//                    getLogger().info("[3]" + entity);
//                    entity.setVelocity(velocity);
//                    getLogger().info("[4]" + entity);
//                    velocitiesMap.put(entity, velocity); // Store the velocity in the map
//                    getLogger().info("[5]" + entity);
//                    movingBlocks.add(entity.getLocation()); // Add the location to the set
//                    getLogger().info("[6]" + entity);
//                }
//            }
//        }
//    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        // Check if the entity is a falling block and has landed in the End dimension
        Entity entity = event.getEntity();
        if (entity instanceof FallingBlock && entity.getWorld().getEnvironment() == World.Environment.THE_END) {
            // Spawn a new falling block entity with velocity to the north and upward
            World world = entity.getWorld();
            Location location = entity.getLocation();
            Vector velocity = new Vector(0, 1.2, -6);
            Material material = ((FallingBlock) entity).getBlockData().getMaterial();
            byte data = ((FallingBlock) entity).getBlockData().getAsString().getBytes()[0];
            FallingBlock newFallingBlock = world.spawnFallingBlock(location, material, data);
            newFallingBlock.setVelocity(velocity);

            // Remove the original falling block entity
            entity.remove();
        }
    }




    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent e){
        if(e.getEntityType() == EntityType.FALLING_BLOCK){
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            Vector vel = entity.getVelocity();
            Block movingTo = getBlockMovingTo(loc, vel);
            try {

            if(movingTo != null && movingTo.getType() == Material.END_PORTAL){
                Location spawnLoc = movingTo.getLocation();
                spawnLoc.setX(spawnLoc.getX()+0.5);
                spawnLoc.setY(spawnLoc.getY()-0.25);
                spawnLoc.setZ(spawnLoc.getZ()+0.5);

                FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                dummy.setDropItem(true);
                dummy.setHurtEntities(true);
                dummy.setGravity(true);
                Vector dummyVel = vel.clone();
                dummyVel.setY(-dummyVel.getY());
                dummyVel.multiply(new Vector(2, 1, 2));

                dummyVel.add(new Vector(0, 1, 0));

                dummy.setVelocity(dummyVel);
            }
            } catch (NullPointerException dummy) {
                getServer().getLogger().info("onFallingBlockToBlock erorr (likly chunky it not load)");
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
        return relative;
    }
}