package net.breakoutinc.prisoncore.islands.objects;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.managers.Error;
import net.breakoutinc.prisoncore.managers.Errors;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public abstract class IslandDatabase {
    PrisonCore plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    private String table = "islands";

    public IslandDatabase(PrisonCore instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + ";");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Location teleportLocation = new Location(IslandManager.getInstance().getWorld(), rs.getDouble("TeleportX"), rs.getDouble("TeleportY"), rs.getDouble("TeleportZ"), rs.getFloat("TeleportYaw"), rs.getFloat("TeleportPitch"));
                Location boatLocation = new Location(IslandManager.getInstance().getWorld(), rs.getDouble("BoatX"), rs.getDouble("BoatY"), rs.getDouble("BoatZ"), rs.getFloat("BoatYaw"), rs.getFloat("BoatPitch"));

                Location mineMin = new Location(IslandManager.getInstance().getWorld(), rs.getDouble("MineMinX"), rs.getDouble("MineMinY"), rs.getDouble("MineMinZ"));
                Location mineMax = new Location(IslandManager.getInstance().getWorld(), rs.getDouble("MineMaxX"), rs.getDouble("MineMaxY"), rs.getDouble("MineMaxZ"));

                Location dockMin = new Location(IslandManager.getInstance().getWorld(), rs.getDouble("DockMinX"), rs.getDouble("DockMinY"), rs.getDouble("DockMinZ"));
                Location dockMax = new Location(IslandManager.getInstance().getWorld(), rs.getDouble("DockMaxX"), rs.getDouble("DockMaxY"), rs.getDouble("DockMaxZ"));

                new Island(
                        rs.getInt("id"),
                        UUID.fromString(rs.getString("UUID")),
                        UUID.fromString(rs.getString("Owner")),
                        rs.getInt("X"),
                        rs.getInt("Y"),
                        rs.getInt("ChunkMinX"),
                        rs.getInt("ChunkMaxX"),
                        rs.getInt("ChunkMinZ"),
                        rs.getInt("ChunkMaxZ"),
                        teleportLocation,
                        boatLocation,
                        mineMin,
                        mineMax,
                        dockMin,
                        dockMax,
                        rs.getBoolean("IslandMineEnabled"),
                        rs.getString("Members")
                );
            }

            close(ps,rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    public void deleteIsland(int id) {
        String sql = "DELETE FROM " + table + " WHERE id=" + id + ";";
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = getSQLConnection();
            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    public UUID getIslandUUIDFromPlayer(Player p) {
        return this.getIslandIDFromPlayerUUID(p.getUniqueId());
    }

    public UUID getIslandIDFromPlayerUUID(UUID p) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE Owner = '" + p.toString() + "';");

            rs = ps.executeQuery();
            while(rs.next()){
                if (rs.getString("Owner").equalsIgnoreCase(p.toString())) { // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return UUID.fromString(rs.getString("UUID")); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return null;
    }

    // Now we need methods to save things to the database
    public void saveIsland(Island island) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (`id`, `UUID`, `Owner`, `X`, `Y`, `Members`, `ChunkMinX`, `ChunkMinZ`, `ChunkMaxX`, `ChunkMaxZ`, `IslandMineEnabled`, `TeleportX`, `TeleportY`, `TeleportZ`, `TeleportYaw`, `TeleportPitch`, `BoatX`, `BoatY`, `BoatZ`, `BoatYaw`, `BoatPitch`, `MineMinX`, `MineMinY`, `MineMinZ`, `MineMaxX`, `MineMaxY`, `MineMaxZ`, `DockMinX`, `DockMinY`, `DockMinZ`, `DockMaxX`, `DockMaxY`, `DockMaxZ`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            if (island.getId() == -1) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, island.getId());
            }
            ps.setString(2, island.getIslandUUID().toString());
            ps.setString(3, island.getOwner().toString());
            ps.setInt(4, island.getX());
            ps.setInt(5, island.getY());
            ps.setString(6, island.jsonMembers());
            ps.setInt(7, island.getChunkMinX());
            ps.setInt(8, island.getChunkMinZ());
            ps.setInt(9, island.getChunkMaxX());
            ps.setInt(10, island.getChunkMaxZ());
            ps.setBoolean(11, island.isIslandMineEnabled());

            ps.setDouble(12, island.getTeleportLocation().getX());
            ps.setDouble(13, island.getTeleportLocation().getY());
            ps.setDouble(14, island.getTeleportLocation().getZ());
            ps.setFloat(15, island.getTeleportLocation().getYaw());
            ps.setFloat(16, island.getTeleportLocation().getPitch());

            ps.setDouble(17, island.getBoatLocation().getX());
            ps.setDouble(18, island.getBoatLocation().getY());
            ps.setDouble(19, island.getBoatLocation().getZ());
            ps.setFloat(20, island.getBoatLocation().getYaw());
            ps.setFloat(21, island.getBoatLocation().getPitch());

            ps.setInt(22, island.getMineMin().getBlockX());
            ps.setInt(23, island.getMineMin().getBlockY());
            ps.setInt(24, island.getMineMin().getBlockZ());

            ps.setInt(25, island.getMineMax().getBlockX());
            ps.setInt(26, island.getMineMax().getBlockY());
            ps.setInt(27, island.getMineMax().getBlockZ());

            ps.setInt(28, island.getDockMin().getBlockX());
            ps.setInt(29, island.getDockMin().getBlockY());
            ps.setInt(30, island.getDockMin().getBlockZ());

            ps.setInt(31, island.getDockMax().getBlockX());
            ps.setInt(32, island.getDockMax().getBlockY());
            ps.setInt(33, island.getDockMax().getBlockZ());

            ps.executeUpdate();

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            } finally {
                return;
            }

        }

    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}
