package com.rezmicr.spigot.randomfighter;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RandFightEvents implements Listener {

    private final RandomFighter plugin;
    private final Map<Player,Location> deathLocs = new HashMap<Player,Location>();
    private final List<Material> itemPool = new ArrayList<Material>();
    private final Random gen = new Random();
    private final RandomTypes randTypes;
    private final Material[] chickenStuff = new Material[]{Material.STONE_PICKAXE,Material.SHIELD,Material.STONE_SWORD};

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
       event.getPlayer().setScoreboard(this.plugin.getScoreBoard().getScoreboard());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand(); 
        switch (itemInHand.getType()) {
            case FISHING_ROD:
                player.getWorld().strikeLightning(player.getTargetBlock((Set<Material>) null, 200).getLocation());
                break;
            case SPYGLASS:
                player.teleport(player.getTargetBlock(null, 20).getLocation());
                break;
        }
    }

    public RandFightEvents(RandomFighter plugin, RandomTypes randTypes) {
        this.plugin = plugin;
        this.randTypes = randTypes;
    }

    @EventHandler
    public void onSlimeSplit(SlimeSplitEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getScoreboardTags().contains("random_enemy")) 
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!entity.getScoreboardTags().contains("random_enemy")) return;
        event.setDroppedExp(0);        
        // Adds to scoreboard
        Player killer = entity.getKiller();
        if (killer != null) {
        switch (entity.getType()) {
            case RAVAGER:
                this.plugin.getScoreBoard().updatePlayer(killer,10);
                killer.sendMessage("10 points kill");
                break;
            case VINDICATOR:
                this.plugin.getScoreBoard().updatePlayer(killer,2);
                killer.sendMessage("2 points kill");
                break;
            default:
                this.plugin.getScoreBoard().updatePlayer(killer);
                killer.sendMessage("1 point kill");
        }
        } else {
            List<Entity> near = entity.getNearbyEntities(10,10,10);
            for (Entity e : near) {
                if (e.getScoreboardTags().contains("random_fighter")) {
                    e.sendMessage("1 point shared kill");
                    this.plugin.getScoreBoard().updatePlayer((Player) e);
                }
            }
        }
        EntityType type = entity.getType();
        List<ItemStack> items = event.getDrops();
        items.clear();
        // define special items to always drop for some specific creatures
        if (type == EntityType.CHICKEN) {
                items.add(new ItemStack(chickenStuff[gen.nextInt(2)],1));
                items.add(new ItemStack(randTypes.randItem(),1));
        }
        // add a random item from a pre defined pool
        items.add(new ItemStack(randTypes.randItem(),1));
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
        Player player = (Player) event.getDamager();
        PotionEffect effect;
        // Verify if the player is currently in a game
        if (player.getScoreboardTags().contains("random_fighter")) {
            Location loc = (Location) player.getLocation();
            loc.setY(loc.getY() - 1);
            Block bloke = (Block) loc.getBlock();
            // Different effects based on the weapon used
            Material itemInHand = player.getInventory().getItemInMainHand().getType();
            switch (itemInHand) {
                case STONE_PICKAXE:
                    if (bloke.getType() != Material.BARRIER || bloke.getType() != Material.BEDROCK)
                        bloke.setType(Material.STONE);
                    break;
                case BUCKET:
                    if (bloke.getType() != Material.BARRIER || bloke.getType() != Material.BEDROCK)
                        bloke.setType(Material.WATER);
                    break;
                case FEATHER:
                    effect = new PotionEffect(PotionEffectType.LEVITATION,10*20,1);
                    effect.apply((LivingEntity) event.getEntity());
                    break;
                case POISONOUS_POTATO:
                    LivingEntity damagee = ((LivingEntity) event.getEntity());
                    effect = new PotionEffect(PotionEffectType.POISON,10*20,1);
                    damagee.setHealth(2);
                    effect.apply(damagee);
                    player.getInventory().getItemInMainHand().setType(Material.BAKED_POTATO);
                    break;
                case CLOCK:
                    effect = new PotionEffect(PotionEffectType.SLOW,10*20,1);
                    effect.apply((LivingEntity) event.getEntity());
                    break;
                case SEA_LANTERN:
                    effect = new PotionEffect(PotionEffectType.GLOWING,10*20,1);
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
    
    private Player player;
    private Location location;

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

