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

/**
 * Created by Andrew on 14/12/2017.
 */
public class RankUpCommand implements CommandExecutor {

    private ChatManager chat;

    public RankUpCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //if(label.equalsIgnoreCase("rankup")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(p);
                RankManager rm = PrisonCore.getInstance().getRM();

                if(rm.isMax(p)){
                    chat.sendMessageFromConfig(p,"maxrank",true);
                    return true;
                }

                Double remaining = rm.getRemaining(p);
                if(remaining == 0.00){
                    Double cost = rm.getCost(p);
                    PrisonCore.getEcon().withdrawPlayer(p,cost);
                    prisonPlayer.rankup();
                    chat.sendMessageFromConfig(p,"rankup",true);
                }else{
                    chat.sendMessageFromConfig(p,"cantaffordrankup",true);
                }
            }else{
                sender.sendMessage("This is a player only command");
            }
      // }
        return true;
    }
}