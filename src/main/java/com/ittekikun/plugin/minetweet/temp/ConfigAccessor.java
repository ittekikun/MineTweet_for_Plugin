package com.ittekikun.plugin.minetweet.temp;

import java.io.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigAccessor
{
    private final JavaPlugin plugin;
    private final File pluginFile;
    private final String fileName;

    private File configFile;
    private FileConfiguration fileConfiguration;

    public ConfigAccessor(JavaPlugin plugin, File pluginFile, String fileName)
    {
        if(plugin == null)
        {
            throw new IllegalArgumentException("plugin cannot be null");
        }
//        //代替メソッドわからない
//        if(!plugin.isInitialized())
//        {
//            throw new IllegalArgumentException("plugin must be initialized");
//        }
        if(plugin.getDataFolder() == null)
        {
            throw new IllegalStateException();
        }

        this.plugin = plugin;
        this.pluginFile = pluginFile;
        this.fileName = fileName;

        this.configFile = new File(this.plugin.getDataFolder(), fileName);
    }

    public void reloadConfig()
    {
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getConfig()
    {
        if(fileConfiguration == null)
        {
            this.reloadConfig();
        }
        return fileConfiguration;
    }

    public void saveDefaultConfig()
    {
        if(!configFile.exists())
        {
            if(BukkitUtility.isCB19orLater())
            {
                FileUtility.copyRawFileFromJar(pluginFile, configFile, fileName);
            }
            else
            {
                FileUtility.copyFileFromJar(pluginFile, configFile, fileName);
            }
        }
    }
}