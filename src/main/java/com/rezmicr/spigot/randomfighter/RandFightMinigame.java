package com.rezmicr.spigot.randomfighter;

import java.util.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;
import org.bukkit.Bukkit;

public class RandFightMinigame {
    // Fields
    private GameRoom room;
    private boolean running = false;
    private List<Player> players = new ArrayList<Player>();
    private List<Entity> enemies = new ArrayList<Entity>();
    // FIXME: these could be merged into one, but that's not my problem rn
    private Map<Player,ItemStack[]> inventories = new HashMap<Player,ItemStack[]>();
    private Map<Player,Location> ogLocations = new HashMap<Player,Location>();
    private Connection con;
    private RandomFighter plugin;

    public RandFightMinigame(GameRoom room, Player player, Connection con, RandomFighter plugin) {
        this.room = room;
        this.addPlayer(player);
        this.con = con;
        this.plugin = plugin;
    }

    private boolean saveData() {
        // DB store data
        return false;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            // general info for keeping control
            players.add(player);
            player.addScoreboardTag("random_fighter");
            // change to adventure mode
            player.setGameMode(GameMode.ADVENTURE);
            // swap inventory for a clean one
            ItemStack[] oldInv = copyPlayerInv(player.getInventory().getContents());
            inventories.put(player,oldInv);
            player.getInventory().clear();
            // save location and then realocate
            ogLocations.put(player,player.getLocation());
            player.teleport(room.getPlayerSpawn());
        } else {
            player.sendMessage("You already joined this game!");
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            player.removeScoreboardTag("random_fighter");
            player.setGameMode(Bukkit.getDefaultGameMode());    // TODO: test this
            player.getInventory().clear();
            player.getInventory().setContents(this.inventories.remove(player));
            player.teleport(ogLocations.remove(player));
        }
    }

    public void removePlayers(int sec) {
        BukkitTask task = new RemovePlayers(players,ogLocations,inventories).runTaskLater(this.plugin,20*sec);
    }
    
    private void deleteEntities(List<Entity> entities, int sec) {
        BukkitTask task = new KillCreatures(entities).runTaskLater(this.plugin,20*sec);
    }

    private List<Entity> spawnEntities(EntityType type,int amount,GameRoom room,List<Entity> entities,int sec) {
        BukkitTask task = new SpawnEntities(type,amount,room,entities).runTaskLater(this.plugin,20*sec);
        return entities;
    }

    public void startGame() {
    if (!running) {
        this.running = true;
        List<Entity> enemies = new ArrayList<Entity>();
        // run logic of the game

        // FIXME: this is an awful solution/hack
        // but I just wanted to get this running
        enemies = spawnEntities(EntityType.CHICKEN,2,this.room,enemies,10);
        deleteEntities(enemies,40);
        enemies = spawnEntities(EntityType.PIG,4,this.room,enemies,50);
        deleteEntities(enemies,80);
        // remove players at the end
        removePlayers(90);    // or should I make them leave?
            
    }
    }
    public boolean isRunning() {
        return running;
    }
    public GameRoom getRoom() {
        return room;
    }
    public List<Player> getPlayers() {
        return players;
    }
    // TODO: implement this
    private ItemStack[] copyPlayerInv(ItemStack[] ogInv) {
        // foreach in ogInv
        ItemStack[] newInv = Arrays.copyOf(ogInv,ogInv.length);
        //new ItemStack(player.getInventory().getContents()); // please stay there
        return newInv;
    }
}

