/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.managers.RankManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Andrew on 14/12/2017.
 */
public class RankUpMaxCommand implements CommandExecutor {

    private ChatManager chat;
    public RankUpMaxCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
       // if(label.equalsIgnoreCase("rankupmax")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(p);
                RankManager rm = PrisonCore.getInstance().getRM();
                String start = prisonPlayer.getRank();
                while(rm.getPercentage(p) == 100.00 && !rm.isMax(p)){
                    Double cost = rm.getCost(p);
                    PrisonCore.getEcon().withdrawPlayer(p,cost);
                    prisonPlayer.rankup();
                }
                if(prisonPlayer.getRank() != start){
                    chat.sendMessageFromConfig(p,"rankup",true);
                }else{
                    chat.sendMessageFromConfig(p,"rankupmax",true);
                }

            }else{
                sender.sendMessage("This is a player only command");
            }
        //}
        return true;
    }
}