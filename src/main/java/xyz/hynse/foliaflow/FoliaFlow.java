package xyz.hynse.foliaflow;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
        super.onDisable();
        debug("Plugin stopped successfully!");
    }


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
                spawnLoc.setY(spawnLoc.getY()-0.1);
                spawnLoc.setZ(spawnLoc.getZ()+0.5);

                FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                dummy.setDropItem(false);
                dummy.setHurtEntities(false);
                dummy.setGravity(true);
                Vector dummyVel = vel.clone();
                dummyVel.setY(-dummyVel.getY());
                dummyVel.multiply(new Vector(2, 1, 2));

                dummyVel.add(new Vector(0, 0.2, 0));

                dummy.setVelocity(dummyVel);
            }
        }
    }


    @EventHandler
    public void onFallingBlockSpawn(EntitySpawnEvent e) {
        if (e.getEntityType() == EntityType.FALLING_BLOCK && e.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();

            debug("Falling block spawned at location " + loc);

            // Set the initial velocity of the falling block
            int index = counter % 4;
            counter ++;
            Vector velocity = velocities[index];
            entity.setVelocity(velocity);

            // Remove the block from the movingBlocks set after a delay, to prevent it from being immediately moved again
            getServer().getScheduler().runTaskLater(this, () -> movingBlocks.remove(loc.getBlock().getLocation()), 20L);
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
        debug("Moving falling block from location " + loc.toString() + " to location " + dir);
        return relative;
    }

    private void debug(String message) {
        getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[FoliaFlow] " + message);
    }
}