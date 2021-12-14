package com.rezmicr.spigot.randomfighter;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Integer;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class RandomTypes {

    private class EntityChance {

        private final HashMap<EntityType, Integer> entities = new HashMap<>();
        private final ArrayList<EntityType> entityChance = new ArrayList<>();
        private int totalWeigth;
        private final Random random; 
        private boolean built;

        public EntityChance(Random random) {
            this.random = random;
        }

        public void add(EntityType type,int weigth) {
            entities.put(type, weigth);
            totalWeigth += weigth;
        }

        public EntityType getRandomEntity() {
            if (entities.size() == 0) return null;
            if (!built) build();
            return entityChance.get(random.nextInt(totalWeigth));
        }

        public void build() {
            for (EntityType type : entities.keySet()) {
                for (int x = 0; x < entities.get(type); x++) {
                    entityChance.add(type);
                }
            }
            built = true;
        }
    }

    private class ItemChance {

        private final HashMap<Material, Integer> items = new HashMap<>();
        private final ArrayList<Material> itemChance = new ArrayList<>();
        private int totalWeigth;
        private final Random random; 
        private boolean built;

        public ItemChance(Random random) {
            this.random = random;
        }

        public void add(Material material,int weigth) {
            items.put(material, weigth);
            totalWeigth += weigth;
        }

        public Material getRandomItem() {
            if (items.size() == 0) return null;
            if (!built) build();
            return itemChance.get(random.nextInt(totalWeigth));
        }

        public void build() {
            for (Material mat : items.keySet()) {
                for (int x = 0; x < items.get(mat); x++) {
                    itemChance.add(mat);
                }
            }
            built = true;
        }
    }

    private final Random random;
    private final ItemChance items;
    private final EntityChance entities;
    
    public RandomTypes() {
        random = new Random();
        items = new ItemChance(random);
        entities = new EntityChance(random);

        // hardcoded pools TODO: (for now)
        entities.add(EntityType.CREEPER,3);
        entities.add(EntityType.HUSK,5);
        entities.add(EntityType.SLIME,4);
        entities.add(EntityType.WITCH,3);
        entities.add(EntityType.VEX,1);
        entities.add(EntityType.VINDICATOR,1);
        entities.add(EntityType.ZOMBIE_HORSE,3);

        items.add(Material.LEATHER_BOOTS,2);
        items.add(Material.LEATHER_HELMET,2);
        items.add(Material.LEATHER_CHESTPLATE,2);
        items.add(Material.LEATHER_LEGGINGS,2);
        items.add(Material.DIAMOND_BOOTS,1);
        items.add(Material.DIAMOND_HELMET,1);
        items.add(Material.DIAMOND_CHESTPLATE,1);
        items.add(Material.DIAMOND_LEGGINGS,1);
        items.add(Material.FISHING_ROD,2);
        items.add(Material.STONE_PICKAXE,5);
        //items.add(Material.SPYGLASS,4);
        items.add(Material.DIAMOND_SWORD,1);
        items.add(Material.BUCKET,3);
        items.add(Material.FEATHER,3);
        items.add(Material.IRON_AXE,4);
        items.add(Material.GOLDEN_CARROT,4);
        items.add(Material.CARROT,5);
        items.add(Material.POISONOUS_POTATO,2);
        items.add(Material.CLOCK,1);
        items.add(Material.SEA_LANTERN,2);
    }

    public EntityType randEntity() {
        return entities.getRandomEntity();
    }

    public Material randItem() {
        return items.getRandomItem();
    }
}

