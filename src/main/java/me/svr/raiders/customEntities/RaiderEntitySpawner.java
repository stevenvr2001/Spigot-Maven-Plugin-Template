package me.svr.raiders.customEntities;

import me.svr.raiders.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pillager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RaiderEntitySpawner implements Listener {

    private final Random random = new Random();
    private final Main plugin = Main.getInstance();

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) return;

        EntityType entityType = event.getEntityType();
        if (plugin.getConfig().contains("entities." + entityType.name().toLowerCase())) {
            double spawnChance = plugin.getConfig().getDouble("entities." + entityType.name().toLowerCase() + ".spawn_chance", 0);
            if (random.nextDouble() > spawnChance) return;
            
            event.setCancelled(true);
            Bukkit.getScheduler().runTaskLater(plugin, () -> spawnCustomEntity(event.getLocation(), entityType.name().toLowerCase()), 
                    plugin.getConfig().getInt("entities." + entityType.name().toLowerCase() + ".spawn_interval", 600));
        }
    }

    private void spawnCustomEntity(Location location, String entityTypeName) {
        String configPath = "entities." + entityTypeName;
        EntityType type = EntityType.valueOf(plugin.getConfig().getString(configPath + ".type", "PILLAGER"));
        LivingEntity entity = (LivingEntity) location.getWorld().spawnEntity(location, type);

        if (entity instanceof Pillager) {
            Pillager pillager = (Pillager) entity;
            String customName = plugin.getConfig().getString(configPath + ".name", "Custom Entity");
            pillager.setCustomName(customName);
            pillager.setCustomNameVisible(true);
            pillager.setHealth(plugin.getConfig().getDouble(configPath + ".health", 20.0));

            // Omdat pillagers geen methoden hebben voor 'setWalkSpeed' en 'setCanPickUpItems'
            // We passen alleen het gedrag en de uitrusting toe

            // Voeg handitems en armoritems toe
            addEquipment(pillager, configPath);

            // Voeg actieve effecten toe
            addActiveEffects(pillager, configPath);
        }
    }

    private void addEquipment(Pillager pillager, String configPath) {
        // Handitems
        List<?> weapons = plugin.getConfig().getList(configPath + ".weapons");
        if (weapons != null) {
            for (Object weaponObj : weapons) {
                if (weaponObj instanceof Map<?, ?>) {
                    Map<?, ?> weaponMap = (Map<?, ?>) weaponObj;
                    String itemType = (String) weaponMap.get("item_in_hand");
                    if (itemType != null) {
                        Material item = Material.getMaterial(itemType);
                        if (item != null) {
                            pillager.getEquipment().setItemInMainHand(new ItemStack(item));
                        }
                    }
                    itemType = (String) weaponMap.get("item_in_offhand");
                    if (itemType != null) {
                        Material item = Material.getMaterial(itemType);
                        if (item != null) {
                            pillager.getEquipment().setItemInOffHand(new ItemStack(item));
                        }
                    }
                }
            }
        }

        // Armor items
        List<?> armorItems = plugin.getConfig().getList(configPath + ".armorItems");
        if (armorItems != null) {
            for (Object armorObj : armorItems) {
                if (armorObj instanceof Map<?, ?>) {
                    Map<?, ?> armorMap = (Map<?, ?>) armorObj;
                    String itemType = (String) armorMap.get("feet");
                    if (itemType != null) {
                        Material item = Material.getMaterial(itemType);
                        if (item != null) {
                            pillager.getEquipment().setBoots(new ItemStack(item));
                        }
                    }
                    itemType = (String) armorMap.get("legs");
                    if (itemType != null) {
                        Material item = Material.getMaterial(itemType);
                        if (item != null) {
                            pillager.getEquipment().setLeggings(new ItemStack(item));
                        }
                    }
                    itemType = (String) armorMap.get("chest");
                    if (itemType != null) {
                        Material item = Material.getMaterial(itemType);
                        if (item != null) {
                            pillager.getEquipment().setChestplate(new ItemStack(item));
                        }
                    }
                    itemType = (String) armorMap.get("head");
                    if (itemType != null) {
                        Material item = Material.getMaterial(itemType);
                        if (item != null) {
                            pillager.getEquipment().setHelmet(new ItemStack(item));
                        }
                    }
                }
            }
        }
    }

    private void addActiveEffects(Pillager pillager, String configPath) {
        List<?> effects = plugin.getConfig().getList(configPath + ".activeEffects");
        if (effects != null) {
            for (Object effectObj : effects) {
                if (effectObj instanceof Map<?, ?>) {
                    Map<?, ?> effectMap = (Map<?, ?>) effectObj;
                    PotionEffectType effectType = PotionEffectType.getByName((String) effectMap.get("id"));
                    int amplifier = (int) effectMap.get("amplifier");
                    int duration = (int) effectMap.get("duration");
                    if (effectType != null) {
                        pillager.addPotionEffect(new PotionEffect(effectType, duration, amplifier));
                    }
                }
            }
        }
    }
}
