package net.breakoutinc.prisoncore.events;

import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.islands.objects.Island;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class IslandBlockEvent implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if (e.getBlock().getWorld().getName().equalsIgnoreCase(IslandManager.getInstance().getIslandConfig().getConfig().getString("island-world"))) {
            Island i = IslandManager.getInstance().getIsland(e.getBlock().getLocation());
            if (i != null) {
                if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase(i.getOwner().toString()) || i.getMembers().contains(e.getPlayer().getUniqueId()) || e.getPlayer().hasPermission("islands.build.others")) {
                    Location newblock = e.getBlockPlaced().getLocation();
                    if (inRegion(newblock, i.getDockMin(), i.getDockMax())) {
                        e.setCancelled(true);
                        new ChatManager().sendMessage(e.getPlayer(), "&cYou cant build here.", true);
                    }

                    if (inRegion(newblock, i.getMineMin(), i.getMineMax()) && (!i.isIslandMineEnabled())) {
                        e.setCancelled(true);
                        new ChatManager().sendMessage(e.getPlayer(), "&cYou cant build here.", true);
                    }


                } else {
                    e.setCancelled(true);
                    new ChatManager().sendMessage(e.getPlayer(), "&cYou cant build here.", true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if (e.getBlock().getWorld().getName().equalsIgnoreCase(IslandManager.getInstance().getIslandConfig().getConfig().getString("island-world"))) {
            Island i = IslandManager.getInstance().getIsland(e.getBlock().getLocation());
            if (i != null) {
//                e.getPlayer().sendMessage(e.getPlayer().getUniqueId() + " | "+i.getOwner());
                if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase(i.getOwner().toString()) || i.getMembers().contains(e.getPlayer().getUniqueId()) || e.getPlayer().hasPermission("islands.build.others")) {
//                   e.getPlayer().sendMessage("Passed 1");
                    Location newblock = e.getBlock().getLocation();
                    if (inRegion(newblock, i.getDockMin(), i.getDockMax())) {
//                        e.getPlayer().sendMessage("Failed 1");
                        new ChatManager().sendMessage(e.getPlayer(), "&cYou cant build here.", true);
                        e.setCancelled(true);
                    }

                    if (inRegion(newblock, i.getMineMin(), i.getMineMax()) && (!i.isIslandMineEnabled())) {
//                        e.getPlayer().sendMessage("Failed 2");
                        new ChatManager().sendMessage(e.getPlayer(), "&cYou cant build here.", true);
                        e.setCancelled(true);
                    } else {
                        if (inRegion(newblock, i.getMineMin(), i.getMineMax())) {
                            e.setCancelled(true);
                            if ((inRegion(newblock, i.getInsideMine().getMinimumPoint(), i.getInsideMine().getMaximumPoint()))) {
                                e.setCancelled(false);
                            }
                        }
                    }


                } else {
//                    e.getPlayer().sendMessage("Failed 3");
                    new ChatManager().sendMessage(e.getPlayer(), "&cYou cant build here.", true);
                    e.setCancelled(true);

                }
            } else {
                e.setCancelled(true);

            }
        }
    }


    public boolean inRegion(Location loc, Location loc1, Location loc2) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();

        double x1 = loc1.getX() - 1;
        double y1 = loc1.getY() - 1;
        double z1 = loc1.getZ() - 1;

        double x2 = loc2.getX() + 1;
        double y2 = loc2.getY() + 1;
        double z2 = loc2.getZ() + 1;

        //return (((x > x1 || x == x1) && (y > y1 || y == y1) && (z > z1 || z == z1) && (x < x2 || x == x2) && (y < y2 || y == y2) && (z > z2 || z == z2)));
        return (x > x1) && (y > y1) && (z > z1) && (x < x2) && (y < y2) && (z < z2);
    }


}
