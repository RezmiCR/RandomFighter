package com.rezmicr.spigot.randomfighter;

import java.util.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.Material;

public class GameRoom {
    // Fields
    private final String roomName;
    private final Location enemySpawn;
    private final Location playerSpawn;
    private final Location corner1;
    private final Location corner2;
    private LasagnaGeometry ogBlocks;
    
    public GameRoom(String roomName, Location corner1,
                    Location corner2, Location playerSpawn,
                    Location enemySpawn
                    ) {
        this.roomName = roomName;
        this.enemySpawn = enemySpawn;
        this.playerSpawn = playerSpawn;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.ogBlocks = new LasagnaGeometry(this.corner1,this.corner2);
    }
    public void resetBlocks() {
        this.ogBlocks.resetRoom();
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

class BlocksNoodle {
    private List<Material> blocks = new ArrayList<Material>();
    public void BlocksNoodle() {}
    public void add(Block block) {
        this.blocks.add(block.getType());
    }
    public Material getMaterial(int i) {
        return this.blocks.get(i);
    }
}

class BlocksSheet {
    private List<BlocksNoodle> noodles = new ArrayList<BlocksNoodle>();
    public void BlocksSheet() {}
    public void add(BlocksNoodle noodle) {
        this.noodles.add(noodle);
    }
    public BlocksNoodle getNoodle(int i) {
        return this.noodles.get(i);
    }
}

class LasagnaGeometry {
    private Location startX, endX, startY, endY, startZ, endZ; 
    private final List<BlocksSheet> sheets = new ArrayList<BlocksSheet>();;
    public LasagnaGeometry(Location corner1, Location corner2) {
        // code to run everything
        this.startY = (corner1.getBlockY() < corner2.getBlockY()) ? corner1 : corner2; 
        this.endY = (corner1.getBlockY() < corner2.getBlockY()) ? corner2 : corner1; 
        this.startX = (corner1.getBlockX() < corner2.getBlockX()) ? corner1 : corner2; 
        this.endX = (corner1.getBlockX() < corner2.getBlockX()) ? corner2 : corner1; 
        this.startZ = (corner1.getBlockZ() < corner2.getBlockZ()) ? corner1 : corner2; 
        this.endZ = (corner1.getBlockZ() < corner2.getBlockZ()) ? corner2 : corner1; 
        for (double j = startY.getBlockY(); j < endY.getBlockY(); j++) {
            // add each sheet
            BlocksSheet sheet = new BlocksSheet();
            for (double i = this.startX.getBlockX(); i < this.endX.getBlockX(); i++) {
                // add each noodle
                BlocksNoodle noodle = new BlocksNoodle();
                for (double k = this.startZ.getBlockZ(); k < this.endZ.getBlockZ(); k++) {
                    // add each material
                    Location blockLoc = new Location(this.startX.getWorld(),i,j,k);
                    noodle.add(blockLoc.getBlock()); 
                } sheet.add(noodle);
            } this.sheets.add(sheet);
        }
    }
    public void resetRoom() {
        int distY = this.endY.getBlockY() - this.startY.getBlockY();
        int distX = this.endX.getBlockX() - this.startX.getBlockX();
        int distZ = this.endZ.getBlockZ() - this.startZ.getBlockZ();
        for (int j = 0; j < distY; j++) {
            BlocksSheet sheet = this.sheets.get(j);
            for (int i = 0; i < distX; i++) {
                BlocksNoodle noodle = sheet.getNoodle(i);
                for (int k = 0; k < distZ; k++) {
                    Material mat = noodle.getMaterial(k);
                    Location block = new Location(this.startX.getWorld(),
                                                  this.startX.getBlockX()+i,
                                                  this.startY.getBlockY()+j,
                                                  this.startZ.getBlockZ()+k);
                    block.getBlock().setType(mat);
                }
            }
        }
    }
}

