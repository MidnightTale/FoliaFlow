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

                    //spawn new falling block in the end dimension and have same properties entity type and material same form entity that detect near end portal

                    FallingBlock dummy = end.spawnFallingBlock(location, ((FallingBlock) entity).getBlockData());
                    Vector dummyVel = vel.clone();
                    dummy.setVelocity(dummyVel);

                    // Debug message
                    getLogger().info("Setting velocity for the dummy falling block");

                    //velocity to north
                    dummy.setVelocity(new Vector(0, 1, 0));
                    //velocity to south
                    dummy.setVelocity(new Vector(0, -1, 0));
                    //velocity to east
                    dummy.setVelocity(new Vector(1, 0, 0));
                    //velocity to west
                    dummy.setVelocity(new Vector(-1, 0, 0));
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