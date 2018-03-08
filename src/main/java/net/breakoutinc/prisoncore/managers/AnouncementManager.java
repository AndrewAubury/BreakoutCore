/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import org.bukkit.ChatColor;

import java.util.ArrayList;

/**
 * Created by Andrew on 16/12/2017.
 */
public class AnouncementManager {
    PrisonCore core;
    Config cnf;
    public static AnouncementManager me;
    int next;
    public AnouncementManager(){
        next = 0;
        core = PrisonCore.getInstance();
        cnf = new Config(core.getDataFolder().getPath(),"announcement.yml");
        me = this;
        core.getServer().getScheduler().scheduleSyncRepeatingTask(core, new Runnable() {
            @Override
            public void run() {
                int next = AnouncementManager.getInstance().next;
                ArrayList<String> keys = new ArrayList<>();
                for (String s : cnf.getConfig().getConfigurationSection("announcements").getKeys(true)) {
                    keys.add(s);
                }
                if (next >= keys.size()) {
                    AnouncementManager.getInstance().next = 0;
                    next = 0;
                }
                for (String line : cnf.getConfig().getConfigurationSection("announcements").getStringList(keys.get(next))){
                    core.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', line));
            }
                AnouncementManager.getInstance().next++;
            }
        },cnf.getConfig().getInt("delay")*20,cnf.getConfig().getInt("delay")*20);
    }
    public static AnouncementManager getInstance(){
        return me;
    }


}
