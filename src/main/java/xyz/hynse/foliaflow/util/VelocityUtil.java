package xyz.hynse.foliaflow.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class VelocityUtil {
    public static Block getBlockMovingTo(Location loc, Vector vel) {
        double absX = Math.abs(vel.getX());
        double absY = Math.abs(vel.getY());
        double absZ = Math.abs(vel.getZ());
        if (absX > absY && absX > absZ) {
            return loc.getBlock().getRelative((int) Math.signum(vel.getX()), 0, 0);
        } else if (absY > absZ) {
            return loc.getBlock().getRelative(0, (int) Math.signum(vel.getY()), 0);
        }
        return loc.getBlock().getRelative(0, 0, (int) Math.signum(vel.getZ()));
    }
}
