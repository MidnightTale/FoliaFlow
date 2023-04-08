package xyz.hynse.bbsdfia;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class Bbsdfia extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getLogger().info("Bbsdfia plugin started");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getServer().getLogger().info("Bbsdfia plugin stopped");
    }

    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent e) {
        if (e.getEntityType() == EntityType.FALLING_BLOCK) {
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            Vector vel = entity.getVelocity();
            Block movingTo = getBlockMovingTo(loc, vel);

            if (movingTo != null && movingTo.getType() == Material.END_PORTAL) {
                Location obsidianPlatformCenter = new Location(movingTo.getWorld(), 100, 50, 0); // change coordinates to the center of the obsidian platform in the end dimension
                Location spawnLoc = obsidianPlatformCenter.clone().add(0, 1, 0); // spawn the new falling block entity one block above the obsidian platform


                // Schedule the logic to be run asynchronously after 1 tick
                getServer().getAsyncScheduler().runNow(this, scheduledTask -> new BukkitRunnable() {
                    @Override
                    public void run() {
                        FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                        Vector dummyVel = vel.clone();
                        dummyVel.setY(-dummyVel.getY());
                        dummyVel.multiply(new Vector(2, 2, 2)); // double the velocity

                        // add a constant downward velocity to simulate gravity
                        dummyVel.add(new Vector(0, 0.3, 0));

                        dummy.setVelocity(dummyVel);
                    }
                }.runTask(this));


            }
        }
    }




                Block getBlockMovingTo(Location loc, Vector vel){
        double absMax = 0, max = 0;
        char dir = ' ';
        Block relative = null;
        if(Math.abs(vel.getX()) > absMax){
            max = vel.getX();
            absMax = Math.abs(vel.getX());
            dir = 'x';
        }
        if(Math.abs(vel.getY()) > absMax){
            max = vel.getY();
            absMax = Math.abs(vel.getY());
            dir = 'y';
        }
        if(Math.abs(vel.getZ()) > absMax){
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
