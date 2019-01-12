package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TokenAdminCommand implements CommandExecutor {

    private ChatManager chat;
    public TokenAdminCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!label.equalsIgnoreCase("tokenadmin")){
            return true;
        }

        if(!sender.hasPermission("prisoncore.tokenadmin")){
            chat.sendMessage(sender,"&cYou dont have permission to do that",true);
            return true;
        }




        return true;
    }

}

