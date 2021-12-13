package com.rezmicr.spigot.randomfighter;

// TODO: only include used packages
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
import org.bukkit.scheduler.BukkitRunnable;

public class RandFightMinigame {

    private GameRoom room;
    private boolean running = false;
    private List<Player> players = new ArrayList<Player>();
    private List<Entity> enemies = new ArrayList<Entity>();
    private Map<Player,ItemStack[]> inventories = new HashMap<Player,ItemStack[]>();
    private Map<Player,Location> ogLocations = new HashMap<Player,Location>();
    private RandomFighter plugin;
    // TODO: implement this values as plugin config file instead of hardcodding
    private final int SpawnT = 5;
    private final int WaveT = 20;
    private int waves;

    public RandFightMinigame(GameRoom room, Player player, RandomFighter plugin) {
        this.room = room;
        this.addPlayer(player);
        this.plugin = plugin;
    }

    // run logic of the game
    public void startGame(Player host, int waves, RandomTypes randType) {
    this.waves = waves;
    running = true;
    List<Entity> enemies = new ArrayList<Entity>();
    // An easy start
    messageToAll("The game was started!");
    enemies = spawnEntities(EntityType.CHICKEN,players.size(),this,enemies,SpawnT);
    deleteEntities(enemies,WaveT+SpawnT);
    // A random middle
    for (int wave = 2; wave < waves; wave++) {
        int midSpawnT = (WaveT+SpawnT)*wave-WaveT;
        int midWaveT = (WaveT+SpawnT)*wave;
        if (wave < 10) {
            enemies = spawnEntities(randType.randEntity(),players.size()*2,this,enemies,midSpawnT);
        } else if (wave < 15) {
            enemies = spawnEntities(randType.randEntity(),players.size()*3,this,enemies,midSpawnT);
        } else if (wave < 20) {
            enemies = spawnEntities(randType.randEntity(),players.size()*4,this,enemies,midSpawnT);
        } else {
            enemies = spawnEntities(randType.randEntity(),players.size()*5,this,enemies,midSpawnT);
        }
        deleteEntities(enemies,midWaveT);
    }
    // A hard ending
    enemies = spawnEntities(EntityType.RAVAGER,players.size()-1,this,enemies,(WaveT+SpawnT)*waves-WaveT);
    deleteEntities(enemies,(WaveT+SpawnT)*(waves+3));

    // remove players at the end
    removePlayers((WaveT+SpawnT)*(waves+3)+SpawnT*2);
    }

    public int getDeleteTime() {
        return (WaveT+SpawnT)*(waves+2)+SpawnT*2;
    }

    public boolean canStart(Player host) {
        if (!running && players.contains(host)) return true;
        return false;
    }

    public void addPlayer(Player player) {
        if (running) {
            player.sendMessage("Game already running, can't join");
            return;
        }
        if (!players.contains(player)) {
            // general info for keeping control
            players.add(player);
            player.addScoreboardTag("random_fighter");
            // change to adventure mode
            player.setGameMode(GameMode.ADVENTURE);
            // save location and then realocate
            ogLocations.put(player,player.getLocation());
            // swap inventory for a clean one if it's in the same world 
            if (player.getLocation().getWorld() == this.room.getPlayerSpawn().getWorld()) {
                ItemStack[] oldInv = copyPlayerInv(player.getInventory().getContents());
                inventories.put(player,oldInv);
                player.getInventory().clear();
            }
            player.teleport(room.getPlayerSpawn());
            player.sendMessage("You joined to the room");
        } else {
            player.sendMessage("You already joined this game!");
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            player.removeScoreboardTag("random_fighter");
            player.setGameMode(Bukkit.getDefaultGameMode());
            player.getInventory().clear();
            if (this.inventories.containsKey(player))
                player.getInventory().setContents(this.inventories.remove(player));
            player.teleport(this.ogLocations.remove(player));
        }
    }

    private void removePlayers(int sec) {
        BukkitTask task = new RemovePlayers(this).runTaskLater(this.plugin,20*sec);
    }

    public void clearStuff() {
        Iterator<Player> itPlayers = this.players.iterator();
        while (itPlayers.hasNext()) {
            Player player = (Player) itPlayers.next();
            player.removeScoreboardTag("random_fighter");
            player.setGameMode(Bukkit.getDefaultGameMode());
            player.getInventory().clear();
            if (this.inventories.containsKey(player))
                player.getInventory().setContents(this.inventories.remove(player));
            player.teleport(this.ogLocations.remove(player));
        }
        players = null;
        this.room.resetBlocks();
    }
    
    private void deleteEntities(List<Entity> entities, int sec) {
        BukkitTask task = new KillCreatures(entities).runTaskLater(this.plugin,20*sec);
    }

    public void messageToAll(String message) {
        for (Player receiver : players) {
            receiver.sendMessage(message);
        }
    }

    private List<Entity> spawnEntities(EntityType type,int amount,RandFightMinigame game,List<Entity> entities,int sec) {
        BukkitTask task = new SpawnEntities(type,amount,game,entities).runTaskLater(this.plugin,20*sec);
        return entities;
    }

    public GameRoom getRoom() {
        return room;
    }

    public List<Player> getPlayers() {
        return players;
    }

    private ItemStack[] copyPlayerInv(ItemStack[] ogInv) {
        ItemStack[] newInv = Arrays.copyOf(ogInv,ogInv.length);
        return newInv;
    }
}

class RemovePlayers extends BukkitRunnable {

    RandFightMinigame minigame;

    public RemovePlayers(RandFightMinigame minigame) {
        this.minigame = minigame;
    }

    @Override
    public void run() {
        this.minigame.clearStuff();
    }
}

