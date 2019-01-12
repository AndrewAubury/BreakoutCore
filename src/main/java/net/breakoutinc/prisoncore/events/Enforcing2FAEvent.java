/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.events;

import javafx.scene.layout.Priority;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

/**
 * Created by Andrew on 16/12/2017.
 */
public class Enforcing2FAEvent implements Listener {

//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerMove(PlayerMoveEvent e){
//        if(BreakoutBot.getInstance() == null){
//            return;
//        }
//        if(e.isCancelled()){
//            return;
//        }
//        if (BreakoutBot.getInstance().stafftfa.awatingtfa(e.getPlayer())){
//            Location loc = e.getFrom();
//            loc.setX(loc.getBlockX()+0.5);
//            loc.setY(loc.getBlockY());
//            loc.setZ(loc.getBlockZ()+0.5);
//            e.getPlayer().teleport(loc);
//            e.setCancelled(true);
//        }
//
//    }
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerChat(AsyncPlayerChatEvent e){
//        if(BreakoutBot.getInstance() == null){
//            return;
//        }
//        if(e.isCancelled()){
//            return;
//        }
//        e.setCancelled(BreakoutBot.getInstance().stafftfa.awatingtfa(e.getPlayer()));
//    }
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onCommandProcess(PlayerCommandPreprocessEvent e){
//        if(BreakoutBot.getInstance() == null){
//            return;
//        }
//        if(e.isCancelled()){
//            return;
//        }
//        e.setCancelled(BreakoutBot.getInstance().stafftfa.awatingtfa(e.getPlayer()));
//    }
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onPlayerInteract(PlayerInteractEvent e){
//        if(BreakoutBot.getInstance() == null){
//            return;
//        }
//        if(e.isCancelled()){
//            return;
//        }
//        if(!BreakoutBot.getInstance().stafftfa.RequestTFA(e.getPlayer())){
//            return;
//        }
//        e.setCancelled(BreakoutBot.getInstance().stafftfa.awatingtfa(e.getPlayer()));
//    }
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onInventoryEvent(InventoryClickEvent e){
//        if(e.getInventory().getType() == InventoryType.CRAFTING){
//            //Happy now XD
//            return;
//        }
//        if(BreakoutBot.getInstance() == null){
//            return;
//        }
//        e.setCancelled(BreakoutBot.getInstance().stafftfa.awatingtfa((Player) e.getWhoClicked()));
//    }
//

}
