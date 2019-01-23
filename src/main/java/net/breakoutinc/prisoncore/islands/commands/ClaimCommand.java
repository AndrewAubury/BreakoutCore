package net.breakoutinc.prisoncore.islands.commands;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.islands.objects.Island;
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
import java.util.UUID;

public class ClaimCommand extends IslandCommand {
    private ChatManager chat;

    public ClaimCommand() {
        this.cmd = new String[]{
                "claim",
                "auto",
                "a"
        };
        this.permission = "prisoncore.island.claim";
        this.desc = "Claim your island";

        chat = new ChatManager();
    }

    public void execute(CommandSender sender, String label, String[] args) {
        //calculateCurrent();
        if (!(sender instanceof Player)) {
            chat.sendMessage(sender, "&cYou must be a player to do this.", true);
            return;
        }
        Player p = (Player) sender;

        if (IslandManager.getInstance().getDb().getIslandUUIDFromPlayer(p) != null) {
            chat.sendMessage(p, "&cYou can only have one island", true);
            return;
        }

        PrisonPlayer pp = PrisonCore.getInstance().getPM().getPlayer(p);
        FileConfiguration config = IslandManager.getInstance().getIslandConfig().getConfig();

        String worldName = config.getString("island-world");

        World w = PrisonCore.getInstance().getServer().getWorld(worldName);

        int x = IslandManager.getInstance().getNextX();
        int z = IslandManager.getInstance().getNextZ();

        Location tmploc = IslandManager.getInstance().calculateCenterForIsland(w, x, z);
        Chunk c = tmploc.getChunk();

        if (!c.isLoaded()) {
            c.load();
            while (!c.isLoaded()) {

            }
        }
        Location loc = c.getBlock(7, config.getInt("water-level"), 7).getLocation();
        IslandManager.getInstance().pasteIsland(loc);

        chat.sendMessage(p, "&aTeleported to your brand new Island", true);

        Location startLoc = loc.clone().subtract(40, 40, 40);
        Location endLoc = loc.clone().add(40, 40, 40);
        List<Block> markers = blocksFromTwoPoints(startLoc, endLoc);


        Location boatSpawn = null;
        Location teleport = null;
        Location dockmin = null;
        Location dockmax = null;
        Location minemin = null;
        Location minemax = null;

        for (Block b : markers) {
            Wool wool = (Wool) b.getState().getData();
            if (wool.getColor().equals(DyeColor.LIGHT_BLUE)) {
                boatSpawn = b.getLocation().clone().add(0.5, 0, 0.5);
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

        float yaw = (float) Math.atan((boatSpawn.getY() - teleport.getY()) / (boatSpawn.getX() - teleport.getX()));
        teleport.setYaw(yaw);


        int offset = ((config.getInt("island-chunks") - 1) / 2);

        Island i = new Island(-1, UUID.randomUUID(), p.getUniqueId(), x, z, c.getX() - offset, c.getX() + offset, c.getZ() - offset, c.getZ() + offset, teleport, boatSpawn, minemin, minemax, dockmin, dockmax, false, "[]");
        p.teleport(i.getTeleportLocation());
        boatSpawn.getWorld().spawnEntity(boatSpawn, EntityType.BOAT);
        i.save();
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
