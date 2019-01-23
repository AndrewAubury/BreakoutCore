package net.breakoutinc.prisoncore.islands.commands;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.islands.objects.Island;
import net.breakoutinc.prisoncore.islands.objects.IslandCommand;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlotMineCommand extends IslandCommand {
    private ChatManager chat;

    public PlotMineCommand() {
        this.cmd = new String[]{
                "mine",
        };
        this.permission = "prisoncore.island.admin.mine";
        this.desc = "Manage Island Mines";

        this.subcommands = new String[]{
                "enable",
                "disable",
                "reset"
        };

        chat = new ChatManager();
    }

    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 3) {
            chat.sendMessage(sender, "&cUsage: /" + label + " " + cmd[0] + " [enable/disable/reset] [playername]", true);
            return;
        }

        String name = args[2];

        if (PrisonCore.getInstance().getServer().getOfflinePlayer(name).hasPlayedBefore()) {
            UUID uuid = PrisonCore.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
            UUID IslandUUID = IslandManager.getInstance().getDb().getIslandIDFromPlayerUUID(uuid);
            if (IslandUUID == null) {
                chat.sendMessage(sender, "&cThat user does not have a Island", true);
                return;
            }

            Island i = IslandManager.getInstance().getIslandUUIDMapping().get(IslandUUID);

            if (args[1].equalsIgnoreCase("enable")) {
                i.enableMine();
                i.resetMine();

                chat.sendMessage(sender, "&aYou have enabled " + name + "'s Island Mine", true);
            } else if (args[1].equalsIgnoreCase("disable")) {
                i.disableMine();
                chat.sendMessage(sender, "&cYou have disabled " + name + "'s Island Mine", true);
            } else if (args[1].equalsIgnoreCase("reset")) {
                if (i.isIslandMineEnabled()) {
                    i.resetMine();
                    chat.sendMessage(sender, "&aYou have reset " + name + "'s Island Mine", true);
                } else {
                    chat.sendMessage(sender, "&c" + name + " doesnt not have a active Island Mine", true);
                }
            } else {
                chat.sendMessage(sender, "&cUsage: /" + label + " " + cmd[0] + " [enable/disable/reset] [playername]", true);
            }

        } else {
            chat.sendMessage(sender, "&cThat user has never played on the server", true);
            return;
        }

    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> options = new ArrayList<String>();
        if (args.length == 2) {
            for (String s : this.subcommands) {
                options.add(s);
            }
        } else if (args.length == 3) {
            for (Player p : PrisonCore.getInstance().getServer().getOnlinePlayers()) {
                options.add(p.getName());
            }
        }
        return options;
    }
}
