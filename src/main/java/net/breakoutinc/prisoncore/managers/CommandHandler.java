package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.Command;
import net.breakoutinc.prisoncore.commands.Test;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class CommandHandler implements CommandExecutor {
    private Command[] commands;

    public CommandHandler() {
        commands = new Command[] {
                new Test()
        };
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        for (Command command : commands) {
            if (args[0].equalsIgnoreCase(command.cmd)) {
                command.run(sender, args);
            }
        }
        return true;
    }
}
