package net.breakoutinc.prisoncore.objects;

import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Date;

public class TimePlayed {
    private Player p;
    private PrisonPlayer prisonPlayer;
    private long startTime;
    private long endTime;
    private long secondsOnline;

    public TimePlayed(PrisonPlayer prisonPlayerLocal) {
        this.prisonPlayer = prisonPlayerLocal;
        this.prisonPlayer.setTimeLogger(this);
        this.p = prisonPlayerLocal.getPlayer();
    }
    public void StartRecording() {
        startTime = new Date().getTime();
    }

    public long calculateCurrent(){
        endTime = new Date().getTime();
        return endTime - startTime;
    }

    public long StopRecording() {
        endTime = new Date().getTime();
        secondsOnline = endTime - startTime;
        return secondsOnline;
    }

    public long getTime(){
        return secondsOnline;
    }

}
