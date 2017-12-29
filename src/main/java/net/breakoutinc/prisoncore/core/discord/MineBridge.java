/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.core.discord;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Andrew on 14/12/2017.
 */
public class MineBridge {
    HashMap<String, String> playermap;
    BreakoutBot bot;
    PrisonCore core;
    JDA jda;
    Guild guild;

    Config configObj;
    FileConfiguration config;

    HashMap<String, String> codes;

    public MineBridge() {
        playermap = new HashMap<>();
        bot = BreakoutBot.getInstance();
        core = bot.core;
        jda = bot.jda;
        guild = bot.guild;

        final Config pconfig = new Config(core.getDataFolder().getPath(), "discordplayers.yml");
        configObj = pconfig;
        config = pconfig.getConfig();
        config.options().copyDefaults(true);
        pconfig.save();
        codes = new HashMap<>();
        for (String id : config.getKeys(false)) {
            playermap.put(id, config.getString(id));
        }
    }

    public void addLink(String discordid, String minecraftuuid) {
        playermap.put(discordid, minecraftuuid);
    }

    public void save(){
        for(String id : playermap.keySet()){
            config.set(id,playermap.get(id));
        }
        configObj.save();
    }

    public String getMinecraftUUID(String DiscordID) {
        if (playermap.containsKey(DiscordID)) {
            return playermap.get(DiscordID);
        } else {
            return null;
        }
    }

    public boolean isLinked(String DiscordID){
        return playermap.containsKey(DiscordID);
    }

    public void assignRole(String discordid){
        Role r = guild.getRoleById(Long.parseLong(bot.cfg.getConfig().getString("linkedrole")));
        User u = jda.getUserById(Long.parseLong(discordid));
        Member m = guild.getMember(u);
        if(!m.getRoles().contains(r.getName())){
            guild.getController().addSingleRoleToMember(m,r);
        }
        guild.getController().addSingleRoleToMember(m,r).queue();
    }

    public boolean isCode(String code){
        return codes.containsKey(code);
    }
    public void verifyCode(Player p, String code){
        if(isCode(code)){
            addLink(codes.get(code),p.getUniqueId().toString());
            assignRole(codes.get(code));
            codes.remove(code);
        }
    }
    public String makeCode(String discordid){
        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        codes.put(generatedString,discordid);
        return generatedString;
    }

}
