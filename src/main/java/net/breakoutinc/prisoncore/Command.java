package net.breakoutinc.prisoncore;

import org.bukkit.command.CommandSender;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public abstract class Command {
    public String cmd = "null";

    public abstract void execute(CommandSender sender, String[] args);

    public void run(CommandSender sender, String[] args) {
        execute(sender, args);
    }
}
