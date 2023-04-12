package xyz.hynse.bbsdfia;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Bbsdfia extends JavaPlugin implements Listener {
    private int counter = 0;
    private Material detectedMaterial = null;
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
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        // Check if the entity is a falling block and has landed in the End dimension
        Entity entity = event.getEntity();
        if (entity instanceof FallingBlock && entity.getWorld().getEnvironment() == World.Environment.THE_END) {
            // Spawn a new falling block entity with velocity to the north and upward
            World world = entity.getWorld();
            Location location = entity.getLocation();
            Material material = ((FallingBlock) entity).getBlockData().getMaterial();
            World worldend = Bukkit.getWorld("world_the_end");
            Location locationblock = new Location(worldend, 100, 49, 0);
            Block block = locationblock.getBlock();
            byte data = ((FallingBlock) entity).getBlockData().getAsString().getBytes()[0];
            Vector velocity = switch (counter % 4) {
                case 0 -> new Vector(0, 0.5, -1);
                case 1 -> new Vector(-1, 0.5, 0);
                case 2 -> new Vector(0, 0.5, 1);
                default -> new Vector(1, 0.5, 0);
            };
            counter++;

            FallingBlock newFallingBlock = world.spawnFallingBlock(location, material, data);
            newFallingBlock.setVelocity(velocity);

            // Remove the original block
            block.setType(Material.AIR);
        }
    }
    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        Location location = item.getLocation();

        // Check if item is in End dimension at (100, 49, 0)
        if (location.getWorld().getEnvironment() == World.Environment.THE_END &&
                location.getBlockX() == 100 && location.getBlockY() == 49 && location.getBlockZ() == 0) {

            // Store material type of detected item
            detectedMaterial = item.getItemStack().getType();
            // Create block data for falling block entity
            byte data = ((FallingBlock) item).getBlockData().getAsString().getBytes()[0];
            Vector velocity = switch (counter % 4) {
                case 0 -> new Vector(0, 0.5, -1);
                case 1 -> new Vector(-1, 0.5, 0);
                case 2 -> new Vector(0, 0.5, 1);
                default -> new Vector(1, 0.5, 0);
            };
            counter++;

            FallingBlock newFallingBlock = item.getWorld().spawnFallingBlock(location, detectedMaterial, data);
            newFallingBlock.setVelocity(velocity);

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