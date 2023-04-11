package xyz.hynse.bbsdfia;

import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
            Location loc2 = new Location(Bukkit.getWorld("world_the_end"), 100, 50, 0);


            if(movingTo != null && movingTo.getType() == Material.END_PORTAL){
                /*
                World world = Bukkit.getServer().getWorld("world");
                Location spawnLoc = new Location(world, 100, 50, 0);
                */
                //Entity Scheduler Task
                EntityScheduler scheduler = new EntityScheduler() {
                    @Override
                    public boolean execute(@NotNull Plugin plugin, @NotNull Runnable run, @Nullable Runnable retired, long delay) {
                        return false;
                    }

                    @Override
                    public @Nullable ScheduledTask run(@NotNull Plugin plugin, @NotNull Consumer<ScheduledTask> task, @Nullable Runnable retired) {
                        return null;
                    }

                    @Override
                    public @Nullable ScheduledTask runDelayed(@NotNull Plugin plugin, @NotNull Consumer<ScheduledTask> task, @Nullable Runnable retired, long delayTicks) {
                        return null;
                    }

                    @Override
                    public @Nullable ScheduledTask runAtFixedRate(@NotNull Plugin plugin, @NotNull Consumer<ScheduledTask> task, @Nullable Runnable retired, long initialDelayTicks, long periodTicks) {
                        return null;
                    }
                };
                scheduler.runDelayed(this, scheduledTask -> {

                    //spawn new falling block in the end dimension and have same properties entity type and material same form entity that detect near end portal
                    FallingBlock dummy = (FallingBlock) loc2.getWorld().spawnEntity(loc2, EntityType.FALLING_BLOCK);
                    Vector dummyVel = vel.clone();
                    dummy.setVelocity(dummyVel);
                    //velocity to north
                    dummy.setVelocity(new Vector(0, 1, 0));
                    //velocity to south
                    dummy.setVelocity(new Vector(0, -1, 0));
                    //velocity to east
                    dummy.setVelocity(new Vector(1, 0, 0));
                    //velocity to west
                    dummy.setVelocity(new Vector(-1, 0, 0));
                },null,1);

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
