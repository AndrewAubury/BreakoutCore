package net.breakoutinc.prisoncore;

import net.breakoutinc.prisoncore.managers.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class Config {
    private File file;
    private FileConfiguration fileConfig;
    public boolean dontReload;
    String path;
    String fileName;

    public Config(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        PrisonCore main = PrisonCore.getInstance();
        dontReload = true;
        file = new File(path, fileName);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (main.getResource(fileName) != null) {
                main.saveResource(fileName, false);
            } else {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        FileConfiguration fileConfig = new YamlConfiguration();
        try {
            fileConfig.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.fileConfig = fileConfig;
        ConfigManager.getInstance().addConfig(this);
    }
    public FileConfiguration getConfig() {
        return fileConfig;
    }
    public void reload(){
        //System.out.println("I reloaded!!!");
        FileConfiguration fileConfig = new YamlConfiguration();
        try {
            fileConfig.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.fileConfig = fileConfig;
    }
    public String getName(){
        return fileName;
    }
    public void setReloader(boolean shouldreload){
        dontReload = shouldreload;
    }

    public void save() {
        file = new File(path, fileName);

        try {
            fileConfig.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}