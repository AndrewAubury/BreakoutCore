/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.core.discord;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

import javax.security.auth.login.LoginException;

/**
 * Created by Andrew on 14/12/2017.
 */
public class BreakoutBot {
    private static BreakoutBot bot;
    JDA jda;
    Config cfg;
    PrisonCore core;
    public MineBridge bridge;
    public StaffTFA stafftfa;
    boolean enabled;

    public Guild guild;

    public BreakoutBot(){
        bot = this;
        core = PrisonCore.getInstance();
        cfg = new Config(core.getDataFolder().getPath(),"discord.yml");
        enabled = cfg.getConfig().getBoolean("enabled");
        if(enabled){
            try {
                if(cfg.getConfig().getBoolean("isplaying")){
                    jda = new JDABuilder(AccountType.BOT)
                            .setToken(cfg.getConfig().getString("discordtoken"))
                            .setGame(Game.playing(cfg.getConfig().getString("game")))
                            .addEventListener(new MessageReciveEvent())
                            .buildBlocking();
                }else{
                    jda = new JDABuilder(AccountType.BOT).setToken(cfg.getConfig().getString("discordtoken")).buildBlocking();
                }
                guild = jda.getGuilds().get(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            bridge = new MineBridge();
            stafftfa = new StaffTFA();
            //jda.addEventListener(new MessageReciveEvent());
        }
    }

    public static BreakoutBot getInstance(){
        return bot;
    }

    public JDA getJDA(){
        return jda;
    }

    public boolean isEnabled(){
        return enabled;
    }
    public void sendChatToDiscord(String message){
        String cid = bot.cfg.getConfig().getString("chatchannelid");
        guild.getTextChannelById(cid).sendMessage(message).queue();
    }
    public void updateTopic(){
        String cid = bot.cfg.getConfig().getString("chatchannelid");
        guild.getTextChannelById(cid).getManager().setTopic("There are "+core.getServer().getOnlinePlayers().size()+" players are online");
    }
}
