package net.breakoutinc.prisoncore.islands.objects;

import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import com.sk89q.worldedit.regions.Region;
import lombok.Data;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.managers.ChatManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Island {
    protected PrisonCore main;

    private int id = -1;
    private UUID IslandUUID;
    private UUID owner;
    private int X;
    private int Y;
    private int ChunkMinX;
    private int ChunkMaxX;
    private int ChunkMinZ;
    private int ChunkMaxZ;
    private List<UUID> members;
    private Location teleportLocation;
    private Location boatLocation;
    private Location MineMin;
    private Location MineMax;

    private Location DockMin;
    private Location DockMax;

    private boolean IslandMineEnabled;

    public Island(int id, UUID islandUUID, UUID owner, int x, int y, int chunkMinX, int chunkMaxX, int chunkMinZ, int chunkMaxZ, Location teleportLocation, Location boatLocation, Location mineMin, Location mineMax, Location dockMin, Location dockMax, boolean islandMineEnabled, String MembersJSON) {
        this.main = PrisonCore.getInstance();
        this.id = id;
        IslandUUID = islandUUID;
        this.owner = owner;
        X = x;
        Y = y;
        ChunkMinX = chunkMinX;
        ChunkMaxX = chunkMaxX;
        ChunkMinZ = chunkMinZ;
        ChunkMaxZ = chunkMaxZ;
        JSONArray jsonArray = new JSONArray(MembersJSON);

        List<UUID> uuids = new ArrayList<UUID>();
        for (int i = 0; i < jsonArray.length(); i++) {
            uuids.add(UUID.fromString(jsonArray.getString(i)));
        }

        this.members = uuids;

        this.teleportLocation = teleportLocation;
        this.boatLocation = boatLocation;
        MineMin = mineMin;
        MineMax = mineMax;
        DockMin = dockMin;
        DockMax = dockMax;
        IslandMineEnabled = islandMineEnabled;

        IslandManager.getInstance().getIslandUUIDMapping().put(islandUUID, this);
        IslandManager.getInstance().storeIsland(X, Y, this);
    }

    public String jsonMembers() {
        JSONArray jsonArray = new JSONArray();
        for (UUID member : members) jsonArray.put(member);
        return jsonArray.toString();
    }

    public void save() {
        IslandManager.getInstance().getDb().saveIsland(this);
    }

    public void addMember(Player p) {
        members.add(p.getUniqueId());
        save();
    }

    public void removeMember(Player p) {
        if (!members.contains(p.getUniqueId())) {
            return;
        }
        members.remove(p.getUniqueId());
        save();
    }

    public void remove() {
        World w = IslandManager.getInstance().getWorld();
        Chunk c = w.getChunkAt(getChunkMinX(), getChunkMinZ());
        Chunk c2 = w.getChunkAt(getChunkMinZ(), getChunkMaxZ());


        Selection selection = new CuboidSelection(IslandManager.getInstance().getWorld(), c.getBlock(0, 0, 0).getLocation(), c2.getBlock(15, 15, 15).getLocation());
        try {
            Region region = selection.getRegionSelector().getRegion();
            region.getWorld().regenerate(region, WorldEdit.getInstance().getEditSessionFactory().getEditSession(region.getWorld(), -1));
        } catch (IncompleteRegionException e) {
        }
    }

    public CuboidSelection getInsideMine() {

        Location max = getMineMax().clone().subtract(1, 4, 1);
        Location min = getMineMin().clone().add(1, 2, 1);

        return new CuboidSelection(IslandManager.getInstance().getWorld(), min, max);
    }

    public void regen() {
        remove();
        Location tmploc = IslandManager.getInstance().calculateCenterForIsland(teleportLocation.getWorld(), getX(), getY());
        Chunk c = tmploc.getChunk();

        if (!c.isLoaded()) {
            c.load();
            while (!c.isLoaded()) {

            }
        }
        Location loc = c.getBlock(7, IslandManager.getInstance().getIslandConfig().getConfig().getInt("water-level"), 7).getLocation();
        IslandManager.getInstance().pasteIsland(loc);
    }

    public void delete() {
        remove();

        IslandManager.getInstance().getDb().deleteIsland(getId());
        IslandManager.getInstance().getIslandUUIDMapping().remove(getIslandUUID());
        IslandManager.getInstance().getIslandMapping().get(X).remove(Y);
    }

    public void enableMine() {
        if (PrisonCore.getInstance().getServer().getOfflinePlayer(owner).isOnline()) {
            Player p = PrisonCore.getInstance().getServer().getOfflinePlayer(owner).getPlayer();
            new ChatManager().sendMessage(p, "&aYour island mine has been enabled", true);
        }
        Selection selection = getInsideMine();
        EditSession editSession = new EditSessionBuilder(BukkitUtil.getLocalWorld(getDockMin().getWorld())).fastmode(true).build();


        Region region = null;

        try {
            region = selection.getRegionSelector().getRegion();
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }

        editSession.setBlocks(region, new BaseBlock(0));

        editSession.flushQueue();

        setIslandMineEnabled(true);
        save();

    }

    public void disableMine() {
        if (PrisonCore.getInstance().getServer().getOfflinePlayer(owner).isOnline()) {
            Player p = PrisonCore.getInstance().getServer().getOfflinePlayer(owner).getPlayer();
            new ChatManager().sendMessage(p, "&cYour island mine has been disabled", true);
        }
        Selection selection = getInsideMine();
        EditSession editSession = new EditSessionBuilder(BukkitUtil.getLocalWorld(getDockMin().getWorld())).fastmode(true).build();
        List<BlockChance> blocks = new ArrayList<BlockChance>();
        blocks.add(new BlockChance(new BaseBlock(12), 50));
        blocks.add(new BlockChance(new BaseBlock(4), 25));
        blocks.add(new BlockChance(new BaseBlock(98, 2), 25));

        RandomFillPattern pattern = new RandomFillPattern(blocks);

        Region region = null;

        try {
            region = selection.getRegionSelector().getRegion();
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }

        editSession.setBlocks(region, pattern);

        editSession.flushQueue();

        setIslandMineEnabled(false);
        save();

    }

    public String getOwnerName() {
        return PrisonCore.getInstance().getServer().getOfflinePlayer(this.getIslandUUID()).getName();
    }

    public void resetMine() {
        OfflinePlayer op = PrisonCore.getInstance().getServer().getOfflinePlayer(owner);
        if (!op.isOnline()) {
            return;
        }

        Selection selection = getInsideMine();
        EditSession editSession = new EditSessionBuilder(BukkitUtil.getLocalWorld(getDockMin().getWorld())).fastmode(true).build();
        List<BlockChance> blocks = new ArrayList<BlockChance>();
        blocks.add(new BlockChance(new BaseBlock(133), 70));
        blocks.add(new BlockChance(new BaseBlock(129), 30));

        RandomFillPattern pattern = new RandomFillPattern(blocks);

        Region region = null;

        try {
            region = selection.getRegionSelector().getRegion();
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }

        editSession.setBlocks(region, pattern);

        editSession.flushQueue();
    }

    private List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        List<Block> blocks = new ArrayList<Block>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }
}
