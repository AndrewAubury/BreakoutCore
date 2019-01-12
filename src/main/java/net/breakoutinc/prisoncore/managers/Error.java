package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.PrisonCore;

import java.util.logging.Level;

public class Error {
    public static void execute(PrisonCore plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(PrisonCore plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}