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

public class AddCommand extends IslandCommand {
    private ChatManager chat;

    public AddCommand() {
        this.cmd = new String[]{
                "info",
        };
        this.permission = "prisoncore.island.members";
        this.desc = "Add a player to your island";

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
            Island i = IslandManager.getInstance().getIsland(p.getLocation());
            if (!p.hasPermission("islands.build.others")) {
                i = null;
            }
            UUID islandID = (i == null ? IslandManager.getInstance().getDb().getIslandUUIDFromPlayer(p) : i.getIslandUUID());
            if (islandID == null) {
                chat.sendMessage(p, "&cThere is no island bound to your account!", true);
            } else {
                if (IslandManager.getInstance().getIslandUUIDMapping().containsKey(islandID)) {
                    Island island = IslandManager.getInstance().getIslandUUIDMapping().get(islandID);

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

                Island island = IslandManager.getInstance().getIslandUUIDMapping().get(IslandUUID);
                ChatManager c = new ChatManager();
                List<String> lines = new ArrayList<String>();

                lines.add("&3X: " + island.getX());
                lines.add("&3Y: " + island.getY());
                lines.add("&3Owner: " + island.getOwnerName());
                lines.add("&3Members: " + (island.getMembers().size() == 0 ? "&cNone" : "&a" + island.getMembers().size()));
                lines.add("&3Island Mine: " + (island.isIslandMineEnabled() ? "&cDisabled" : "&aEnabled"));

                c.sendList(p, lines);
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
