package net.breakoutinc.prisoncore.islands.managers;

import net.breakoutinc.prisoncore.islands.commands.ClaimCommand;
import net.breakoutinc.prisoncore.islands.commands.HomeCommand;
import net.breakoutinc.prisoncore.islands.commands.InfoCommand;
import net.breakoutinc.prisoncore.islands.commands.PlotMineCommand;
import net.breakoutinc.prisoncore.islands.objects.IslandCommand;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class IslandCommandHandler implements CommandExecutor, TabCompleter {
    private IslandCommand[] commands;

    public IslandCommandHandler() {
        commands = new IslandCommand[] {
                new ClaimCommand(),
                new HomeCommand(),
                new PlotMineCommand(),
                new InfoCommand()
        };
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            ChatManager chat = new ChatManager();
            chat.sendMessageFromConfig(sender,"header",false);
            for (IslandCommand command : commands) {
                if (sender.hasPermission(command.permission)) {
                    chat.sendMessage(sender, "&3/" + label + " " + command.cmd[0] + " - " + command.desc, false);
                }
            }
            chat.sendMessageFromConfig(sender,"footer",false);
        }else{
            for (IslandCommand command : commands) {
                for (String possible : command.cmd) {
                    if (args[0].equalsIgnoreCase(possible)) {
                        command.run(sender, label, args);
                        return true;
                    }
                }
            }
            ChatManager chat = new ChatManager();
            chat.sendMessageFromConfig(sender, "header", false);
            for (IslandCommand command : commands) {
                if (sender.hasPermission(command.permission)) {
                    chat.sendMessage(sender, "&3/" + label + " " + command.cmd[0] + " - " + command.desc, false);
                }
            }
            chat.sendMessageFromConfig(sender, "footer", false);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command c, String label, String[] args) {
        final List<String> completions = new ArrayList<String>();

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<String>();
            for (IslandCommand command : commands) {
                if (commandSender.hasPermission(command.permission)) {
                    for (String alias : command.cmd) {
                        subCommands.add(alias);
                    }
                }
            }
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length > 1) {
            for (IslandCommand command : commands) {
                for (String possible : command.cmd) {
                    if (args[0].equalsIgnoreCase(possible)) {
                        if (commandSender.hasPermission(command.permission)) {
                            List<String> subCommands = command.onTabComplete(args);
                            StringUtil.copyPartialMatches(args[args.length - 1], subCommands, completions);
                        }
                    }
                }
            }
        }


        Collections.sort(completions);
        return completions;
    }
    // return null;
}

