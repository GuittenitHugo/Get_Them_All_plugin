package com.ideagle.plugin;

import com.ideagle.plugin.catchballs.BasicBall;
import com.ideagle.plugin.catchballs.BrittleBall;
import com.ideagle.plugin.catchballs.PerfectBall;
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
            BasicBall.initCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.brittle"))
            BrittleBall.initCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.perfect"))
            PerfectBall.initCraftingRecipe();

        new CatchballHandler(this);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        /* destroy crafting recipes */
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.basic"))
            BasicBall.destroyCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.brittle"))
            BrittleBall.destroyCraftingRecipe();
        if (_config.getConfig().getBoolean("recipes.allowed_to_craft.perfect"))
            PerfectBall.destroyCraftingRecipe();
    }
}
