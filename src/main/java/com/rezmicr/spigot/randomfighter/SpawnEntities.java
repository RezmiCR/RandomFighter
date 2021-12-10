package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class SpawnEntities extends BukkitRunnable {

    //private final JavaPlugin plugin;
    private EntityType type;
    private int amount;
    private List<Entity> entities = new ArrayList<Entity>();
    private GameRoom room;

    //public KillCreatures(JavaPlugin plugin) {
    public SpawnEntities(EntityType type, int amount,
                         GameRoom room, List<Entity> entities) {
        //this.plugin = plugin;
        this.type = type;
        this.amount = amount;
        this.room = room;
        this.entities = entities;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.amount; i++) {
            this.entities.add(this.room.getWorld().spawnEntity(this.room.getEnemySpawn(),this.type));
        }
    }
}

