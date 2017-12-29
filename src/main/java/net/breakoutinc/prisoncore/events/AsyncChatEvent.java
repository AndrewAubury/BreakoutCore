package net.breakoutinc.prisoncore.events;


import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.objects.DiscordOfflinePlayer;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

// ------------------------------
// Copyright (c) PiggyPiglet and AndrewAubury 2017
// https://www.piggypiglet.me
// https://andrewaubury.me
// ------------------------------
public class AsyncChatEvent implements Listener {
    private ChatHandler chat;

    public AsyncChatEvent() {
        chat = new ChatHandler();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        boolean isPreCanceled = e.isCancelled();

        if(!isPreCanceled){
            e.setCancelled(true);
            String message = e.getMessage();
            //message = StringEscapeUtils.escapeJava(message);
            message = chat.formatMessage(e.getPlayer(),message);

            for(Player p : e.getRecipients()){
                p.sendMessage(message);
            }

            PrisonCore.getInstance().getServer().getConsoleSender().sendMessage(message);
            if(BreakoutBot.getInstance().isEnabled()){
                BreakoutBot bb = BreakoutBot.getInstance();
                String noColor = ChatColor.stripColor(message);
                bb.sendChatToDiscord(noColor);
            }
        }
    }
}
