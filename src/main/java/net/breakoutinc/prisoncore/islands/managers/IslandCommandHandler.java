package net.breakoutinc.prisoncore.islands.managers;

import net.breakoutinc.prisoncore.Command;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.commands.Test;
import net.breakoutinc.prisoncore.islands.commands.ClaimCommand;
import net.breakoutinc.prisoncore.islands.objects.IslandCommand;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class IslandCommandHandler implements CommandExecutor {
    private IslandCommand[] commands;

    public IslandCommandHandler() {
        commands = new IslandCommand[] {
            new ClaimCommand()
        };
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if(args.length < 1){
            ChatManager chat = new ChatManager();
            chat.sendMessageFromConfig(sender,"header",false);
            for (IslandCommand command : commands) {
                if (command.permission != null && (!PrisonCore.getPerms().has(sender, command.permission))) {
                     chat.sendMessage(sender, "&3" + command.cmd + " - "+command.desc,false);
                }
            }
            chat.sendMessageFromConfig(sender,"footer",false);
        }else{
            for (IslandCommand command : commands) {
                if (args[0].equalsIgnoreCase(command.cmd)) {
                    command.run(sender, args);
                }
            }
        }
        return true;
    }
}
