package net.breakoutinc.prisoncore.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class ChatManager {
    private PrisonCore main;

    public ChatManager() {
        main = PrisonCore.getInstance();
    }

    public void sendMessage(Player p, String msg_, boolean prefix) {
        final FileConfiguration config = new Config(main.getDataFolder().getPath(), "lang.yml").getConfig();
        String msg = prefix ? config.getString("prefix", "&7[&cBOINC&7]") + msg_ : msg_;
        msg  = PlaceholderAPI.setPlaceholders(p, msg);
        p.sendMessage(cc(msg));
    }
    public void sendMessage(CommandSender sender, String msg_, boolean prefix) {
        final FileConfiguration config = new Config(main.getDataFolder().getPath(), "lang.yml").getConfig();
        String msg = prefix ? config.getString("prefix", "&7[&cBOINC&7]") + msg_ : msg_;
        sender.sendMessage(cc(msg));
    }
    public void sendMessageFromConfig(Player p,String key,boolean prefix){
        final FileConfiguration config = new Config(main.getDataFolder().getPath(), "lang.yml").getConfig();
        String msg = prefix ? config.getString("prefix", "&7[&cBOINC&7]") + config.getString(key) : config.getString(key);
        msg  = PlaceholderAPI.setPlaceholders(p, msg);
        p.sendMessage(cc(msg));
    }
    public void sendMessageFromConfig(Player p,String key,boolean prefix, HashMap<String,String> replacements){
        final FileConfiguration config = new Config(main.getDataFolder().getPath(), "lang.yml").getConfig();
        String msg = prefix ? config.getString("prefix", "&7[&cBOINC&7]") + config.getString(key) : config.getString(key);
        for(String rkey : replacements.keySet()){
            if(msg.contains("%"+rkey+"%")){
                msg = msg.replaceAll("%"+rkey+"%",replacements.get(rkey));
            }
        }
        msg  = PlaceholderAPI.setPlaceholders(p, msg);
        p.sendMessage(cc(msg));
    }

    private String cc(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
