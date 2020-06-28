package me.mattdokn.quickfeller;

import org.bukkit.plugin.java.JavaPlugin;

public final class QuickFeller extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getLogger().info("QuickFeller initialized.");
    }

    @Override
    public void onDisable() {}
}
