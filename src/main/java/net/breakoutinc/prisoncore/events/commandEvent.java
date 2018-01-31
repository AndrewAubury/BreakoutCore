package net.breakoutinc.prisoncore.events;

import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class commandEvent implements Listener {
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e){
        if(e.getMessage().split(" ")[0].contains(":")){
            e.setCancelled(true);
            ChatManager cm =  new ChatManager();
            cm.sendMessage(e.getPlayer(),"&cSorry but colon commands are blocked",true);
        }
    }
    @EventHandler
    public void onTabComplete(PlayerChatTabCompleteEvent e) {
        if(e.getChatMessage().split(" ")[0].contains(":")){
            e.getTabCompletions().clear();
        }
        if(e.getChatMessage().replace("/","").split(" ")[0].equalsIgnoreCase("ver")){
            e.getTabCompletions().clear();
        }
        if(e.getChatMessage().split(" ")[0].contains("version")){
            e.getTabCompletions().clear();
        }

    }
}
