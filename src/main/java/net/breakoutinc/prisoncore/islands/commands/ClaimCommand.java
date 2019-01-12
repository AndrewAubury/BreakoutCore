package net.breakoutinc.prisoncore.islands.commands;

import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import net.breakoutinc.prisoncore.Command;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.islands.objects.IslandCommand;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

public class ClaimCommand extends IslandCommand {
    private ChatManager chat;

    public ClaimCommand() {
        this.cmd = "claim";
        this.permission = "prisoncore.island.claim";
        this.desc = "Claim your island";

        chat = new ChatManager();
    }

    public void execute(CommandSender sender, String[] args) {
        //calculateCurrent();
        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, "&cYou must be a player to do this.", true);
            return;
        }
        if(args.length != 3){
            chat.sendMessage(sender, "&cUsage: /is claim <X> <Y>", true);
            return;
        }
        Player p = (Player) sender;
        PrisonPlayer pp = PrisonCore.getInstance().getPM().getPlayer(p);
        FileConfiguration config = IslandManager.getInstance().getIslandConfig().getConfig();

        String worldName = config.getString("island-world");

        World w = PrisonCore.getInstance().getServer().getWorld(worldName);

        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);

        Location tmploc = IslandManager.getInstance().calculateCenterForIsland(w,x,y);
        Chunk c = tmploc.getChunk();

        if (!c.isLoaded()) {
            c.load();
            while (!c.isLoaded()) {

            }
        }
        Location loc = c.getBlock(7, config.getInt("water-level"), 7).getLocation();
        IslandManager.getInstance().pasteIsland(loc);

        chat.sendMessage(p, "&aYou should now be on a island", true);
        chat.sendMessage(p, "&cLooking for marker blocks", true);
        Location startLoc = loc.clone().subtract(40, 40, 40);
        Location endLoc = loc.clone().add(40, 40, 40);
        List<Block> markers = blocksFromTwoPoints(startLoc, endLoc);
        chat.sendMessage(p, "&aFound " + markers.size() + " Markers", true);

        Location boatSpawn = null;
        Location teleport = null;
        Location dockmin = null;
        Location dockmax = null;
        Location minemin = null;
        Location minemax = null;

        for (Block b : markers) {
            Wool wool = (Wool) b.getState().getData();
            if (wool.getColor().equals(DyeColor.LIGHT_BLUE)) {
                boatSpawn = b.getLocation();
            } else if (wool.getColor().equals(DyeColor.MAGENTA)) { //Spawn Loc
                teleport = b.getLocation().clone().add(0.5,0,0.5);
            } else if (wool.getColor().equals(DyeColor.ORANGE)) { //Dock Locations (expect 2)
                if(dockmin == null){
                    dockmin = b.getLocation();
                }else{
                    dockmax = b.getLocation();
                }
            } else if (wool.getColor().equals(DyeColor.LIME)){ //Mine Locations (expect 2)
                if(minemin == null){
                    minemin = b.getLocation();
                }else{
                    minemax = b.getLocation();
                }
            }
            b.setType(Material.AIR);
    }

        float yaw  =  (float) Math.atan((boatSpawn.getY()-teleport.getY())/(boatSpawn.getX()-teleport.getX()))*-1.50f;
        teleport.setYaw(yaw);

        chat.sendMessage(p,locationToString("Boat",boatSpawn),true);
        chat.sendMessage(p,locationToString("Teleport",teleport),true);
        chat.sendMessage(p,locationToString("Dock 1",dockmin),true);
        chat.sendMessage(p,locationToString("Dock 2",dockmax),true);
        chat.sendMessage(p,locationToString("Mine 1",minemin),true);
        chat.sendMessage(p,locationToString("Mine 2",minemax),true);
        p.teleport(teleport);
        boatSpawn.getWorld().spawnEntity(boatSpawn, EntityType.BOAT);
}

public String locationToString(String name, Location loc){
        return "&3"+name+":"+" &3X:&b "+loc.getBlockX()+" &3Y:&b "+loc.getBlockY()+" &3Z:&b "+loc.getBlockZ();
}

    public List<Block> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    if(block.getType().equals(Material.WOOL)) {
                        blocks.add(block);
                    }
                    if(block.getType().equals(Material.HAY_BLOCK)){
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        return blocks;
    }
}
