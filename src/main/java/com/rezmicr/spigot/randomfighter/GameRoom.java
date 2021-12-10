package com.rezmicr.spigot.randomfighter;

import org.bukkit.Location;
import org.bukkit.World;

public class GameRoom {
    // Fields
    private final String roomName;
    private final Location enemySpawn;
    private final Location playerSpawn;
    private final Location corner1;
    private final Location corner2;
    // Constructor
    public GameRoom(String roomName, Location corner1,
                    Location corner2, Location playerSpawn,
                    Location enemySpawn
                    ) {
        this.roomName = roomName;
        this.enemySpawn = enemySpawn;
        this.playerSpawn = playerSpawn;
        this.corner1 = corner1;
        this.corner2 = corner2;
    }
    public Location getPlayerSpawn() {
        return this.playerSpawn;
    }
    public Location getEnemySpawn() {
        return this.enemySpawn;
    }
    public World getWorld() {
        return this.enemySpawn.getWorld();
    }
}
