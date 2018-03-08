/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.objects.BreakoutSender;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrew on 13/12/2017.
 */
public class RankManager {
    PrisonCore main;
    Config cfg;

    ArrayList<String> ranks;
    HashMap<String,Double> rankmap;

    ArrayList<String> rankUpcommands;
    ArrayList<String> prestigecommands;
    public RankManager(PrisonCore ourPlugin){
        main = ourPlugin;
        Config _cfg = new Config(main.getDataFolder().getPath(),"ranks.yml");
        cfg = _cfg;
        ranks = new ArrayList<String>();
        rankUpcommands = new ArrayList<String>();
        prestigecommands = new ArrayList<String>();
        rankmap = new HashMap<>();
        ConfigurationSection rankSection = cfg.getConfig().getConfigurationSection("ranks");
        for(String s : rankSection.getKeys(false)){
           // main.getLogger().info(s);
            ranks.add(s);
            rankmap.put(s,rankSection.getDouble(s));
        }
        for(String cmd : cfg.getConfig().getStringList("options.rankup.commands")){
            rankUpcommands.add(cmd);
        }
        for(String cmd : cfg.getConfig().getStringList("options.prestige.commands")){
            prestigecommands.add(cmd);
        }
    }

    public void runCommands(Player p){
        for(String cmd : rankUpcommands){
            cmd = cmd.replace("%player%",p.getName());
            PrisonCore.getInstance().getServer().dispatchCommand(new BreakoutSender(),cmd);
        }
    }
    public boolean isMaxPrestige(PrisonPlayer p){
        return (p.getPrestigeInt() >= cfg.getConfig().getInt("options.prestige.max"));
    }
    public void runPrestigeCommands(Player p){
        for(String cmd : prestigecommands){
            cmd = cmd.replace("%player%",p.getName());
            PrisonCore.getInstance().getServer().dispatchCommand(new BreakoutSender(),cmd);
        }
    }
    public Double getCost(String fromrank,Player p){
        Double without = rankmap.get(ranks.get(ranks.indexOf(fromrank)));
        PrisonPlayer pp = main.getPM().getPlayer(p);
        return without * ((cfg.getConfig().getDouble("options.prestige.multiplier")*Double.parseDouble(pp.getPrestige())+1.00));
    }
    public String getNext(String fromrank){
        return ranks.get(ranks.indexOf(fromrank)+1);
    }
    public Double getCost(Player p){
        PrisonPlayer pp = main.getPM().getPlayer(p);
        String current = pp.getRank();
        if(ranks.contains(current)){
            return rankmap.get(current) * ((cfg.getConfig().getDouble("options.prestige.multiplier")*Double.parseDouble(pp.getPrestige())+1.00));
        }
        main.getLogger().info("Called bad code");

        return (Double.parseDouble("999999999999999999"));
    }
    public ArrayList<String> getRanks(){return ranks;}
    public double getPercentage(Player p){
        PrisonPlayer pp = main.getPM().getPlayer(p);
        String current = pp.getRank();
        if(ranks.contains(current)){
             Double cost = getCost(current, p);
             Double balance = main.getEcon().getBalance(p);
             if(balance > cost){
                 return 100;
             }
            double result = balance/cost;
            result = result * 100;
           // main.getLogger().info(result+"");

            return result;
        }
        return (Double.parseDouble("0"));
    }
    public boolean isMax(Player p){
        PrisonPlayer pp = main.getPM().getPlayer(p);
        String current = pp.getRank();
        return (ranks.indexOf(current) == ranks.size() - 1);
    }
    public boolean isMax(String current){
        return (ranks.indexOf(current) == ranks.size() - 1);
    }
    public String getNext(Player p){
        PrisonPlayer pp = main.getPM().getPlayer(p);
        String current = pp.getRank();
        return ranks.get(ranks.indexOf(current)+1);
    }
    public Double getRemaining(Player p){
        PrisonPlayer pp = main.getPM().getPlayer(p);
        String current = pp.getRank();
        if(ranks.contains(current)){
            Double cost = getCost(p);
            Double balance = main.getEcon().getBalance(p);
            if(balance > cost){
                return 0.00;
            }
            double result = cost-balance;
            return result;
        }
        return (Double.parseDouble("0"));
    }
    public void fix(PrisonPlayer p){
        for(World w:main.getServer().getWorlds()){
            for(String rank : main.getPerms().getPlayerGroups(w.getName(),p.getPlayer())){
                main.getPerms().playerRemoveGroup(w.getName(),p.getPlayer(),rank);
            }
        }
        main.getPerms().playerAddGroup(null,p.getPlayer(),p.getRank());
    }
    public String getProgressBar(Player p){
        final FileConfiguration config = new Config(main.getDataFolder().getPath(), "lang.yml").getConfig();
        double progress = this.getPercentage(p);
        int green = (int) ((config.getDouble("progressbarlen") / 100.00) * progress);
        int red = config.getInt("progressbarlen")-green;
        String bar = "";
        bar = bar+ ChatColor.GREEN;
        bar = bar + StringUtils.repeat(config.getString("progressbar"), green);
        bar = bar+ ChatColor.RED;
        bar = bar + StringUtils.repeat(config.getString("progressbar"), red);
        return bar;
    }

}
