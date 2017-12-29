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
public class PrestigeCommand  implements CommandExecutor {

    private ChatManager chat;
    private ArrayList<Player> waiting;

    public PrestigeCommand() {
        chat = new ChatManager();
        waiting = new ArrayList<Player>();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("prestige")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(p);
                RankManager rm = PrisonCore.getInstance().getRM();
                if(!rm.isMax(p)){
                    chat.sendMessageFromConfig(p,"notmaxrank",true);
                    return true;
                }
                if(rm.isMaxPrestige(prisonPlayer)){
                    chat.sendMessageFromConfig(p,"maxprestige",true);
                    return true;
                }
                if(!waiting.contains(p)){
                    waiting.add(p);
                    chat.sendMessageFromConfig(p,"prestigewarning",true);
                }else{
                    waiting.remove(p);
                    prisonPlayer.prestige();
                    rm.runPrestigeCommands(p);
                    chat.sendMessageFromConfig(p,"prestige",true);
            }


            }else{
                sender.sendMessage("This is a player only command");
            }
        }
        return true;
    }
}