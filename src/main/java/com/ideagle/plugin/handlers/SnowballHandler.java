package com.ideagle.plugin.handlers;

import com.ideagle.plugin.GetThemAll;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class SnowballHandler implements Listener {

    public SnowballHandler(GetThemAll plugin) {
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }
}
