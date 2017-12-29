/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.managers.ConfigManager;
import net.breakoutinc.prisoncore.managers.RankManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

/**
 * Created by Andrew on 16/12/2017.
 */
public class CoreReloadCommand implements CommandExecutor {

    private ChatManager chat;
    private Config cnf;

    public CoreReloadCommand() {
        chat = new ChatManager();
    }


    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("corereload")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ConfigManager.getInstance().reloadAll();
                ChatHandler.loadConfigs();
                chat.sendMessage(p,"&athe config was reloaded",true);
            } else {
                sender.sendMessage("This is a player only command");
            }
        }
        return true;
    }
}
