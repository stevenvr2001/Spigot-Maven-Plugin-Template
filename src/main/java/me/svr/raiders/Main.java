package me.svr.raiders;

import me.svr.raiders.commands.SpawnEntityCommand;
import me.svr.raiders.customEntities.CustomEntityManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.getCommand("spawnentity").setExecutor(new SpawnEntityCommand(this));
    }

    public static Main getInstance() {
        return instance;
    }
}
