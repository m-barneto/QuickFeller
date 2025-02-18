package me.mattdokn.quickfeller;

import org.bukkit.plugin.java.JavaPlugin;

public final class QuickFeller extends JavaPlugin {
    public static QuickFeller instance;
    @Override
    public void onEnable() {
        // Save a copy of the default config if one doesn't exist
        this.saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("QuickFeller initialized.");

        instance = this;
    }
}
