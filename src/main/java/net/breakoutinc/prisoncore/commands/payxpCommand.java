/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.managers.ExperienceManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Andrew on 22/12/2017.
 */
public class payxpCommand implements CommandExecutor {

    private ChatManager chat;

    public payxpCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("payxp")){
            if(!(sender instanceof Player)) {
                sender.sendMessage("Player only command.");
                return true;
            }
            Player p = (Player) sender;
            ExperienceManager emp = new ExperienceManager(p);

            if(args.length != 2){
                chat.sendMessageFromConfig(p,"payxpsyntax",true);
                return true;
            }
            Player target = PrisonCore.getInstance().getServer().getPlayer(args[0]);
            if(target == null || !target.isOnline()){
                chat.sendMessageFromConfig(p,"targetoffline",true);
                return true;
            }
            if(target.getUniqueId() == p.getUniqueId()){
                chat.sendMessageFromConfig(p,"targetyourself",true);
                return true;
            }
            if(!isInteger(args[1])){
                chat.sendMessageFromConfig(p,"payxpsyntax",true);
                return true;
            }

            int xp = Integer.parseInt(args[1]);

            if(emp.getTotalExperience() < xp){
                chat.sendMessageFromConfig(p,"xpcantafford",true);
                chat.sendMessage(p,"&cYou have "+emp.getTotalExperience()+" XP",true);
                return true;
            }

            emp.setTotalExperience(emp.getTotalExperience() - xp);
            ExperienceManager tem = new ExperienceManager(target);
            tem.setTotalExperience(tem.getTotalExperience() + xp);

            HashMap<String,String> replacements = new HashMap<String,String>();
            replacements.put("xp",xp+"");
            replacements.put("from",p.getName());
            replacements.put("to",target.getName());

            chat.sendMessageFromConfig(p,"xpsentto",true,replacements);
            chat.sendMessageFromConfig(target,"xpsentfrom",true,replacements);


        }
        return true;
    }
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}