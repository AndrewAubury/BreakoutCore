package net.breakoutinc.prisoncore.objects;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.RankManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

/**
 * Created by Andrew on 24/11/2017.
 */
public class PrisonPlayer {
    private PrisonCore main;

    private TimePlayed timeLogger;
    private int tickets;
    private int prestige;
    private Long onlinetime;
    private String rank;
    private FileConfiguration config;
    private Config configObj;
    private Player player;

    public PrisonPlayer(Player player) {
        this.player = player;
        main = PrisonCore.getInstance();

        final Config pconfig = new Config(main.getDataFolder().getPath()+"/playerdata", player.getUniqueId().toString() + ".yml");
        pconfig.setReloader(false);
        configObj = pconfig;
        config = pconfig.getConfig();
        config.addDefault("uuid", UUID.randomUUID().toString());
        config.addDefault("onlinetime", 0);
        config.addDefault("tickets", 0);
        config.addDefault("prestige", 0);
        config.addDefault("blocksbroken", 0);
        config.addDefault("rank", PrisonCore.getInstance().getRM().getRanks().get(0));
        config.options().copyDefaults(true);
        pconfig.save();

        this.tickets = config.getInt("tickets");
        this.prestige = config.getInt("prestige");
        this.rank = config.getString("rank");
        this.onlinetime = config.getLong("onlinetime");
    }

    public void save(){
        config.set("tickets",this.tickets);
        config.set("prestige",this.prestige);
        config.set("rank",this.rank);
        config.set("onlinetime",this.onlinetime);
        configObj.save();
    }
    public int getPrestigeInt(){
        return prestige;
    }

    public String getPrestige(){
        return prestige+"";
    }
    public int getTickets(){return tickets;}
    public String getRank(){return rank;}
    public Long addOnlineTime(Long time){
        onlinetime =+ time;
        save();
        return onlinetime;
    }
    public long getOnlineTime(){
        return onlinetime;
    }
    public Player getPlayer(){return player;}

    public void prestige(){
        RankManager rm = PrisonCore.getInstance().getRM();
        this.prestige++;
        Permission perms = PrisonCore.getPerms();
        perms.playerRemoveGroup(null,player,rank);
        this.rank = PrisonCore.getInstance().getRM().getRanks().get(0);
        PrisonCore.getEcon().withdrawPlayer(player,PrisonCore.getEcon().getBalance(player));
        perms.playerAddGroup(null,player,rank);
        save();
    }

    public void fixPerms(){
        RankManager rm = PrisonCore.getInstance().getRM();
        rm.fix(this);
    }

    public void rankup(){
        RankManager rm = PrisonCore.getInstance().getRM();
        Economy econ = PrisonCore.getEcon();
        Permission perms = PrisonCore.getPerms();
        Chat chat = PrisonCore.getChat();
        if(perms.playerInGroup(null,player, rank)){
            perms.playerRemoveGroup(null, player, rank);
        }
        rank = rm.getNext(player);
        perms.playerAddGroup(null,player,rank);
        rm.runCommands(player);
        save();
    }
    public TimePlayed getTimeLogger() {
        return timeLogger;
    }

    public void setTimeLogger(TimePlayed timeLogger) {
        this.timeLogger = timeLogger;
    }
}
