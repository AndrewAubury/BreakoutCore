package net.breakoutinc.prisoncore.islands.objects;


import net.breakoutinc.prisoncore.PrisonCore;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


public class IslandSQLLite extends IslandDatabase{
    protected String dbname;
    public IslandSQLLite(PrisonCore instance){
        super(instance);
        dbname = "islands"; // Set the table name here e.g player_kills
    }

    private String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS islands (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `UUID` VARCHAR(36) NOT NULL , `Owner` VARCHAR(36) NOT NULL , `X` INT NOT NULL , `Y` INT NOT NULL , `Members` LONGTEXT NOT NULL , `ChunkMinX` INT NOT NULL , `ChunkMinZ` INT NOT NULL , `ChunkMaxX` INT NOT NULL , `ChunkMaxZ` INT NOT NULL , `IslandMineEnabled` BOOLEAN NOT NULL DEFAULT FALSE , `TeleportX` DECIMAL NOT NULL , `TeleportY` DECIMAL NOT NULL , `TeleportZ` DECIMAL NOT NULL , `TeleportYaw` DECIMAL NOT NULL , `TeleportPitch` DECIMAL NOT NULL , `BoatX` INT NOT NULL , `BoatY` INT NOT NULL , `BoatZ` INT NOT NULL , `BoatYaw` INT NOT NULL , `BoatPitch` INT NOT NULL , `MineMinX` INT NOT NULL , `MineMinY` INT NOT NULL , `MineMinZ` INT NOT NULL , `MineMaxX` INT NOT NULL , `MineMaxY` INT NOT NULL , `MineMaxZ` INT NOT NULL, `DockMinX` INT NOT NULL , `DockMinY` INT NOT NULL , `DockMinZ` INT NOT NULL , `DockMaxX` INT NOT NULL , `DockMaxY` INT NOT NULL , `DockMaxZ` INT NOT NULL);";


    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}