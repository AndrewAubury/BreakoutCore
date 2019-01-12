package net.breakoutinc.prisoncore.islands.objects;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.CommandSender;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public abstract class IslandCommand {
    public String cmd = "null";
    public String permission = null;
    public String desc = null;

    public abstract void execute(CommandSender sender, String[] args);

    public void run(CommandSender sender, String[] args) {

        if (permission != null && (!PrisonCore.getPerms().has(sender, permission))) {
            new ChatManager().sendMessage(sender, "&cYou dont have permission to run this command",true);
        } else {
            execute(sender, args);
            
        }
    }
}
