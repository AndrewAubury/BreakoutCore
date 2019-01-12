package net.breakoutinc.prisoncore.islands.objects;

import lombok.Data;
import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import org.bukkit.Location;

import java.util.UUID;

@Data
public class Island {
    protected PrisonCore main;

    private Location teleportLocation;
    private Location boatLocation;


    public Island(UUID uid){


    }
}
