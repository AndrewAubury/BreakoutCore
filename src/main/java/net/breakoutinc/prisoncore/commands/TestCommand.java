package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

            sender.sendMessage("running test");
            if(sender instanceof Player){
                Player p = (Player) sender;
                PrisonPlayer prisonPlayer = PrisonCore.getInstance().getPM().getPlayer(p);
                prisonPlayer.prestige();
            }
        }
        return false;
    }
}
