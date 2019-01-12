package net.breakoutinc.prisoncore.objects;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.RankManager;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
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
    private int tokens;
    private int prestige;
    private Long onlinetime;
    private String rank;
    private FileConfiguration config;
    private Config configObj;
    private String displayName;
    private Player player;
    private boolean silentmuted;

    public PrisonPlayer(Player player) {
        this.player = player;
        main = PrisonCore.getInstance();

        Config pconfig = new Config(main.getDataFolder().getPath()+"/playerdata", player.getUniqueId().toString() + ".yml");
        pconfig.setReloader(false);
        configObj = pconfig;
        displayName = player.getDisplayName();
        config = pconfig.getConfig();
        config.addDefault("uuid", UUID.randomUUID().toString());
        config.addDefault("onlinetime", 0);
        config.addDefault("tickets", 0);
        config.addDefault("prestige", 0);
        config.addDefault("blocksbroken", 0);
        config.addDefault("rank", PrisonCore.getInstance().getRM().getRanks().get(0));
        config.addDefault("displayname", player.getDisplayName());
        config.addDefault("silentmuted", false);
        config.addDefault("tokens", 0);
        config.options().copyDefaults(true);
        pconfig.save();
        if(!config.contains("displayname")) {
            config.set("displayname", player.getName());
            configObj.save();
        }
        if(!config.contains("silentmuted")) {
            config.set("silentmuted", false);
            configObj.save();
        }


        this.tokens = config.getInt("tokens");
        this.tickets = config.getInt("tickets");
        this.prestige = config.getInt("prestige");
        this.rank = config.getString("rank");
        this.onlinetime = config.getLong("onlinetime");
        this.displayName = config.getString("displayname");
        this.silentmuted = config.getBoolean("silentmuted");
    }

    public void save(){
        config.set("tickets",this.tickets);
        config.set("prestige",this.prestige);
        config.set("rank",this.rank);
        config.set("onlinetime",this.onlinetime);
        config.set("displayname",this.displayName);
        config.set("silentmuted",this.silentmuted);
        configObj.save();
    }
    public int getPrestigeInt(){
        return prestige;
    }

    public boolean isSilentMuted(){
        return silentmuted;
    }

    public void silentMute(){
        silentmuted = true;
    }

    public void silentUnmute(){
        silentmuted = false;
    }

    public String getPrestige(){
        return prestige+"";
    }

    public int getTickets(){return tickets;}

    public int getTokens(){return tokens;}

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

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

    public void setTabListHeader(){
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);

        String header = PlaceholderAPI.setPlaceholders(player, StringUtils.join(PrisonCore.getInstance().miscConfig.getConfig().getStringList("tab-header"), "\n"));
        String footer = PlaceholderAPI.setPlaceholders(player, StringUtils.join(PrisonCore.getInstance().miscConfig.getConfig().getStringList("tab-footer"), "\n"));


        pc.getChatComponents().write(0, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&',header)))
                .write(1, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&',footer)));
        try
        {
            protocolManager.sendServerPacket(this.getPlayer(), pc);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
