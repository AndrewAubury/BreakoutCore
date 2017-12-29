package net.breakoutinc.prisoncore.events;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import net.breakoutinc.prisoncore.objects.TimePlayed;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Andrew on 01/12/2017.
 */
public class JoinEvent implements Listener {
    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {

        PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(e.getPlayer());
        TimePlayed time = new TimePlayed(prisonPlayer);
        time.StartRecording();

        PrisonCore.getInstance().getLogger().info("Starting to log player online time for "+e.getPlayer().getName());
        if(BreakoutBot.getInstance().isEnabled()){
            BreakoutBot bb = BreakoutBot.getInstance();
            String noColor = ChatColor.stripColor(":heavy_plus_sign: "+e.getPlayer().getName()   +" has joined the server");
            bb.sendChatToDiscord(noColor);
            bb.updateTopic();
            PrisonCore.getInstance().getServer().getScheduler().runTaskAsynchronously(PrisonCore.getInstance(),
                    new Runnable() {
                        @Override
                        public void run() {
                            if(bb.stafftfa.requiresTFA(e.getPlayer())){
                                bb.stafftfa.RequestTFA(e.getPlayer());
                            }
                        }
                    });
        }
    }
}
