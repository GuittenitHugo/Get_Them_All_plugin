package com.ideagle.plugin;

import com.ideagle.plugin.catchballs.BasicCatchball;
import com.ideagle.plugin.catchballs.BrittleCatchball;
import com.ideagle.plugin.catchballs.PerfectCatchball;
import com.ideagle.plugin.handlers.CatchballHandler;
import com.ideagle.plugin.util.ConfigUtils;
import org.bukkit.plugin.java.JavaPlugin;

public final class GetThemAll extends JavaPlugin {

    public static ConfigUtils _config;

    @Override
    public void onEnable() {
        // Plugin startup logic

        saveDefaultConfig();

        _config = new ConfigUtils(this,"config.yml");

        /* initiating crafting recipes */
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.basic"))
            BasicCatchball.initCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.brittle"))
            BrittleCatchball.initCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.perfect"))
            PerfectCatchball.initCraftingRecipe();

        new CatchballHandler(this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        /* destroy crafting recipes */
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.basic"))
            BasicCatchball.destroyCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.brittle"))
            BrittleCatchball.destroyCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.perfect"))
            PerfectCatchball.destroyCraftingRecipe();
    }
}
