package net.breakoutinc.prisoncore;

import net.breakoutinc.prisoncore.commands.*;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.events.*;
import net.breakoutinc.prisoncore.managers.*;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import net.breakoutinc.prisoncore.objects.TimePlayed;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created by Andrew on 24/11/2017.
 */

public class PrisonCore extends JavaPlugin {
    private static PrisonCore instance;

    private PlayerManager pm;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    private RankManager RM;
    public static PrisonCore getInstance() {
        return instance;
    }
    @Override
    public void onEnable() {
        instance = this;
        pm = new PlayerManager();

        //getCommand("timetest").setExecutor(new CommandHandler());
        getCommand("rankup").setExecutor(new RankUpCommand());
        getCommand("prestige").setExecutor(new PrestigeCommand());
        getCommand("ranks").setExecutor(new RanksCommand());
        getCommand("corereload").setExecutor(new CoreReloadCommand());
        getCommand("payxp").setExecutor(new payxpCommand());
        getCommand("link").setExecutor(new LinkCommand());
        getCommand("rankupmax").setExecutor(new RankUpMaxCommand());
        getCommand("plugin").setExecutor(new pluginCommand());
        //getCommand("pc").setExecutor(new CommandHandler());
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new AsyncChatEvent(), this);
        getServer().getPluginManager().registerEvents(new Enforcing2FAEvent(), this);
        getServer().getPluginManager().registerEvents(new commandEvent(), this);

        new ClipPlaceHolderManager(instance).hook();
        new MVDWPlaceHolderManager(instance);
        if (!setupEconomy() ) {
            getServer().getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();
        RM = new RankManager(this);

        for(Player p : getServer().getOnlinePlayers()){
            PrisonPlayer pp = pm.getPlayer(p);
            new TimePlayed(pp);
        }
        new AnouncementManager();
        Thread bot = new Thread(new Runnable() {
            @Override
            public void run() {
                new BreakoutBot();
            }
        });
   bot.start();
   super.onEnable();
    }

    @Override
    public void onDisable() {
        pm.saveAll();
        if(BreakoutBot.getInstance().bridge != null){
            BreakoutBot.getInstance().bridge.save();
            BreakoutBot.getInstance().getJDA().shutdownNow();
        }
    }

    public PlayerManager getPM() {
        return pm;
    }
    public RankManager getRM() {
        return RM;
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
}