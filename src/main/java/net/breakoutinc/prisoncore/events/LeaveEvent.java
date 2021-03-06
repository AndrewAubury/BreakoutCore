package net.breakoutinc.prisoncore.events;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class LeaveEvent implements Listener {
    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        PrisonCore.getInstance().getServer().getScheduler().runTaskLater(PrisonCore.getInstance(), new Runnable() {
            @Override
            public void run() {
                PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(e.getPlayer());
                prisonPlayer.addOnlineTime(prisonPlayer.getTimeLogger().StopRecording());
                if (BreakoutBot.getInstance().isEnabled()) {
                    BreakoutBot bb = BreakoutBot.getInstance();
                    String noColor = ChatColor.stripColor(":heavy_minus_sign: " + e.getPlayer().getName() + " has left the server");
                    bb.sendChatToDiscord(noColor);
                    bb.updateTopic(PrisonCore.getInstance().getServer().getOnlinePlayers().size() - 1);
                }
                PrisonCore.getInstance().getServer().getOnlinePlayers().forEach(p -> PrisonCore.getInstance().getPM().getPlayer(p).setTabListHeader());

            }
        }, 10l);
    }
}
