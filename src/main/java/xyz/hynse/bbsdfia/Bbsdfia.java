package xyz.hynse.bbsdfia;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class Bbsdfia extends JavaPlugin implements Listener {
    private final Map<Entity, Vector> teleportedFallingBlocks = new HashMap<>();

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

            if(movingTo != null && movingTo.getType() == Material.END_PORTAL){
                Location spawnLoc = movingTo.getLocation();
                spawnLoc.setX(spawnLoc.getX()+0.5);
                spawnLoc.setY(spawnLoc.getY()+0.5);
                spawnLoc.setZ(spawnLoc.getZ()+0.5);

                FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());

                // Store the velocity of the falling block entity
                teleportedFallingBlocks.put(dummy, vel.clone());

                // Set the velocity of the dummy entity to 0 to prevent it from moving
                dummy.setVelocity(new Vector(0, 0, 0));
            }
        }
    }

    @EventHandler
    public void onEntityTeleport(EntityTeleportEvent e) {
        Entity entity = e.getEntity();
        if (entity.getType() == EntityType.FALLING_BLOCK && e.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            // Check if the falling block entity has been teleported through the end portal
            if (teleportedFallingBlocks.containsKey(entity)) {
                Vector vel = teleportedFallingBlocks.remove(entity);
                entity.setVelocity(vel);
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
