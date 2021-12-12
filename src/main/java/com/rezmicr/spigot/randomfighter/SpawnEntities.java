package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.EntityEffect;
import org.bukkit.loot.LootTables;

public class SpawnEntities extends BukkitRunnable {

    private EntityType type;
    private int amount;
    private List<Entity> entities = new ArrayList<Entity>();
    private GameRoom room;

    public SpawnEntities(EntityType type, int amount,
                         GameRoom room, List<Entity> entities) {
        this.type = type;
        this.amount = amount;
        this.room = room;
        this.entities = entities;
    }

    @Override
    public void run() {
        for (int i = 0; i < this.amount; i++) {
            // spawn created entity into the coords
            Mob newSpawn = ((Mob) this.room.getWorld().spawnEntity(this.room.getEnemySpawn(),
                                                                   this.type));
            newSpawn.addScoreboardTag("random_enemy");
            // add special spawn conditions
            switch(this.type) {
                case CHICKEN:
                    newSpawn.playEffect(EntityEffect.WOLF_HEARTS);
                    break;
                case ZOMBIE_HORSE:
                    newSpawn.playEffect(EntityEffect.WOLF_SMOKE);
                    Mob jockey = ((Mob) this.room.getWorld().spawnEntity(this.room.getEnemySpawn(),
                                                                         EntityType.WITHER_SKELETON));
                    jockey.playEffect(EntityEffect.WOLF_SMOKE);
                    jockey.addScoreboardTag("random_enemy");
                    newSpawn.addPassenger(jockey);
                    this.entities.add(jockey);
                    break;
                default:
                    newSpawn.playEffect(EntityEffect.WOLF_SMOKE);
            }
            this.entities.add(newSpawn);
        }
    }
}

