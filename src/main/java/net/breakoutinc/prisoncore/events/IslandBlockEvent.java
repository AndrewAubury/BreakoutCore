package net.breakoutinc.prisoncore.events;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class IslandBlockEvent implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e){
        if(e.getBlock().getWorld().getName().equalsIgnoreCase(IslandManager.getInstance().getIslandConfig().getConfig().getString("island-world")))
        {
            if(!IslandManager.getInstance().isValidIslandLocation(e.getBlock().getLocation())){
                e.setCancelled(true);
                e.getPlayer().sendMessage("You Cant build here.");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(e.getBlock().getWorld().getName().equalsIgnoreCase(IslandManager.getInstance().getIslandConfig().getConfig().getString("island-world")))
        {
            if(!IslandManager.getInstance().isValidIslandLocation(e.getBlock().getLocation())){
                e.setCancelled(true);
                e.getPlayer().sendMessage("You Cant build here.");
            }
        }
    }
}
