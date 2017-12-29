package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Andrew on 25/11/2017.
 */
public class PlayerManager {
    private HashMap<Player,PrisonPlayer> playermap;

    public PlayerManager() {
        playermap = new HashMap<Player, PrisonPlayer>();
    }

    public PrisonPlayer getPlayer(Player p){
        if(!playermap.containsKey(p)){
            playermap.put(p,new PrisonPlayer(p));
        }
        return playermap.get(p);
    }

    public void saveAll(){
        for(PrisonPlayer p : playermap.values()){
            p.save();
        }
    }
}
