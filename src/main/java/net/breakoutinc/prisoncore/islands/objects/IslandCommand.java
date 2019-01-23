package net.breakoutinc.prisoncore.islands.objects;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

// ------------------------------
// Copyright (c) AndrewAubury 2017
// ------------------------------
public abstract class IslandCommand {
    public String[] cmd = new String[]{};
    public String permission = "";
    public String desc = null;

    protected String[] subcommands = new String[]{};

    public abstract void execute(CommandSender sender, String label, String[] args);

    public void run(CommandSender sender, String label, String[] args) {

        if (permission != null && (!PrisonCore.getPerms().has(sender, permission))) {
            new ChatManager().sendMessage(sender, "&cYou dont have permission to run this command",true);
        } else {
            execute(sender, label, args);
            
        }
    }

    public List<String> onTabComplete(String[] args) {
        return new ArrayList<>();
    }
}
