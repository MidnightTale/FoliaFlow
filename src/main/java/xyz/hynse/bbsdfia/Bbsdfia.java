package xyz.hynse.bbsdfia;

import com.tcoded.folialib.FoliaLib;
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

public class Bbsdfia extends JavaPlugin implements Listener {
    private final Vector velocity1 = new Vector(0, 0.5, -1);
    private final Vector velocity2 = new Vector(-1, 0.5, 0);
    private final Vector velocity3 = new Vector(0, 0.5, 1);
    private final Vector velocity4 = new Vector(1, 0.5, 0);
    private final Vector[] velocities = { velocity1, velocity2, velocity3, velocity4 };
    private int counter = 0;


/*
    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getLogger().info("Bbsdfia plugin started");
    }*/
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


    /*@EventHandler
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
    }*/

    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent e) {
        if (e.getEntityType() == EntityType.FALLING_BLOCK) {
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            Vector vel = entity.getVelocity();
            Block movingTo = getBlockMovingTo(loc, vel);
            FoliaLib foliaLib = new FoliaLib(this);

            if (movingTo != null && movingTo.getType() == Material.END_PORTAL) {
                Location spawnLoc = movingTo.getLocation();
                spawnLoc.setX(spawnLoc.getX()+0.5);
                spawnLoc.setY(spawnLoc.getY()+0.5);
                spawnLoc.setZ(spawnLoc.getZ()+0.5);

                // Spawn the first falling block immediately
                FallingBlock firstBlock = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                Vector dummyVel = vel.clone();
                dummyVel.setY(-dummyVel.getY());
                dummyVel.multiply(new Vector(2, 2, 2));
                dummyVel.add(new Vector(0, -0.2, 0));
                firstBlock.setVelocity(dummyVel);

                foliaLib.getImpl().runAtLocation(spawnLoc, () -> {
                    for (int i = 0; i < 2; i++) {
                        Location spawnLoc1 = movingTo.getLocation();
                        spawnLoc1.setX(spawnLoc1.getX() + 0.5);
                        spawnLoc1.setY(spawnLoc1.getY() + 0.5);
                        spawnLoc1.setZ(spawnLoc1.getZ() + 0.5);

                        FallingBlock fallingBlock = (FallingBlock) entity;
                        FallingBlock dummy = spawnLoc1.getWorld().spawnFallingBlock(spawnLoc1, fallingBlock.getBlockData());
                        Vector dummyVel1 = vel.clone();
                        dummyVel1.setY(-dummyVel1.getY());
                        dummyVel1.multiply(new Vector(2, 2, 2));
                        dummyVel1.add(new Vector(0, -0.2, 0));
                        dummy.setVelocity(dummyVel1);
                    }
                });
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
        entity.remove();
        Location fuck = new Location(Bukkit.getWorld("world_the_end"), 100, 49 ,0);
        fuck.getBlock().setType(Material.AIR);
        World world = entity.getWorld();
        Location location = entity.getLocation();
        byte data = ((FallingBlock) entity).getBlockData().getAsString().getBytes()[0];
        Material material = ((FallingBlock) entity).getBlockData().getMaterial();

        int index = counter % 4;
        Vector velocity = velocities[index];
        counter++;

        FallingBlock newFallingBlock = world.spawnFallingBlock(location, material, data);
        newFallingBlock.setVelocity(velocity);
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