package com.rezmicr.spigot.randomfighter;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Integer;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class RandomTypes {

    private class EntityChance {

        private final HashMap<Integer,EntityType> entities;
        private final ArrayList<EntityType> entityChance = new ArrayList<EntityType>();
        private int totalWeigth;
        private final Random random; 
        private boolean built;

        public EntityChance(Random random) {
            entities = new HashMap<Integer,EntityType>();
            this.random = random;
        }

        public void add(EntityType type,int weigth) {
            entities.put(weigth,type);
            totalWeigth += weigth;
        }

        public EntityType getRandomEntity() {
            if (entities.size() == 0)
                return null;
            if (!built)
                build();
            //System.err.println(totalWeigth);
            //System.err.println(entityChance.size());
            int x = random.nextInt(entityChance.size());
            return entityChance.get(x);
        }

        public void build() {
            for (Integer i : entities.keySet()) {
                for (int x = 0; x <= i; x++) {
                    entityChance.add(entities.get(i));
                }
            }
            built = true;
        }
    }

    private class ItemChance {

        private final HashMap<Integer,Material> items;
        private final ArrayList<Material> itemChance = new ArrayList<Material>();
        private int totalWeigth;
        private final Random random; 
        private boolean built;

        public ItemChance(Random random) {
            items = new HashMap<Integer,Material>();
            this.random = random;
        }

        public void add(Material material,int weigth) {
            items.put(weigth,material);
            totalWeigth += weigth;
        }

        public Material getRandomItem() {
            if (items.size() == 0)
                return null;
            if (!built)
                build();
            //System.err.println(totalWeigth);
            //System.err.println(itemChance.size());
            int x = random.nextInt(itemChance.size());
            return itemChance.get(x);
        }

        public void build() {
            for (Integer i : items.keySet()) {
                for (int x = 0; x <= i; x++) {
                    itemChance.add(items.get(i));
                }
            }
            built = true;
        }
    }

    private final Random random;
    private final ItemChance items;
    private final EntityChance entities;
    
    public RandomTypes() {
        // weigth from 1 to 10
        random = new Random();
        items = new ItemChance(random);
        entities = new EntityChance(random);

        // hardcoded pools TODO: (for now)
        entities.add(EntityType.CREEPER,5);
        entities.add(EntityType.HUSK,5);
        entities.add(EntityType.SLIME,2);
        entities.add(EntityType.PIGLIN_BRUTE,4);
        //entities.add(EntityType.CAVE_SPIDER,3);
        entities.add(EntityType.VINDICATOR,1);
        entities.add(EntityType.ZOMBIE_HORSE,3);
        entities.build();

        items.add(Material.LEATHER_BOOTS,3);
        items.add(Material.LEATHER_HELMET,3);
        items.add(Material.LEATHER_CHESTPLATE,3);
        items.add(Material.LEATHER_LEGGINGS,3);
        items.add(Material.IRON_BOOTS,2);
        items.add(Material.IRON_HELMET,2);
        items.add(Material.IRON_CHESTPLATE,2);
        items.add(Material.IRON_LEGGINGS,2);
        items.add(Material.DIAMOND_BOOTS,1);
        items.add(Material.DIAMOND_HELMET,1);
        items.add(Material.DIAMOND_CHESTPLATE,1);
        items.add(Material.DIAMOND_LEGGINGS,1);
        items.add(Material.FISHING_ROD,4);
        items.add(Material.STONE_PICKAXE,6);
        items.add(Material.SPYGLASS,4);
        items.add(Material.BUCKET,4);
        items.add(Material.FEATHER,7);
        items.add(Material.IRON_AXE,1);
        items.add(Material.GOLDEN_CARROT,6);
        items.add(Material.CARROT,6);
        items.add(Material.POISONOUS_POTATO,4);
        items.add(Material.CLOCK,3);
        items.add(Material.SEA_LANTERN,3);
        items.build();
    }

    public EntityType randEntity() {
        return entities.getRandomEntity();
    }

    public Material randItem() {
        return items.getRandomItem();
    }
}

