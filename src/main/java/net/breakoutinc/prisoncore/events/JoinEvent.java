package net.breakoutinc.prisoncore.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.managers.ConfigManager;
import net.breakoutinc.prisoncore.managers.PlayerManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import net.breakoutinc.prisoncore.objects.TimePlayed;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

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
            bb.updateTopic(PrisonCore.getInstance().getServer().getOnlinePlayers().size());
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

        PrisonCore.getInstance().getServer().getOnlinePlayers().forEach(p -> PrisonCore.getInstance().getPM().getPlayer(p).setTabListHeader());

    }

    private String fixColors(String s)
    {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @EventHandler
    public void onServerListView(ServerListPingEvent e){
        e.setMotd(ChatColor.translateAlternateColorCodes('&',StringUtils.join(PrisonCore.getInstance().miscConfig.getConfig().getStringList("motd"),"\n")));
        e.setMaxPlayers(PrisonCore.getInstance().miscConfig.getConfig().getInt("slots"));
    }


}
