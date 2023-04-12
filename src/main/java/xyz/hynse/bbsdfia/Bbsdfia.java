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

            if(movingTo != null && movingTo.getType() == Material.END_PORTAL){
                Location spawnLoc = movingTo.getLocation();
                spawnLoc.setX(spawnLoc.getX()+0.5);
                spawnLoc.setY(spawnLoc.getY()+0.5);
                spawnLoc.setZ(spawnLoc.getZ()+0.5);

                FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                Vector dummyVel = vel.clone();
                dummyVel.setY(-dummyVel.getY());
                dummyVel.multiply(new Vector(2, 2, 2));

                dummyVel.add(new Vector(0, -0.2, 0));

                dummy.setVelocity(dummyVel);
            }
        }
    }
    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock fallingBlock) {
            if (fallingBlock.getWorld().getEnvironment() == World.Environment.THE_END && event.getTo() != Material.AIR) {
                Location blockLocation = fallingBlock.getLocation();
                if (isInCube(blockLocation, 101, 49, 1, 99, 51, -1)) {
                    if (fallingBlock.getFallDistance() > 0) {
                        Vector velocity;
                        if (fallingBlock.getVelocity().getX() == 0 && fallingBlock.getVelocity().getZ() == 0) {
                            velocity = new Vector(-1, 0, 0);
                        } else if (fallingBlock.getVelocity().getX() < 0 && fallingBlock.getVelocity().getZ() == 0) {
                            velocity = new Vector(0, 0, -1);
                        } else if (fallingBlock.getVelocity().getX() < 0 && fallingBlock.getVelocity().getZ() < 0) {
                            velocity = new Vector(1, 0, 0);
                        } else {
                            velocity = new Vector(0, 0, 1);
                        }
                        fallingBlock.setVelocity(velocity.multiply(0.5));
                    }
                }
            }
        }
    }

    private boolean isInCube(Location location, int x1, int y1, int z1, int x2, int y2, int z2) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        return location.getBlockX() >= minX && location.getBlockX() <= maxX
                && location.getBlockY() >= minY && location.getBlockY() <= maxY
                && location.getBlockZ() >= minZ && location.getBlockZ() <= maxZ;
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