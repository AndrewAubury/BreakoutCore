/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.managers.RankManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Andrew on 16/12/2017.
 */
public class RanksCommand implements CommandExecutor {

    private ChatManager chat;
    private Config cnf;

    public RanksCommand() {
        chat = new ChatManager();
        cnf = new Config(PrisonCore.getInstance().getDataFolder().getPath(),"lang.yml");
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("ranks")){
            if(sender instanceof Player){
                Player p = (Player) sender;
                PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(p);
                RankManager rm = PrisonCore.getInstance().getRM();
                NumberFormat numberFormat = NumberFormat.getInstance();
                chat.sendMessageFromConfig(p,"header",false);
                for(String rank : PrisonCore.getInstance().getRM().getRanks()){
                    if(PrisonCore.getInstance().getRM().isMax(rank)){
                        break;
                    }
                    String to = PrisonCore.getInstance().getRM().getNext(rank);
                    String cost = numberFormat.format(PrisonCore.getInstance().getRM().getCost(rank,p));
                    String format = cnf.getConfig().getString("ranksformat");
                    format = format.replaceAll("%to%",to);
                    format = format.replaceAll("%from%",rank);
                    format = format.replaceAll("%price%",cost);
                    chat.sendMessage(p,format,false);
                }
                chat.sendMessageFromConfig(p,"footer",false);
            }else{
                sender.sendMessage("This is a player only command");
            }
        }
        return true;
    }

}
