package net.breakoutinc.prisoncore.islands;


import com.boydti.fawe.wrappers.WorldWrapper;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.*;
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

public class IslandManager {
    private static IslandManager instance;
    public static IslandManager getInstance(){
        if(instance == null){
            instance = new IslandManager();
        }
        return instance;
    }
    private PrisonCore prisonCore;

    HashMap<Integer, HashMap<Integer, Island>> islandMapping;

    @Getter private Config islandConfig;
    @Getter private IslandDatabase db;

    public IslandManager(){
        prisonCore = PrisonCore.getInstance();
        islandConfig = new Config(prisonCore.getDataFolder().getPath(), "islands.yml");
        this.db = new IslandSQLLite(prisonCore);
        this.db.load();
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
        if(((loc.getChunk().getX()+"").endsWith("0")||(loc.getChunk().getX()+"").endsWith("1")) && ((loc.getChunk().getZ()+"").endsWith("0")||(loc.getChunk().getZ()+"").endsWith("1"))){
            return false;
        }
        return true;
    }

}
