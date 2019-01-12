/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.core.discord;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.events.JoinEvent;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Andrew on 15/12/2017.
 */
public class StaffTFA{
    BreakoutBot bot;
    JDA jda;
    PrisonCore core;
    Guild guild;
    MineBridge bridge;

    ArrayList<Player> tfaawating;
    public StaffTFA(){
        bot = BreakoutBot.getInstance();
        jda = bot.jda;
        core = bot.core;
        guild = bot.guild;
        bridge = bot.bridge;
        tfaawating = new ArrayList<Player>();

    }

    public boolean requiresTFA(Player p){
        boolean enabled = bot.cfg.getConfig().getBoolean("tfaenabled");
        if(!enabled){
            return false;
        }
        String perm = bot.cfg.getConfig().getString("tfaperm");
        if(p.hasPermission(perm)){
            if(bridge.playermap.containsValue(p.getUniqueId().toString())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    public boolean awatingtfa(Player p){
        return tfaawating.contains(p);
    }
    public void addToList(Player p){
        if(!tfaawating.contains(p)){
            tfaawating.add(p);
        }
    }
    public void removefromList(Player p){
        if(tfaawating.contains(p)){
            tfaawating.remove(p);
        }
    }
    public boolean RequestTFA(Player p){
        if(!requiresTFA(p)){
            return true;
        }
        addToList(p);
       core.getServer().getScheduler().runTask(core, new Runnable() {
           @Override
           public void run() {
               p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 255*20 , 1));
           }
       });
        ChatManager chat = new ChatManager();
        chat.sendMessageFromConfig(p,"discordtfaneeded",true);
        String discordid = "";
        for(String row : bridge.playermap.keySet()){
            String value = bridge.playermap.get(row);
            if(value.equalsIgnoreCase(p.getUniqueId().toString())){
                discordid = row;
            }
        }
       // System.out.println("Discord ID:" +discordid);
        User u = jda.getUserById(discordid);
        String msg = bot.cfg.getConfig().getString("messages.stafftfa");
        if(msg.contains("%player%")){
            msg = msg.replaceAll("%player%",p.getName());
        }
        if(msg.contains("%ip%")){
            msg = msg.replaceAll("%ip%",p.getAddress().getHostName());
        }
        if(msg.contains("%mention%")){
            msg = msg.replaceAll("%mention%",u.getAsMention());
        }

        Message m = u.openPrivateChannel().complete().sendMessage(msg).complete();
        Boolean isStaff = DiscordUtil.pullYesOrNoPrivate(m,u);
        //m.delete().queue();
        if(isStaff){
            removefromList(p);
            core.getServer().getScheduler().runTask(core, new Runnable() {
                @Override
                public void run() {
                    p.removePotionEffect(PotionEffectType.BLINDNESS);
                }
            });
            chat.sendMessageFromConfig(p,"discordtfadone",true);
        }else{
            removefromList(p);
            p.setBanned(true);
            core.getServer().getScheduler().runTask(core, new Runnable() {
                @Override
                public void run() {
                    p.kickPlayer("Temp system for tfa (line 127ish in StaffTFA class)");
                }
            });
        }
        System.out.println(discordid+": "+isStaff.toString());
        return isStaff;
    }
}
