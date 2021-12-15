package com.rezmicr.spigot.randomfighter;

import java.util.*;
import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Location;

public class RandomFighter extends JavaPlugin {
    private final Map<String, RandFightMinigame> games = new HashMap<String, RandFightMinigame>();
    private final List<Connection> connections = new ArrayList<Connection>(); 
    private GameCommands cmdMgr;
    private RandomTypes randTypes;
    private RFScoreBoard scoreBoard;
    private Map<String,GameRoom> rooms;
    private final String DB = "jdbc:sqlite:plugins/RandomFighter/randfight.db";

    @Override
    public void onEnable() {
        // create RandomFighter folder if it doesn't exist
        new File("plugins/RandomFighter").mkdirs();
        // create a connection pool
        try {
            for (int i = 0; i < 10; i++)
                connections.add(DriverManager.getConnection(DB));
            getLogger().info("Created DB connections pool");
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        Connection con = connections.get(connections.size()-1);            
        randTypes = new RandomTypes();
        cmdMgr = new GameCommands(games,connections,loadRooms(con),this,randTypes); 
        connections.remove(con);
        con = connections.get(connections.size()-1);            
        scoreBoard = new RFScoreBoard(con,this); // create scoreboard
        connections.remove(con);
        scoreboardAllPlayers(); // adds scoreboard in case of plugin reload
        getServer().getPluginManager().registerEvents(new RandFightEvents(this, randTypes), this);
    }
    @Override
    public void onDisable() {
        Connection con = connections.get(connections.size()-1);
        scoreBoard.saveToDB(con,this);
        connections.remove(con);
        getLogger().info("Thanks for trying my plugin!");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmdMgr.runCommand(sender, cmd, label, args);
    }

    public RFScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    private Map<String,GameRoom> loadRooms(Connection con) {
        Map<String,GameRoom> DBRooms = new HashMap<String, GameRoom>();
        try {
        // Creates the ROOMS table if it doesn't exist
        if (!tableExists(con, "ROOMS")) {
            String createString = "CREATE TABLE ROOMS (NAME,XCORNER1,"+ 
                                  "YCORNER1,ZCORNER1,XCORNER2,YCORNER2,"+
                                  "ZCORNER2,PLAYSPNWX,PLAYSPNWY,PLAYSPNWZ,"+
                                  "ENMSPNWX,ENMSPNWY,ENMSPNWZ,WRLDNAME)";
            PreparedStatement createTable = con.prepareStatement(createString);
            createTable.executeUpdate();
        // Reads the ROOMS table and creates the room objects
        } else {
            String selectString = "SELECT * FROM ROOMS";
            PreparedStatement selectTable = con.prepareStatement(selectString);
            ResultSet rs = selectTable.executeQuery();
            while(rs.next()) {
                // Using the worldname as UUID method failed sometimes
                // However, if the world is managed by another plugin like 
                // Multiverse-Core, those plugins will have to be softdepent
                World world = Bukkit.getWorld(rs.getString("WRLDNAME"));
                if (world != null) {
                Location loc1 = new Location(world,
                    rs.getDouble("XCORNER1"),
                    rs.getDouble("YCORNER1"),
                    rs.getDouble("ZCORNER1"));
                Location loc2 = new Location(world,
                    rs.getDouble("XCORNER2"),
                    rs.getDouble("YCORNER2"),
                    rs.getDouble("ZCORNER2"));                
                Location loc3 = new Location(world,
                    rs.getDouble("PLAYSPNWX"),
                    rs.getDouble("PLAYSPNWY"),
                    rs.getDouble("PLAYSPNWZ"));
                Location loc4 = new Location(world,
                    rs.getDouble("ENMSPNWX"),
                    rs.getDouble("ENMSPNWY"),
                    rs.getDouble("ENMSPNWZ"));
                GameRoom tempRoom = new GameRoom(rs.getString("NAME"),loc1,loc2,loc3,loc4);
                DBRooms.put(rs.getString("NAME"),tempRoom);
                } else {
                    System.err.println("World with name "+rs.getString("WRLDNAME")+" wasn't found");
                }
            }
            getLogger().info("Loaded existing rooms");
            // TODO: manage connections more intelligently
            con.close();
            connections.remove(con);   
        }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
            DBRooms = new HashMap<String,GameRoom>();
        }
        return DBRooms;
    }

    private void scoreboardAllPlayers() {
        Object[] onlinePlayers = Bukkit.getOnlinePlayers().toArray();
        for (int i = 0; i < onlinePlayers.length - 1; i++) {
            ((Player) onlinePlayers[i]).setScoreboard(scoreBoard.getScoreboard());
        }
    }

    private boolean tableExists(Connection con, String name) throws SQLException {
        DatabaseMetaData meta = con.getMetaData();
        ResultSet rs = meta.getTables(null, null, name, new String[] {"TABLE"});
        return rs.next();
    }
}

