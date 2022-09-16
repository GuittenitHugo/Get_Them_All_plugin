package com.ideagle.plugin.handlers;

import com.ideagle.plugin.GetThemAll;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class SnowballHandler implements Listener {

    public SnowballHandler(GetThemAll plugin) {
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onSnowballThrow(ProjectileLaunchEvent e){

    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent e) {

    }

}
