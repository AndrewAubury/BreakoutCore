package net.breakoutinc.prisoncore.events;


import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.objects.DiscordOfflinePlayer;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
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
    private static AsyncChatEvent me;

    public AsyncChatEvent() {
        chat = new ChatHandler();
        me = this;
    }
public static AsyncChatEvent getInstance() {
    return me;
}

    public ChatHandler getChat() {
        return chat;
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncPlayerChatEvent e) {
        boolean isPreCanceled = e.isCancelled();

        if(! (e.getPlayer() instanceof DiscordOfflinePlayer)){
            PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(e.getPlayer());
            prisonPlayer.setDisplayName(e.getPlayer().getDisplayName());
            prisonPlayer.save();
        }
        PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(e.getPlayer());
        if(prisonPlayer.isSilentMuted()){
            e.setCancelled(true);
            String message = e.getMessage();
            message = StringEscapeUtils.escapeJava(message);
            message = chat.formatMessage(e.getPlayer(),message);
            e.getPlayer().sendMessage(message);
            return;
        }
        if(!isPreCanceled){
            //PrisonCore.getInstance().getServer().broadcastMessage("EVENT!");
            //e.setCancelled(true);
            String message = e.getMessage();
            message = StringEscapeUtils.escapeJava(message);
            message = chat.formatMessage(e.getPlayer(),message);

            e.setFormat(message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAsyncChatTwo(AsyncPlayerChatEvent e) {
        if(e.isCancelled()){
            e.setCancelled(true);
            return;
        }
        String message = e.getMessage();
        message = chat.formatMessage(e.getPlayer(),message);
        e.setFormat(message);

        //PrisonCore.getInstance().getServer().getConsoleSender().sendMessage(message);
        if(BreakoutBot.getInstance().isEnabled()){
            BreakoutBot bb = BreakoutBot.getInstance();
            String noColor = ChatColor.stripColor(message);
            bb.sendChatToDiscord(noColor);
        }
    }
}
