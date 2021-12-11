package com.rezmicr.spigot.randomfighter;

import java.util.*;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.DatabaseMetaData;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.Location;

public class RandomFighter extends JavaPlugin {
    // Fields
    protected Map<String, RandFightMinigame> games = new HashMap<String, RandFightMinigame>();
    protected List<Connection> connections = new ArrayList<Connection>(); 
    protected GameCommands cmdMgr;
    protected Map<String,GameRoom> rooms;
    private final String DB = "jdbc:sqlite:plugins/RandomFighter/randfight.db";
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RandFightEvents(this), this);
        // check if the RandomFighter folder exists
        new File("plugins/RandomFighter").mkdirs();
        // create a connection pool
        try {
            for (int i = 0; i < 10; i++) {
                connections.add(DriverManager.getConnection(DB));
            }
            getLogger().info("Created DB connections pool");
            Connection con = connections.get(connections.size()-1);            
            cmdMgr = new GameCommands(games,connections,loadRooms(con),this); 
        } catch(SQLException e) {
            // if the error message is "out of memory" it probably means no database file is found
            System.err.println(e.getMessage());
        }
    }
    @Override
    public void onDisable() {
        getLogger().info("Thanks for trying my plugin!");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmdMgr.runCommand(sender, cmd, label, args);
    }
    private Map<String,GameRoom> loadRooms(Connection con) {
        Map<String,GameRoom> DBRooms = new HashMap<String, GameRoom>();
        try {
        // load em up
        Statement statement = con.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.
        if (!tableExists(con, "ROOMS")) {
            statement.executeUpdate("CREATE TABLE ROOMS (NAME,XCORNER1,"+ 
                                    "YCORNER1,ZCORNER1,XCORNER2,YCORNER2,"+
                                    "ZCORNER2,PLAYSPNWX,PLAYSPNWY,PLAYSPNWZ,"+
                                    "ENMSPNWX,ENMSPNWY,ENMSPNWZ,WRLDNAME)");
        } else {
            ResultSet rs = statement.executeQuery("SELECT * FROM ROOMS");
            while(rs.next()) {
                // read the result set
                World world = Bukkit.getWorld(rs.getString("WRLDNAME"));
                if (world != null) {
                Location loc1 = new Location(world,
                    Double.parseDouble(rs.getString("XCORNER1")),
                    Double.parseDouble(rs.getString("YCORNER1")),
                    Double.parseDouble(rs.getString("ZCORNER1")));
                Location loc2 = new Location(world,
                    Double.parseDouble(rs.getString("XCORNER2")),
                    Double.parseDouble(rs.getString("YCORNER2")),
                    Double.parseDouble(rs.getString("ZCORNER2")));                
                Location loc3 = new Location(world,
                    Double.parseDouble(rs.getString("PLAYSPNWX")),
                    Double.parseDouble(rs.getString("PLAYSPNWY")),
                    Double.parseDouble(rs.getString("PLAYSPNWZ")));
                Location loc4 = new Location(world,
                    Double.parseDouble(rs.getString("ENMSPNWX")),
                    Double.parseDouble(rs.getString("ENMSPNWY")),
                    Double.parseDouble(rs.getString("ENMSPNWZ")));
                GameRoom tempRoom = new GameRoom(rs.getString("NAME"),loc1,loc2,loc3,loc4);
                DBRooms.put(rs.getString("NAME"),tempRoom);
                } else {
                    System.err.println("World with name "+rs.getString("WRLDNAME")+" wasn't found");
                }
            }
            getLogger().info("Loaded existing rooms");
            con.close();
            connections.remove(con);   
        }
        } catch(SQLException e) {
            // if the error message is "out of memory" it probably means no database file is found
            System.err.println(e.getMessage());
            DBRooms = new HashMap<String,GameRoom>();
        }
        return DBRooms;
    }
    // TODO: clean this, for the love of god
    boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
        return resultSet.next();
    }
}

