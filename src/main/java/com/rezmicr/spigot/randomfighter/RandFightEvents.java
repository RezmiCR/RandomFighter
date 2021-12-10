package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RandFightEvents implements Listener {

    RandomFighter plugin;
    Map<Player,Location> deathLocs = new HashMap<Player,Location>();

   //@EventHandler(priority = EventPriority.HIGH)
   /*
   @EventHandler
   public void onJustWorkPLS(PlayerInteractEvent event) {
       Player player = event.getPlayer();
       if (player.getInventory().getItemInMainHand().getType() == Material.FISHING_ROD) {
           // Creates a bolt of lightning at a given location. In this case, that location is where the player is looking.
           // Can only create lightning up to 200 blocks away.
           player.getWorld().strikeLightning(player.getTargetBlock((Set<Material>) null, 200).getLocation());
       }
   } */

    public RandFightEvents(RandomFighter plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
        Player player = (Player) event.getDamager();
        // Verify if the player is currently in a game
        if (player.getScoreboardTags().contains("random_fighter")) {
            Location locat = (Location) player.getLocation();
            locat.setY(locat.getY() - 1);
            Block bloke = (Block) locat.getBlock();
            // Different effects based on the weapon used
            if (player.getInventory().getItemInMainHand().getType() == Material.STONE_PICKAXE) {
                bloke.setType(Material.STONE);
            } else if (player.getInventory().getItemInMainHand().getType() == Material.BUCKET) {
                bloke.setType(Material.WATER);
            } else if (player.getInventory().getItemInMainHand().getType() == Material.FEATHER) {
                //PotionEffect(PotionEffectType.LEVITATION,10,1,false).apply(event.getEntity());
                PotionEffect effect = new PotionEffect(PotionEffectType.LEVITATION,10*20,1);
                effect.apply((LivingEntity) event.getEntity());
            }
        }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    Player player = event.getEntity();
    if (player.getScoreboardTags().contains("random_fighter")) {
        Location location = player.getLocation();
        player.sendMessage("well, the coords are x: "+location.getBlockX()+" y: "+location.getBlockY()+" z: "+location.getBlockZ());
        this.deathLocs.put(player,location);
    }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    if (player.getScoreboardTags().contains("random_fighter")) {
        Location loc = this.deathLocs.remove(player);
        BukkitTask task = new RespawnPlayer(player,loc).runTaskLater(this.plugin,20);
    }
    }
}

class RespawnPlayer extends BukkitRunnable {
    
    Player player;
    Location location;

    public RespawnPlayer(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    @Override
    public void run() {
        if (!player.isDead()) {
        this.player.teleport(this.location);
        }
    }
}

