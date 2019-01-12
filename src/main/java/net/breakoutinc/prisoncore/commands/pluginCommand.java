package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pluginCommand implements CommandExecutor {
    private ChatManager chat;

    public pluginCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        chat.sendMessage((Player)sender,"&fPlugins (4): &aEssentials, &aInmateIslandCore&f, &a&aAndrews&f, &aCustom&f, &aPlugins",false);
        return true;
    }
}
