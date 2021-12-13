package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;
import org.bukkit.EntityEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnEntities extends BukkitRunnable {

    private final EntityType type;
    private int amount;
    private final List<Entity> entities;
    private final RandFightMinigame game;

    public SpawnEntities(EntityType type, int amount,
                         RandFightMinigame game, List<Entity> entities) {
        this.type = type;
        this.amount = amount;
        this.game = game;
        this.entities = entities;
    }

    @Override
    public void run() {
        game.getRoom().getWorld().playSound(game.getRoom().getEnemySpawn(),
                                            Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                                            1.0f,
                                            1.0f);
        game.messageToAll("Wave starting");
        if (this.type == EntityType.RAVAGER)
            game.messageToAll("These seem a bit out of place...");

        cleanSpawn();
        this.amount = (this.amount < 1) ? 1 : this.amount;
        for (int i = 0; i < this.amount; i++) {
            // spawn created entity into the coords
            World world = game.getRoom().getWorld();
            Location spawn = game.getRoom().getEnemySpawn();    
            Mob newSpawn = ((Mob) world.spawnEntity(spawn,this.type));
            Mob jockey;
            newSpawn.addScoreboardTag("random_enemy");
            // add special spawn conditions
            switch(this.type) {
                case CHICKEN:
                    newSpawn.playEffect(EntityEffect.WOLF_HEARTS);
                    break;
                case ZOMBIE_HORSE:
                    jockey = ((Mob) world.spawnEntity(spawn,EntityType.WITHER_SKELETON));
                    jockey.playEffect(EntityEffect.WOLF_SMOKE);
                    jockey.addScoreboardTag("random_enemy");
                    jockey.setCanPickupItems(false);
                    newSpawn.addPassenger(jockey);
                    this.entities.add(jockey);
                    break;
                case RAVAGER:
                    jockey = ((Mob) world.spawnEntity(spawn,EntityType.PILLAGER));
                    jockey.addScoreboardTag("random_enemy");
                    jockey.setCanPickupItems(false);
                    newSpawn.addPassenger(jockey);
                    this.entities.add(jockey);
                default:
                    newSpawn.playEffect(EntityEffect.WOLF_SMOKE);
                    newSpawn.setCanPickupItems(false);
            }
            this.entities.add(newSpawn);
        }
    }

    private void cleanSpawn() {
        // clean blocks for the mobs
        final Location spawn = this.game.getRoom().getEnemySpawn();
        final World world = spawn.getWorld();
        final int spawnX = spawn.getBlockX();
        final int spawnY = spawn.getBlockY();
        final int spawnZ = spawn.getBlockZ();
        //spawn.add(new Vector(-1,0,0).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(1,0,0).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(0,0,1).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(0,0,-1).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(0,0,-1).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(1,0,1).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(-1,0,1).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(1,0,-1).getBlock().setType(Material.AIR);
        //spawn.add(new Vector(-1,0,-1).getBlock().setType(Material.AIR);
        for (int y = -1; y == 4; y++) {
            for (int x = -1; x == 1; x++) {
                for (int z = -1; z == 1; z++) {
                    System.err.println("doing mah stuff x: "+(spawnX+x)+" y: "+(spawnY+y)+" z: "+(spawnZ+z));
                    new Location(world,spawnX+x,spawnY+y,spawnZ+z).getBlock().setType(Material.AIR);
                }
            }
        }
    }
}

