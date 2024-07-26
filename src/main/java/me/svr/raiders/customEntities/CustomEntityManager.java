package me.svr.raiders.customEntities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.PolarBear;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.scheduler.BukkitRunnable;
import me.svr.raiders.Main;

import java.util.Random;
import java.util.List;

public class CustomEntityManager {

    private final Main plugin;
    private final Random random;

    public CustomEntityManager(Main plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public boolean spawnEntity(String entityType, Location location) {
        switch (entityType.toLowerCase()) {
            case "raider":
                return spawnRaider(location);
            case "raider-boss":
                return spawnRaiderBoss(location);
            case "raid-runner":
                return spawnRaidRunner(location);
            case "raid-party":
                return spawnRaidParty(location);
            default:
                return false;
        }
    }

    private boolean spawnRaider(Location location) {
        Pillager raider = (Pillager) location.getWorld().spawnEntity(location, EntityType.PILLAGER);

        raider.setCustomName("Raider");
        raider.setCustomNameVisible(true);

        double maxHealth = 65;
        raider.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        raider.setHealth(maxHealth);

        raider.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);

        ItemStack mainHandItem = new ItemStack(Material.CROSSBOW);
        raider.getEquipment().setItemInMainHand(mainHandItem);

        ItemStack offHandItem = new ItemStack(Material.SHIELD);
        raider.getEquipment().setItemInOffHand(offHandItem);

        raider.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        raider.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        raider.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        raider.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));

        raider.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, Integer.MAX_VALUE, 0));

        raider.setAI(true);

        return true;
    }

    private boolean spawnRaiderBoss(Location location) {
        PolarBear polarBear = (PolarBear) location.getWorld().spawnEntity(location, EntityType.POLAR_BEAR);

        Pillager raiderBoss = (Pillager) location.getWorld().spawnEntity(location.add(0, 1, 0), EntityType.PILLAGER);

        raiderBoss.setCustomName("Raider Boss");
        raiderBoss.setCustomNameVisible(true);

        double maxHealth = 120;
        raiderBoss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        raiderBoss.setHealth(maxHealth);

        raiderBoss.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3);

        ItemStack mainHandItem = new ItemStack(Material.CROSSBOW);
        raiderBoss.getEquipment().setItemInMainHand(mainHandItem);

        ItemStack offHandItem = new ItemStack(Material.SHIELD);
        raiderBoss.getEquipment().setItemInOffHand(offHandItem);

        raiderBoss.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        raiderBoss.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
        raiderBoss.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        raiderBoss.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));

        raiderBoss.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, Integer.MAX_VALUE, 0));

        polarBear.addPassenger(raiderBoss);
        polarBear.setTarget(raiderBoss.getTarget());
        polarBear.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.5);
        raiderBoss.setAI(true);

        return true;
    }

    private boolean spawnRaidRunner(Location location) {
        Zombie raidRunner = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);

        raidRunner.setCustomName("Raid-runner");
        raidRunner.setCustomNameVisible(true);

        double maxHealth = 25;
        raidRunner.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);
        raidRunner.setHealth(maxHealth);
        raidRunner.setCanBreakDoors(true);

        ItemStack mainHandItem = new ItemStack(Material.IRON_AXE);
        raidRunner.getEquipment().setItemInMainHand(mainHandItem);

        raidRunner.getEquipment().setBoots(new ItemStack(Material.LEATHER_BOOTS));
        raidRunner.getEquipment().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
        raidRunner.getEquipment().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
        raidRunner.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));

        raidRunner.setAI(true);

        // Voeg een periodieke taak toe om deuren te breken
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!raidRunner.isValid()) {
                    cancel();
                    return;
                }

                Location loc = raidRunner.getLocation();
                List<Block> nearbyBlocks = raidRunner.getNearbyEntities(10, 10, 10).stream()
                        .filter(entity -> entity instanceof Block)
                        .map(entity -> (Block) entity)
                        .toList();

                for (Block block : nearbyBlocks) {
                    Material blockType = block.getType();
                    if (blockType == Material.LEGACY_WOODEN_DOOR || blockType == Material.LEGACY_IRON_DOOR_BLOCK ||
                        blockType == Material.LEGACY_SPRUCE_DOOR || blockType == Material.LEGACY_BIRCH_DOOR ||
                        blockType == Material.LEGACY_JUNGLE_DOOR || blockType == Material.LEGACY_ACACIA_DOOR ||
                        blockType == Material.LEGACY_DARK_OAK_DOOR) {
                        block.breakNaturally(); // Deur breken
                        return; // Stop na het breken van de eerste deur
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 40L); // Start de taak met een interval van 20 seconden en elke 40 seconden daarna

        return true;
    }

    private boolean spawnRaidParty(Location location) {
        // Spawn de Raider Boss op een Polar Bear
        spawnRaiderBoss(location);

        // Spawn 2 Raid-Runners
        for (int i = 0; i < 2; i++) {
            spawnRaidRunner(location.clone().add(random.nextDouble() * 10 - 5, 0, random.nextDouble() * 10 - 5));
        }

        // Spawn een willekeurig aantal Raiders (tussen de 3 en 8)
        int numRaiders = random.nextInt(6) + 3; // Tussen 3 en 8
        for (int i = 0; i < numRaiders; i++) {
            spawnRaider(location.clone().add(random.nextDouble() * 10 - 5, 0, random.nextDouble() * 10 - 5));
        }

        return true;
    }

}
