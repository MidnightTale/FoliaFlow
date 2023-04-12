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
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import static org.bukkit.Bukkit.getWorld;

public class Bbsdfia extends JavaPlugin implements Listener {
    private final Vector velocity1 = new Vector(0, 0.5, -1);
    private final Vector velocity2 = new Vector(-1, 0.5, 0);
    private final Vector velocity3 = new Vector(0, 0.5, 1);
    private final Vector velocity4 = new Vector(1, 0.5, 0);
    private final Vector[] velocities = { velocity1, velocity2, velocity3, velocity4 };
    private int counter = 0;


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
    public void setBlockInEndDimension(Location location) {
        if (!location.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            // The location is not in the End dimension
            return;
        }

        Block block = location.getBlock();
        if (block.getType() != Material.AIR) {
            // The block is not already air
            block.setType(Material.AIR);
        }
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
                Location location = new Location(getWorld("world_the_end"), 100, 49, 0);

                String commandString = String.format("/execute in minecraft:the_end run setblock %d %d %d air", location.getBlockX(), location.getBlockY(), location.getBlockZ());

                Bukkit.getScheduler().runTaskAsynchronously(this, () -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandString));
            }
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

        // Spawn a new falling block entity with velocity

        World world = entity.getWorld();
        Location location = entity.getLocation();
        byte data = ((FallingBlock) entity).getBlockData().getAsString().getBytes()[0];
        Material material = ((FallingBlock) entity).getBlockData().getMaterial();
        Location locationblock = new Location(world, 100,49 ,0);
        setBlockInEndDimension(locationblock);

        int index = counter % 4;
        Vector velocity = velocities[index];
        counter++;

        FallingBlock newFallingBlock = world.spawnFallingBlock(location, material, data);
        newFallingBlock.setVelocity(velocity);
        entity.remove();
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