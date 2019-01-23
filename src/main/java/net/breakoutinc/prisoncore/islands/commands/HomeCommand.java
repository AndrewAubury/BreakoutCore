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

public class HomeCommand extends IslandCommand {
    private ChatManager chat;

    public HomeCommand() {
        this.cmd = new String[]{
                "home",
                "h",
        };
        this.permission = "prisoncore.island.home";
        this.desc = "Goto your island";

        chat = new ChatManager();
    }

    public void execute(CommandSender sender, String label, String[] args) {
        //calculateCurrent();
        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, "&cYou must be a player to do this.", true);
            return;
        }
        Player p = (Player) sender;
        if (args.length == 1) {
            UUID islandID = IslandManager.getInstance().getDb().getIslandUUIDFromPlayer(p);
            if (islandID == null) {
                chat.sendMessage(p, "&cThere is no island bound to your account!", true);
            } else {
                if (IslandManager.getInstance().getIslandUUIDMapping().containsKey(islandID)) {
                    Island island = IslandManager.getInstance().getIslandUUIDMapping().get(islandID);
                    p.teleport(island.getTeleportLocation());
                    chat.sendMessage(p, "&aTeleported To Your Island!", true);
                } else {
                    chat.sendMessage(p, "&cThere was a island bound to your account but not stored in the IslandManager!", true);
                }
            }
        } else if (args.length == 2) {
            String name = args[1];
            if (PrisonCore.getInstance().getServer().getOfflinePlayer(name).hasPlayedBefore()) {
                UUID uuid = PrisonCore.getInstance().getServer().getOfflinePlayer(name).getUniqueId();
                name = PrisonCore.getInstance().getServer().getOfflinePlayer(name).getName();
                UUID IslandUUID = IslandManager.getInstance().getDb().getIslandIDFromPlayerUUID(uuid);
                if (IslandUUID == null) {
                    chat.sendMessage(sender, "&cThat user does not have a Island", true);
                    return;
                }

                Island i = IslandManager.getInstance().getIslandUUIDMapping().get(IslandUUID);
                p.teleport(i.getTeleportLocation());
                chat.sendMessage(p, "&aTeleported To " + name + "'s Island!", true);
            } else {
                chat.sendMessage(sender, "&cThat user has never played on the server", true);
                return;
            }
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 2) {
            for (Player p : PrisonCore.getInstance().getServer().getOnlinePlayers()) {
                options.add(p.getName());
            }
        }
        return options;
    }
}
