package xyz.hynse.foliaflow.watcher;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.util.Vector;

import xyz.hynse.foliaflow.FoliaFlow;
import xyz.hynse.foliaflow.util.SchedulerUtil;

import static xyz.hynse.foliaflow.util.VelocityUtil.getBlockMovingTo;

public class PortalWatcher implements Listener {
    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent e){
        if(e.getEntityType() != EntityType.FALLING_BLOCK) return;
        Entity entity = e.getEntity();
        Location loc = entity.getLocation();
        Vector vel = entity.getVelocity();
        Block movingTo = getBlockMovingTo(loc, vel);

        if(movingTo.getType() == Material.END_PORTAL){
            Location spawnLoc = movingTo.getLocation();
            spawnLoc.add(0.5, 0.5, 0.5);

            FallingBlock dummy = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
            Vector dummyVel = vel.clone();
            dummyVel.setY(-dummyVel.getY());
            dummyVel.multiply(1.3); // This seems vanilla.
            dummy.setVelocity(dummyVel);

            // Add vector seems vanilla.
            SchedulerUtil.runLaterEntity(dummy, FoliaFlow.instance, () -> {
                    // Portal teleportation on Folia is a bit below vanilla, so we teleport it above
                    if (SchedulerUtil.isFolia()) dummy.teleportAsync(dummy.getLocation().add(0, 0.5, 0));
                    dummy.setVelocity(dummyVel);
                },
                2
            );
        }
    }
}
