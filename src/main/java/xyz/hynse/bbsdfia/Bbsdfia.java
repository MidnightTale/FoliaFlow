package xyz.hynse.bbsdfia;

import com.tcoded.folialib.FoliaLib;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bbsdfia extends JavaPlugin implements Listener {
    private final Vector velocity1 = new Vector(0, 0.5, -1);
    private final Vector velocity2 = new Vector(-1, 0.5, 0);
    private final Vector velocity3 = new Vector(0, 0.5, 1);
    private final Vector velocity4 = new Vector(1, 0.5, 0);
    private final Vector[] velocities = {velocity1, velocity2, velocity3, velocity4};
    private int counter = 0;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    FoliaLib foliaLib = new FoliaLib(this);

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
        executorService.shutdown();
    }

    @EventHandler
    public void onFallingBlockToBlock(EntityChangeBlockEvent e) {
        Entity entity = e.getEntity();
        if (entity.getType() != EntityType.FALLING_BLOCK || entity.getVelocity().length() == 0) {
            return;
        }

        executorService.execute(() -> {
            Location loc = entity.getLocation();
            Vector vel = entity.getVelocity();
            Block movingTo = getBlockMovingTo(loc, vel);

            if (         movingTo != null && movingTo.getType() == Material.END_PORTAL) {
                foliaLib.getImpl().runAtLocation(loc, () -> {
                    Location spawnLoc = movingTo.getLocation().add(0.5, 0.5, 0.5);
                    FallingBlock firstBlock = loc.getWorld().spawnFallingBlock(spawnLoc, ((FallingBlock) entity).getBlockData());
                    Vector dummyVel = vel.clone();
                    dummyVel.setY(-dummyVel.getY());
                    dummyVel.multiply(new Vector(2, 2, 2));
                    dummyVel.add(new Vector(0, -0.2, 0));
                    firstBlock.setVelocity(dummyVel);
                });
            }
        });
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock) || entity.getWorld().getEnvironment() != World.Environment.THE_END) {
            return;
        }

        byte data = ((FallingBlock) entity).getBlockData().getAsString().getBytes()[0];
        Material material = ((FallingBlock) entity).getBlockData().getMaterial();

        synchronized (this) {
            counter++;
            int index = counter % 4;
            Vector velocity = velocities[index];
            entity.remove();

            fallingBlocks.add(new FallingBlockData(entity.getLocation(), material, data, velocity));
            if (fallingBlocks.size() == 3) {
                executorService.execute(() -> {
                    List<FallingBlockData> blocks = new ArrayList<>(fallingBlocks);
                    fallingBlocks.clear();

                    for (FallingBlockData blockData : blocks) {
                        Location location = blockData.getLocation();
                        Vector blockVelocity = blockData.getVelocity();
                        spawnFallingBlockWithVelocity(location, blockData.getMaterial(), blockData.getData(), blockVelocity);
                    }
                });
            }
        }
    }

    private void spawnFallingBlockWithVelocity(Location location, Material material, byte data, Vector velocity) {
        FallingBlock newFallingBlock = location.getWorld().spawnFallingBlock(location, material, data);
        newFallingBlock.setVelocity(velocity);
    }

    Block getBlockMovingTo(Location loc, Vector vel) {
        double absMax = 0, max = 0;
        char dir = ' ';
        Block relative = null;
        if (Math.abs(vel.getX()) > absMax) {
            max = vel.getX();
            absMax = Math.abs(vel.getX());
            dir = 'x';
        }
        if (Math.abs(vel.getY()) > absMax) {
            max = vel.getY();
            absMax = Math.abs(vel.getY());
            dir = 'y';
        }
        if (Math.abs(vel.getZ()) > absMax) {
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

    private static class FallingBlockData {
        private final Location location;
        private final Material material;
        private final byte data;
        private final Vector velocity;

        public FallingBlockData(Location location, Material material, byte data, Vector velocity) {
            this.location = location;
            this.material = material;
            this.data = data;
            this.velocity = velocity;
        }

        public Location getLocation() {
            return location;
        }

        public Material getMaterial() {
            return material;
        }

        public byte getData() {
            return data;
        }

        public Vector getVelocity() {
            return velocity;
        }
    }

    private final List<FallingBlockData> fallingBlocks = new ArrayList<>();
}
