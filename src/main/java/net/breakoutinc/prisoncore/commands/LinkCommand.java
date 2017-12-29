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
public class LinkCommand implements CommandExecutor {

    private ChatManager chat;

    public LinkCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("link")){
            if(!BreakoutBot.getInstance().isEnabled()){
                chat.sendMessage(sender, "Discord integration is not turned on",true);
            }

            if(sender instanceof Player){
                if(args.length != 1){
                    chat.sendMessage(sender,"Syntax: /link (code)",true);
                    return true;
                }
                Player p = (Player) sender;
                BreakoutBot bb = BreakoutBot.getInstance();

                if(!bb.bridge.isCode(args[0])){
                    chat.sendMessageFromConfig(p,"discordbadcode",true);
                    return true;
                }
                bb.bridge.verifyCode(p,args[0]);
                chat.sendMessageFromConfig(p,"discordlinked",true);
            }else{
                sender.sendMessage("This is a player only command");
            }
        }
        return true;
    }
}