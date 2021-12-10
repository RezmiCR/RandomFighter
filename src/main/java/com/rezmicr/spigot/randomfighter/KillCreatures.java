package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Entity;

public class KillCreatures extends BukkitRunnable {

    //private final JavaPlugin plugin;
    private List<Entity> entities;

    //public KillCreatures(JavaPlugin plugin) {
    public KillCreatures(List<Entity> entities) {
        //this.plugin = plugin;
        this.entities = entities;
    }

    @Override
    public void run() {
        // What you want to schedule goes here
        //plugin.getServer().broadcastMessage("a");
        Iterator<Entity> itThem = this.entities.iterator();
        while(itThem.hasNext()) {
            itThem.next().remove();
        }
        this.entities.clear();
    }

}

