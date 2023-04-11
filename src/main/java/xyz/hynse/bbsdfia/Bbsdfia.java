package xyz.hynse.bbsdfia;

import org.bukkit.Bukkit;
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
    public void onFallingBlockToBlock(EntityChangeBlockEvent e){
        if(e.getEntityType() == EntityType.FALLING_BLOCK){
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            Vector vel = entity.getVelocity();
            Block movingTo = getBlockMovingTo(loc, vel);
            Location loc2 = new Location(Bukkit.getWorld("world_the_end"), 100, 50, 0);

            if(movingTo != null && movingTo.getType() == Material.END_PORTAL){
                /*
                World world = Bukkit.getServer().getWorld("world");
                Location spawnLoc = new Location(world, 100, 50, 0);
                */
                Bukkit.getAsyncScheduler().runNow(this, task -> {
                    FallingBlock dummy = loc2.getWorld().spawnFallingBlock(loc2, ((FallingBlock) entity).getBlockData());
                    Vector dummyVel = vel.clone();
                    dummyVel.setY(-dummyVel.getY());
                    dummyVel.multiply(new Vector(2, 2, 2)); // double the velocity

                    // add a constant downward velocity to simulate gravity
                    dummyVel.add(new Vector(0, 0.3, 0));

                    dummy.setVelocity(dummyVel);
                });
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
