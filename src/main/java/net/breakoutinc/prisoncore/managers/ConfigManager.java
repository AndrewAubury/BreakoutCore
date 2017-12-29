/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.Config;

import java.util.ArrayList;

/**
 * Created by Andrew on 16/12/2017.
 */
public class ConfigManager {
    private static ConfigManager ourInstance = new ConfigManager();
    public static ConfigManager getInstance() {
        return ourInstance;
    }

    ArrayList<Config> configs;

    private ConfigManager() {
        configs = new ArrayList<>();
    }
    public void addConfig(Config conf){
        configs.add(conf);
    }
    public void reloadAll(){
        for(Config cnf : configs){
            if(!cnf.dontReload){
                cnf.reload();
            }
        }
    }
}
