package xyz.hynse.foliaflow;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getScheduler;

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
    private final Map<Entity, Vector> velocitiesMap = new HashMap<>();
    private ScheduledTask task;
    private ScheduledTask blockktask;
    private BlockDisplay display;
    @Override
    public void onEnable() {
        super.onEnable();
        try {
            RegionScheduler schedulerblock = getServer().getRegionScheduler();
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
        try {
            AsyncScheduler scheduler = getServer().getAsyncScheduler();
            task = scheduler.runAtFixedRate(this, (scheduledTask) -> getScheduler().runTask(this, () -> {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity.getType() == EntityType.FALLING_BLOCK && entity.getWorld().getEnvironment() == World.Environment.THE_END) {
                            Location centerLoc = new Location(entity.getWorld(), 100, 48.5, 0);
                            Location loc = entity.getLocation();
                            if (loc.distance(centerLoc) <= 1) {
                                if (!velocitiesMap.containsKey(entity)) {
                                    int index = counter % 4;
                                    counter++;
                                    Vector velocity = velocities[index];
                                    entity.setVelocity(velocity);
                                    velocitiesMap.put(entity, velocity);
                                    movingBlocks.add(entity.getLocation());
                                }
                            }
                        }
                    }
                }
            }), 0L, 1L, TimeUnit.MILLISECONDS);
        } catch (UnsupportedOperationException ignored) {
        }

        Location loc = new Location(Bukkit.getWorld("world_the_end"), 100, 48, 0);
        BlockData blockData = Material.OBSIDIAN.createBlockData();
        display = Objects.requireNonNull(Bukkit.getWorld("world_the_end")).spawn(loc, BlockDisplay.class);
        display.setBlock(blockData);


        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (display != null) {
            display.remove();
            display = null;
        }
        for (Entity entity : Objects.requireNonNull(Bukkit.getWorld("world_the_end")).getEntitiesByClass(BlockDisplay.class)) {
            entity.remove();
        }
        task.cancel();
        blockktask.cancel();
        super.onDisable();
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