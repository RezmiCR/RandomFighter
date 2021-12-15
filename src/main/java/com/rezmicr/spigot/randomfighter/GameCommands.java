package com.rezmicr.spigot.randomfighter;

import java.util.*;
import java.lang.Double;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameCommands {

    private final Map<String, RandFightMinigame> games;
    // TODO: auto manager class for the connections pool
    // smartly repopulate the connections once they are running out
    private List<Connection> connections; 
    private Map<String, GameRoom> rooms;
    private RandomFighter plugin;
    private RandomTypes randTypes;

    public GameCommands(Map<String, RandFightMinigame> games,
                        List<Connection> connections,
                        Map<String,GameRoom> rooms, RandomFighter plugin, RandomTypes randTypes) {
        this.rooms = rooms;
        this.games = games;
        this.connections = connections;
        this.plugin = plugin;
        this.randTypes = randTypes;
    }

    public boolean runCommand(CommandSender sender, Command cmd,
                              String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("joinGame")) {
    if (args.length > 0) {
        // check if the room exists at all
        if (rooms.containsKey(args[0])) {
            // check if the player is already tagged as random_fighter
            if (!((Player) sender).getScoreboardTags().contains("random_fighter")) {
                // check if the game is already started
                if (games.get(args[0]) == null) {
                    World world = ((Player) sender).getWorld();
                    games.put(args[0],new RandFightMinigame(rooms.get(args[0]),
                                (Player) sender, this.plugin));
                } else {
                    RandFightMinigame game = games.get(args[0]);
                    game.addPlayer((Player) sender);
                }
            } else {
                sender.sendMessage("You can't join multiple games at the same time");
            }
        } else {
            sender.sendMessage("The room "+args[0]+" doesn't exist");
        }
        return true;
    } return false;
    } else if (cmd.getName().equalsIgnoreCase("startGame")) {
        if (args.length < 1 || args.length > 2)
            return false; // incorrect amount of args

        if (games.get(args[0]) == null) {
            sender.sendMessage("There is no game in that room, use /joingame [roomname]");
            return true;
        }

        RandFightMinigame game = games.get(args[0]);
        if (!game.canStart((Player) sender)) {
            sender.sendMessage("You can't start that game");
            return true;
        }
        switch (args.length) {
            case 1:
                game.startGame((Player) sender,5,randTypes);
                break;
            case 2:
                game.startGame((Player) sender,Integer.parseInt(args[1]),randTypes);
        }
        deleteGame(args[0],game.getDeleteTime());
        return true;
    } else if (cmd.getName().equalsIgnoreCase("leaveGame")) {
        if (args.length == 1) {
            if (games.get(args[0]) != null) {
                if (games.get(args[0]).getPlayers().contains((Player) sender)) {
                    games.get(args[0]).removePlayer((Player) sender);
                    sender.sendMessage("You left the room");
                } else {
                    sender.sendMessage("You aren't in that game room!");
                }
            } else {
                sender.sendMessage("There are "+games.size()+" games");
            }
            return true;
        }
        return false;
    } else if (cmd.getName().equalsIgnoreCase("createRoom")) {
        // \createroom roomName corner1 corner2 playerSpawn enemySpawn
        if (args.length == 13) {
            String insertString = "INSERT INTO ROOMS VALUES(?,"+ // roomName
                                "?,?,?,"+                      // corner1
                                "?,?,?,"+                      // corner2
                                "?,?,?,"+                      // playerSpawn
                                "?,?,?)";                      // enemySpawn
            Connection con = connections.get(connections.size()-1);
            try (PreparedStatement insertRoom = con.prepareStatement(insertString)) {
                // Create GameRoom instance
                World world = ((Player) sender).getWorld();
                Location loc1 = new Location(world, 
                                             Double.parseDouble(args[1]), 
                                             Double.parseDouble(args[2]), 
                                             Double.parseDouble(args[3]));
                Location loc2 = new Location(world, 
                                             Double.parseDouble(args[4]), 
                                             Double.parseDouble(args[5]), 
                                             Double.parseDouble(args[6]));
                Location loc3 = new Location(world, 
                                             Double.parseDouble(args[7]), 
                                             Double.parseDouble(args[8]), 
                                             Double.parseDouble(args[9]));
                Location loc4 = new Location(world, 
                                             Double.parseDouble(args[10]), 
                                             Double.parseDouble(args[11]), 
                                             Double.parseDouble(args[12]));
                GameRoom tempRoom = new GameRoom(args[0],loc1,loc2,
                                                         loc3,loc4);
                this.rooms.put(args[0],tempRoom);
                // Store room in DB
                insertRoom.setString(1,args[0]);
                insertRoom.setDouble(2,Double.parseDouble(args[1]));
                insertRoom.setDouble(3,Double.parseDouble(args[2]));
                insertRoom.setDouble(4,Double.parseDouble(args[3]));
                insertRoom.setDouble(5,Double.parseDouble(args[4]));
                insertRoom.setDouble(6,Double.parseDouble(args[5]));
                insertRoom.setDouble(7,Double.parseDouble(args[6]));
                insertRoom.setDouble(8,Double.parseDouble(args[7]));
                insertRoom.setDouble(9,Double.parseDouble(args[8]));
                insertRoom.setDouble(10,Double.parseDouble(args[9]));
                insertRoom.setDouble(11,Double.parseDouble(args[10]));
                insertRoom.setDouble(12,Double.parseDouble(args[11]));
                insertRoom.setDouble(13,Double.parseDouble(args[12]));
                insertRoom.setString(14,world.getName());
                insertRoom.executeUpdate();
                con.close();
                connections.remove(con);
                sender.sendMessage("Succesfully created the room " + args[0]);
            } catch(SQLException e) {
                System.err.println(e.getMessage());
                sender.sendMessage("Something went wrong, contact an admin");
            }
            return true;
        }
        return false;
    /*
    } else if (cmd.getName().equalsIgnoreCase("deleteRoom")) {
        // TODO: implement deleteroom command
        if (args.length == 1) {
            // code to delete the room from the DB
            sender.sendMessage("Welp, I still don't have this done");
            return true;
        }
        return false;
    */
    } else if (cmd.getName().equalsIgnoreCase("listRooms")) {
        if (args.length == 0) {
        if (rooms.size() > 0) {
            sender.sendMessage("Available rooms:");
            rooms.keySet().forEach((e) -> { sender.sendMessage("-> "+e); });
        } else {
            sender.sendMessage("No available rooms were found");
        }
        return true;
        }
    } else if (cmd.getName().equalsIgnoreCase("updateRoom")) {
        // \ updateroom [roomname]
        if (!(args.length == 1)) return false;
        // get room from list
        GameRoom room = this.rooms.get(args[0]);
        if (room != null) {
            room.updateRoom();
            sender.sendMessage("Updated the room " + args[0]);
        } else {
            sender.sendMessage("There is no room " + args[0]);
        }
        return true;
    }
    return false; 
    }
//}
    public void deleteGame(String roomName,int sec) {
        BukkitTask task = new DeleteGame(this.games,roomName).runTaskLater(this.plugin,20*sec);
    }
}

class DeleteGame extends BukkitRunnable {

    private Map<String, RandFightMinigame> games;
    private String roomName;

    public DeleteGame(Map<String, RandFightMinigame> games,String roomName) {
        this.games = games;
        this.roomName = roomName;
    }

    @Override
    public void run() {
        this.games.remove(this.roomName);
    }
}

