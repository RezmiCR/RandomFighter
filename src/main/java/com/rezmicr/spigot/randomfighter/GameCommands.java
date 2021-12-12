package com.rezmicr.spigot.randomfighter;

import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameCommands {

    protected Map<String, RandFightMinigame> games;
    // TODO: auto manager class for the connections pool
    // smartly repopulate the connections once they are running out
    protected List<Connection> connections = new ArrayList<Connection>(); 
    protected Map<String, GameRoom> rooms;
    protected RandomFighter plugin;

    public GameCommands(Map<String, RandFightMinigame> games,
                        List<Connection> connections,
                        Map<String,GameRoom> rooms, RandomFighter plugin) {
        this.rooms = rooms;
        this.games = games;
        this.connections = connections;
        this.plugin = plugin;
    }

    public boolean runCommand(CommandSender sender, Command cmd,
                              String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("joinGame")) {
    if (args.length > 0) {
        // create a game or join the player to an active game
        if (rooms.containsKey(args[0])) {
            if (games.get(args[0]) == null) {
                World world = ((Player) sender).getWorld();
                Connection con = connections.get(connections.size()-1);
                games.put(args[0],new RandFightMinigame(rooms.get(args[0]),
                            (Player) sender, con, this.plugin));
                connections.remove(con);
                sender.sendMessage("You joined to the room");
            } else {
                RandFightMinigame game = games.get(args[0]);
                // check if the game is already running
                if (!game.isRunning()) {
                    game.addPlayer((Player) sender);
                    sender.sendMessage("You joined to the room");
                } else {
                    sender.sendMessage("That game is already "+
                        "running, wait or join another room");
                }
            }
        } else {
            sender.sendMessage("The room "+args[0]+" doesn't exist");
        }
        return true;
    } return false;
    } else if (cmd.getName().equalsIgnoreCase("startGame")) {
        if (args.length == 1) {
            if (games.get(args[0]) != null) {
                RandFightMinigame game = games.get(args[0]);
                if (!game.isRunning() && game.getPlayers().contains((Player) sender)) {
                    sender.sendMessage("You started the game, good luck!");
                    // game loop starts
                    game.startGame();
                    // FIXME: Another consequence of the awful waves hack, ... too bad
                    deleteGame(args[0],85);
                } else {
                    sender.sendMessage("Couldn't start that game, wrong room maybe?");
                }
            } else {
                sender.sendMessage("There is no game in that room, use /joingame [roomname]");
            }
            return true;
        }
        return false;
    } else if (cmd.getName().equalsIgnoreCase("leaveGame")) {
        if (args.length == 1) {
            if (games.get(args[0]) != null) {
                games.get(args[0]).removePlayer((Player) sender);
                sender.sendMessage("You left the room " + args[0]);
            } else {
                sender.sendMessage("There are "+games.size()+" games");
                sender.sendMessage("You aren't in that game room!");
            }
            return true;
        }
        return false;
    } else if (cmd.getName().equalsIgnoreCase("createRoom")) {
        // \createroom roomName corner1 corner2 playerSpawn enemySpawn
        if (args.length == 13) {
            try {
                // code to create GameRoom instance
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
                this.rooms.put(args[0],tempRoom);       // FIXME: A
                // code to store room in DB
                Connection con = connections.get(connections.size()-1);
                Statement statement = con.createStatement();
                statement.setQueryTimeout(30);  // set timeout to 30 sec.
                statement.executeUpdate("INSERT INTO ROOMS VALUES('"+args[0]+
                                            "','"+args[1]+"','"+args[2]+
                                            "','"+args[3]+"','"+args[4]+
                                            "','"+args[5]+"','"+args[6]+
                                            "','"+args[7]+"','"+args[8]+
                                            "','"+args[9]+"','"+args[10]+
                                            "','"+args[11]+"','"+args[12]+
                                            "','"+world.getName()+"')");
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

