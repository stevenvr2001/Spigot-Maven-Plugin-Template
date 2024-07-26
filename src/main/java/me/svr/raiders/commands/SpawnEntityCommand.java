package me.svr.raiders.commands;

import me.svr.raiders.customEntities.CustomEntityManager; // Zorg ervoor dat dit de juiste import is
import me.svr.raiders.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class SpawnEntityCommand implements CommandExecutor {

    private final Main plugin;
    private final CustomEntityManager entityManager;

    public SpawnEntityCommand(Main plugin) {
        this.plugin = plugin;
        this.entityManager = new CustomEntityManager(plugin); // Creëer een instantie van CustomEntityManager
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Usage: /spawnentity <entityType>");
            return false;
        }

        String entityType = args[0];
        Location location = player.getLocation();

        // Roep spawnEntity aan via de instantie van CustomEntityManager
        boolean result = entityManager.spawnEntity(entityType, location);

        if (result) {
            player.sendMessage("Entity spawned successfully.");
        } else {
            player.sendMessage("Failed to spawn entity.");
        }

        return true;
    }
}
