package net.breakoutinc.prisoncore.commands;

/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

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
public class SilentMuteCommand implements CommandExecutor {

    private ChatManager chat;
    public SilentMuteCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("prisoncore.silentmute")){
            chat.sendMessage(sender,"&cYou dont have permission to do that",true);
            return true;
        }

        if(args.length != 1){
            chat.sendMessage(sender,"&cUsage: /"+label+" [player]",true);
            return true;
        }

            Player p = PrisonCore.getInstance().getServer().getPlayer(args[0]);
            if(p == null){
                chat.sendMessage(sender,"&cPlayer not found",true);
                return true;
            }

            PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(p);
            if(prisonPlayer.isSilentMuted()){
                prisonPlayer.silentUnmute();
                for(Player staff : PrisonCore.getInstance().getServer().getOnlinePlayers()){
                    if(staff.hasPermission("prisoncore.silentmute")){
                        chat.sendMessage(staff,"&a"+p.getName()+" has been unmuted by "+sender.getName(),true);
                    }
                }
            }else{
                prisonPlayer.silentMute();
                for(Player staff : PrisonCore.getInstance().getServer().getOnlinePlayers()){
                    if(staff.hasPermission("prisoncore.silentmute")){
                        chat.sendMessage(staff,"&c"+p.getName()+" has been muted by "+sender.getName(),true);
                    }
                }
            }

        return true;
    }
}