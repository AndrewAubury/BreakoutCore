package net.breakoutinc.prisoncore.islands;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import lombok.Getter;
import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.objects.Island;
import net.breakoutinc.prisoncore.islands.objects.IslandDatabase;
import net.breakoutinc.prisoncore.islands.objects.IslandSQLLite;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class IslandManager {
    private static IslandManager instance;
    public static IslandManager getInstance(){
        if(instance == null){
            instance = new IslandManager();
        }
        return instance;
    }
    private PrisonCore prisonCore;

    @Getter
    HashMap<Integer, HashMap<Integer, Island>> islandMapping;
    @Getter
    HashMap<UUID, Island> islandUUIDMapping;

    @Getter private Config islandConfig;
    @Getter private IslandDatabase db;

    public IslandManager(){
        prisonCore = PrisonCore.getInstance();
        islandConfig = new Config(prisonCore.getDataFolder().getPath(), "islands.yml");
        islandUUIDMapping = new HashMap<>();
        islandMapping = new HashMap<>();

        PrisonCore.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(PrisonCore.getInstance(), getResetMineRunnable(), 120l * 20l, 120l * 20l);
    }


    public World getWorld() {
        String worldName = islandConfig.getConfig().getString("island-world");

        return PrisonCore.getInstance().getServer().getWorld(worldName);
    }

    public void loadDB() {
        this.db = new IslandSQLLite(prisonCore);
        this.db.load();
    }

    public void storeIsland(int X, int Z, Island island) {
        if (!islandMapping.containsKey(X)) {
            islandMapping.put(X, new HashMap<>());
        }

        islandMapping.get(X).put(Z, island);
    }

    public Island getIslandXY(int X, int Z) {
        if (!islandMapping.containsKey(X)) {
            return null;
        }
        if (!islandMapping.get(X).containsKey(Z)) {
            return null;
        } else {
            return islandMapping.get(X).get(Z);
        }
    }

    public boolean isXZSafe(int x, int z) {
        if (!islandMapping.containsKey(x)) {
            return true;
        }
        return !islandMapping.get(x).containsKey(z);
    }

    public int getNextX() {
        int BiggestX = 0;
        int BiggestZ = 0;


        for (int i : islandMapping.keySet()) {
            if (i > BiggestX) {
                BiggestX = i;
            }
        }
        if (islandMapping.containsKey(BiggestX)) {
            for (int i : islandMapping.get(BiggestX).keySet()) {
                if (i > BiggestZ) {
                    BiggestZ = i;
                }
            }
        }


        if (BiggestX == BiggestZ) {
            return BiggestX + 1;
        }
        if (BiggestX > BiggestZ) {
            return BiggestX;
        }
        if (BiggestX < BiggestZ) {
            return BiggestZ;
        }

        if (BiggestX < BiggestZ) {
            return BiggestX + 1;
        } else if (BiggestX == BiggestZ) {
            return BiggestX + 1;
        } else if (BiggestX > BiggestZ) {
            return BiggestX;
        }

        return BiggestX + 1; //Incase Error
    }

    public int getNextZ() {
        int BiggestX = 0;
        int BiggestZ = 0;

        for (int i : islandMapping.keySet()) {
            if (i > BiggestX) {
                BiggestX = i;
            }

        }
        if (islandMapping.containsKey(BiggestX)) {
            for (int ii : islandMapping.get(BiggestX).keySet()) {
                if (ii > BiggestZ) {
                    BiggestZ = ii;
                }
            }
        }
        if (BiggestX < BiggestZ) {
            return BiggestZ;
        } else if (BiggestX == BiggestZ) {
            return 0;
        } else if (BiggestX > BiggestZ) {
            return BiggestZ + 1;
        }

        return 1;
    }

    public Runnable getResetMineRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                IslandManager.getInstance().resetAllMines();
            }
        };
    }

    public void resetAllMines() {
        for (Island i : islandUUIDMapping.values()) {
            if (i.isIslandMineEnabled()) {
                i.resetMine();
            }
        }
    }

    public void pasteIsland(Location loc){
        File file = new File(prisonCore.getDataFolder().getPath(),"Schematics"+File.separator+ islandConfig.getConfig().getString("island-schematic"));

        //loc = loc.getChunk().getBlock(7,loc.getBlockY(),7).getLocation();

        boolean allowUndo = true;
        try {
            EditSession editSession = ClipboardFormat.findByFile(file).load(file).paste(BukkitUtil.getLocalWorld(loc.getWorld()), Vector.toBlockPoint(loc.getX(),islandConfig.getConfig().getDouble("water-level"),loc.getZ()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location calculateCenterForIsland(World w, int x, int y){
        int total = getIslandConfig().getConfig().getInt("island-chunks")+getIslandConfig().getConfig().getInt("spacer-chunks");
        x = (x*total)-5;
        y = (y*total)-5;
        return w.getChunkAt(x,y).getBlock(7,islandConfig.getConfig().getInt("water-level"),7).getLocation();
    }

    public boolean isValidIslandLocation(Location loc){
        return getIsland(loc) != null;
    }

    public Island getIsland(Location loc) {
        int CX = loc.getChunk().getX();
        int CZ = loc.getChunk().getZ();
        for (Island i : islandUUIDMapping.values()) {
            int X1 = i.getChunkMinX();
            int X2 = i.getChunkMaxX();
            int Z1 = i.getChunkMinZ();
            int Z2 = i.getChunkMaxZ();

            if ((CX > X1 && CX < X2) || CX == X1 || CX == X2) {
                if ((CZ > Z1 && CZ < Z2) || CZ == Z1 || CZ == Z2) {
                    return i;
                }
            }
        }
        return null;
    }



}
