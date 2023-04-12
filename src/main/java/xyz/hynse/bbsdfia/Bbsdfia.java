package xyz.hynse.bbsdfia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
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
    public void onBlockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (isFallingBlock(block.getType())) {
            // Get the location of the block
            Location loc = block.getLocation();

            // Check if the block is within the desired area
            if (isWithinArea(loc)) {
                // Create a new falling block entity at the location of the block
                FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, block.getBlockData());

                // Set the velocity of the falling block entity
                fallingBlock.setVelocity(getRandomVelocity());

                // Remove the original block
                block.setType(Material.AIR);
            }
        }
    }

    private boolean isFallingBlock(Material material) {
        return material == Material.BLACK_CONCRETE_POWDER ||
                material == Material.BLUE_CONCRETE_POWDER ||
                material == Material.BROWN_CONCRETE_POWDER ||
                material == Material.CYAN_CONCRETE_POWDER ||
                material == Material.GRAY_CONCRETE_POWDER ||
                material == Material.GREEN_CONCRETE_POWDER ||
                material == Material.LIGHT_BLUE_CONCRETE_POWDER ||
                material == Material.LIGHT_GRAY_CONCRETE_POWDER ||
                material == Material.LIME_CONCRETE_POWDER ||
                material == Material.MAGENTA_CONCRETE_POWDER ||
                material == Material.ORANGE_CONCRETE_POWDER ||
                material == Material.PINK_CONCRETE_POWDER ||
                material == Material.PURPLE_CONCRETE_POWDER ||
                material == Material.RED_CONCRETE_POWDER ||
                material == Material.WHITE_CONCRETE_POWDER ||
                material == Material.YELLOW_CONCRETE_POWDER ||
                material == Material.DRAGON_EGG ||
                material == Material.GRAVEL ||
                material == Material.RED_SAND ||
                material == Material.SAND;
    }

    private boolean isWithinArea(Location loc) {
        return loc.getWorld().getName().equals("world_the_end") &&
                loc.getBlockX() >= 101 && loc.getBlockX() <= 99 &&
                loc.getBlockY() >= 49 && loc.getBlockY() <= 51 &&
                loc.getBlockZ() >= -1 && loc.getBlockZ() <= 1;
    }

    private Vector getRandomVelocity() {
        int direction = (int) (Math.random() * 4);
        return switch (direction) {
            case 0 -> new Vector(0, 1, -2);
            case 1 -> new Vector(2, 1, 0);
            case 2 -> new Vector(0, 1, 2);
            default -> new Vector(-2, 1, 0);
        };
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