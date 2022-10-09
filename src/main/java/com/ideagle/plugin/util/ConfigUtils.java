package com.ideagle.plugin.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class ConfigUtils {

    private File _file;
    private FileConfiguration _config;

    public ConfigUtils(Plugin plugin, String path){
        this(plugin.getDataFolder().getAbsolutePath()+"/"+path);
    }

    public ConfigUtils(String path){
        this._file = new File(path);
        this._config = YamlConfiguration.loadConfiguration(this._file);
    }

    public boolean save() {
        try {
            this._config.save(this._file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile() {
        return _file;
    }


    public FileConfiguration getConfig() {
        return _config;
    }

}
