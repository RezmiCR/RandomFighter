package com.rezmicr.spigot.randomfighter;

import java.util.HashMap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.DatabaseMetaData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.ScoreboardManager;

public class RFScoreBoard {
    
    private final Scoreboard sc = Bukkit.getScoreboardManager().getNewScoreboard();
    private final Objective obj;
    private final HashMap<String,Integer> scores = new HashMap<String,Integer>();

    public RFScoreBoard(Connection con,RandomFighter plugin) {
        obj = sc.registerNewObjective("rf_scoreboard","dummy","Random Fighter"); 
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        // build scores for each player
        loadFromDB(con,plugin);
    }
    
    private void loadFromDB(Connection con, RandomFighter plugin) {
        try {
        if (!tableExists(con, "PLAYERS")) {
            String createString = "CREATE TABLE PLAYERS (NAME,POINTS)";
            PreparedStatement createTable = con.prepareStatement(createString);
            createTable.executeUpdate();
        } else {
            String readString = "SELECT * FROM PLAYERS ORDER BY POINTS DESC";
            PreparedStatement readTable = con.prepareStatement(readString);
            ResultSet rs = readTable.executeQuery();
            while(rs.next()) {
                // read the result set and add them to the scores
                String player_name = rs.getString("NAME");
                Score temp_scr = obj.getScore(player_name);
                Integer player_score = Integer.valueOf(rs.getInt("POINTS"));
                temp_scr.setScore(player_score);
                scores.put(player_name,player_score);
                }
            }
            plugin.getLogger().info("Loaded existing scores");
            con.close();
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void saveToDB(Connection con, RandomFighter plugin) {
        String selectString = "SELECT * FROM PLAYERS WHERE NAME = ? ";
        String insertString = "INSERT INTO PLAYERS (NAME, POINTS) VALUES( ?, ? )";
        String updateString = "UPDATE PLAYERS SET POINTS = ? WHERE NAME = ? ";
        try (PreparedStatement selectPlayer = con.prepareStatement(selectString);
             PreparedStatement updatePlayer = con.prepareStatement(updateString);
             PreparedStatement insertPlayer = con.prepareStatement(insertString))
        {
            for (HashMap.Entry<String,Integer> e : scores.entrySet()) {
                selectPlayer.setString(1, e.getKey());
                ResultSet rs = selectPlayer.executeQuery();
                if (rs.next()) {
                    // row exists, update
                    updatePlayer.setInt(1, e.getValue().intValue());
                    updatePlayer.setString(2, e.getKey());
                    updatePlayer.executeUpdate();
                } else {
                    // have to insert row
                    insertPlayer.setString(1, e.getKey());
                    insertPlayer.setInt(2, e.getValue().intValue());
                    insertPlayer.executeUpdate();
                }
            }
        } catch(SQLException e) {
            System.err.println(e.getMessage());
        }
        plugin.getLogger().info("Saved scoreboard to DB");
    }

    public Scoreboard getScoreboard() {
        return sc;
    }

    public void updatePlayer(Player player,int increase) {
        // use name to update score by increase
        Score scr = this.obj.getScore(player.getName());
        int val = scr.getScore() + increase;
        scr.setScore(val);
        scores.put(player.getName(),Integer.valueOf(val));
    }

    // Overloaded ^^^^
    public void updatePlayer(Player player) {
        // use name to update score by one 
        Score scr = this.obj.getScore(player.getName());
        int val = scr.getScore() + 1;
        scr.setScore(val);
        scores.put(player.getName(),Integer.valueOf(val));
    }

    // TODO: clean this, for the love of god
    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[] {"TABLE"});
        return resultSet.next();
    }
}

