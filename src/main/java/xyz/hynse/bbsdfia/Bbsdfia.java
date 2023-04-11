package xyz.hynse.bbsdfia;


import com.tcoded.folialib.FoliaLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
            World end = Bukkit.getServer().getWorld("world_the_end");
            Location location = new Location(end, 100, 5, 0);
            FoliaLib foliaLib = new FoliaLib(this);

            if (movingTo != null && movingTo.getType() == Material.END_PORTAL) {
                // Debug message
                getLogger().info("Falling block detected near end portal");

                //Entity Scheduler Task
                //foliaLib.getImpl().runAtEntity(entity, () -> {
                // Debug message
                getLogger().info("Spawning falling block in the end dimension");

                // Get the material and data of the original falling block
                Material material = ((FallingBlock) entity).getBlockData().getMaterial();
                @NotNull BlockData data = ((FallingBlock) entity).getBlockData();

                // Spawn a new falling block in the end dimension with the same material and data
                FallingBlock dummy = end.spawnFallingBlock(location, Bukkit.createBlockData(material, (Consumer<BlockData>) data));
                Vector dummyVel = vel.clone();
                dummy.setVelocity(dummyVel);

                // Debug message
                getLogger().info("Setting velocity for the dummy falling block");

                // Set the velocity of the dummy falling block
                dummy.setVelocity(new Vector(0, 1, 0));
                //dummy.setVelocity(new Vector(0, -1, 0));  // Uncomment this line if you want the block to fall downwards
                //dummy.setVelocity(new Vector(1, 0, 0));   // Uncomment these lines if you want the block to move horizontally
                //dummy.setVelocity(new Vector(-1, 0, 0));
                //});
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