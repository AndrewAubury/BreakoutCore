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
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.bukkit.entity.Player;

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
                    jda = new JDABuilder(AccountType.BOT).setToken(cfg.getConfig().getString("discordtoken")).addEventListener(new MessageReciveEvent()).buildBlocking();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            bridge = new MineBridge();
            stafftfa = new StaffTFA();
            Role r = getLinkedRole();

           getChatChannel().putPermissionOverride(r).setAllow(net.dv8tion.jda.core.Permission.MESSAGE_READ, net.dv8tion.jda.core.Permission.MESSAGE_WRITE).complete();

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
        guild = jda.getGuilds().get(0);
        String cid = bot.cfg.getConfig().getString("chatchannelid");
        guild.getTextChannelById(cid).sendMessage(message).queue();
    }
    public TextChannel getChatChannel(){
        guild = jda.getGuilds().get(0);
        String cid = bot.cfg.getConfig().getString("chatchannelid");
        return guild.getTextChannelById(cid);
    }

    public Role getLinkedRole(){
        return jda.getGuilds().get(0).getRolesByName(cfg.getConfig().getString("linkedrole"),true).get(0);
    }

    public void updateTopic(int count){
        guild = jda.getGuilds().get(0);
        String cid = bot.cfg.getConfig().getString("chatchannelid");
        if(count == 0){
            guild.getTextChannelById(cid).getManager().setTopic("There are no players online").queue();
        }else if(count == 1){
            Player p = null;
            for(Player pp: core.getServer().getOnlinePlayers()){
                p = pp;
            }
            guild.getTextChannelById(cid).getManager().setTopic("Only "+p.getName()+" is online").queue();

        }else{
            guild.getTextChannelById(cid).getManager().setTopic("There are "+core.getServer().getOnlinePlayers().size()+" players online").queue();

        }
    }
}
