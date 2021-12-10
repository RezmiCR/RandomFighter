package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Entity;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RemovePlayers extends BukkitRunnable {

    private Map<Player,Location> ogLoc = new HashMap<Player,Location>();
    private List<Player> players = new ArrayList<Player>();
    private Map<Player,ItemStack[]> inventories = new HashMap<Player,ItemStack[]>();
    
    public RemovePlayers(List<Player> players,
                         Map<Player,Location> ogLoc,
                         Map<Player,ItemStack[]> inventories) {
        this.players = players;
        this.ogLoc = ogLoc;
        this.inventories = inventories;
    }

    @Override
    public void run() {
        Iterator<Player> itPlayers = this.players.iterator();
        while (itPlayers.hasNext()) {
            Player player = (Player) itPlayers.next();
            player.removeScoreboardTag("random_fighter");
            player.setGameMode(Bukkit.getDefaultGameMode());    // TODO: test this
            player.getInventory().clear();
            player.getInventory().setContents(this.inventories.remove(player));
            player.teleport(this.ogLoc.remove(player));
        }
        players = null;
    }
}

