package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Andrew on 01/12/2017.
 */
public class TestCommand implements CommandExecutor {

    private ChatManager chat;

    public TestCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("prisoncoretest")){


            if(sender instanceof Player){
              Player p = (Player) sender;
                IslandManager.getInstance().pasteIsland(p.getLocation());
                chat.sendMessage(p,"&cAttpting to place schematic",true);
            }
        }
        return true;
    }

}
