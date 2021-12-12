package com.rezmicr.spigot.randomfighter;

import java.util.HashMap;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        // TODO: maybe I could check if the server already has the last sc?
        obj = sc.registerNewObjective("rf_scoreboard","dummy","Random Fighter"); 
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        // build scores for each player
        loadFromDB(con,plugin);
    }
    
    private void loadFromDB(Connection con, RandomFighter plugin) {
        try {
        Statement statement = con.createStatement();
        statement.setQueryTimeout(30);
        if (!tableExists(con, "PLAYERS")) {
            // should use prepared statements
            statement.executeUpdate("CREATE TABLE PLAYERS (NAME,POINTS)");
        } else {
            ResultSet rs = statement.executeQuery("SELECT * FROM PLAYERS "+
                                                  "ORDER BY POINTS DESC");
            while(rs.next()) {
                // read the result set and add them to the scores
                String player_name = rs.getString("NAME");
                Score temp_scr = obj.getScore(player_name);
                Integer player_score = Integer.valueOf(rs.getInt("POINTS"));
                temp_scr.setScore(player_score); // FIXME: is there a max I should add?
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
        String selectString = "SELECT (POINTS) FROM PLAYERS WHERE NAME = ? ";
        String insertString = "INSERT INTO PLAYERS (NAME, POINTS) VALUES( ?, ? )";
        String updateString = "UPDATE PLAYERS SET POINTS = ? WHERE NAME = ? ";
        try (PreparedStatement selectPlayer = con.prepareStatement(selectString);
             PreparedStatement updatePlayer = con.prepareStatement(updateString);
             PreparedStatement insertPlayer = con.prepareStatement(insertString))
        {
            //Statement statement = con.createStatement();
            //statement.setQueryTimeout(30);
            for (HashMap.Entry<String,Integer> e : scores.entrySet()) {
                selectPlayer.setInt(1, e.getValue().intValue());
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
        plugin.getLogger().info("Saved points");
    }

    public Scoreboard getScoreboard() {
        return sc;
    }

    public void updatePlayer(Player player,int increase) {
        // use name to update score by increase
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

