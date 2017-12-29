package net.breakoutinc.prisoncore.commands;

import net.breakoutinc.prisoncore.Command;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class Test extends Command {
    private ChatManager chat;

    public Test() {
        this.cmd = "timetest";
        chat = new ChatManager();
    }

    public void execute(CommandSender sender, String[] args) {
        //calculateCurrent();
        if(!(sender instanceof Player)){
            chat.sendMessage(sender, "&cYou must be a player to do this.", true);
            return;
        }
        Player p = (Player) sender;
        PrisonPlayer pp = PrisonCore.getInstance().getPM().getPlayer(p);

        chat.sendMessage(sender, "&aSession online time: &2"+pp.getTimeLogger().calculateCurrent(), true);
        chat.sendMessage(sender, "&aTotal online time: &2"+pp.getTimeLogger().calculateCurrent()+pp.getOnlineTime(), true);
    }
}
