package net.breakoutinc.prisoncore;

import net.breakoutinc.prisoncore.commands.*;
import net.breakoutinc.prisoncore.core.discord.BreakoutBot;
import net.breakoutinc.prisoncore.events.*;
import net.breakoutinc.prisoncore.islands.IslandManager;
import net.breakoutinc.prisoncore.islands.managers.IslandCommandHandler;
import net.breakoutinc.prisoncore.managers.*;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import net.breakoutinc.prisoncore.objects.TimePlayed;
import net.dv8tion.jda.core.entities.Role;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Andrew on 24/11/2017.
 */

public class PrisonCore extends JavaPlugin {
    private static PrisonCore instance;

    public Config miscConfig = new Config(getDataFolder().getPath(),"misc.yml");

    public Config tabConfig = null;

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

//        getLogger().addHandler();
        instance = this;
        pm = new PlayerManager();
        tabConfig = new Config(getDataFolder().getPath(), "tablist.yml");
        //getCommand("timetest").setExecutor(new IslandCommandHandler());
        getCommand("rankup").setExecutor(new RankUpCommand());
        getCommand("prestige").setExecutor(new PrestigeCommand());
        getCommand("ranks").setExecutor(new RanksCommand());
        getCommand("corereload").setExecutor(new CoreReloadCommand());
        getCommand("payxp").setExecutor(new payxpCommand());
        getCommand("link").setExecutor(new LinkCommand());
        getCommand("rankupmax").setExecutor(new RankUpMaxCommand());
        getCommand("silentmute").setExecutor(new SilentMuteCommand());
        // getCommand("prisoncoretest").setExecutor(new TestCommand());
        // getCommand("plugins").setExecutor(new pluginCommand());
        getCommand("island").setExecutor(new IslandCommandHandler());
        getCommand("island").setTabCompleter((IslandCommandHandler) getCommand("island").getExecutor());
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new AsyncChatEvent(), this);
        getServer().getPluginManager().registerEvents(new Enforcing2FAEvent(), this);
        getServer().getPluginManager().registerEvents(new commandEvent(), this);
        getServer().getPluginManager().registerEvents(new IslandBlockEvent(), this);

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
        Thread bot = new Thread(() -> new BreakoutBot());
        bot.start();

        IslandManager.getInstance().loadDB();

   super.onEnable();
    }

    @Override
    public void onDisable() {
        pm.saveAll();
        if(BreakoutBot.getInstance().bridge != null){
            BreakoutBot.getInstance().bridge.save();
            Role r = BreakoutBot.getInstance().getLinkedRole();
            BreakoutBot.getInstance().getChatChannel().putPermissionOverride(r).setDeny(net.dv8tion.jda.core.Permission.MESSAGE_READ, net.dv8tion.jda.core.Permission.MESSAGE_WRITE).complete();


            //BreakoutBot.getInstance().getJDA().shutdownNow();

        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return super.onTabComplete(sender, command, alias, args);
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

    public String formatValue(double value)
    {
        if(value == 0.00){
            return "0";
        }
        String suffixes = ",K, M, B, Tri, Quad, Quint, Sextil, Septil, Octil, Nonil, Decil, Undecil, Duodecil, Tredecil, Quattuordeci, Quindecillion, Sexdecil, Septendecil, Octodecil, Novemdecil, Vigintil";

        String[] suffix = suffixes.split(",");

        NumberFormat formatter = new DecimalFormat("#,###.#");
        int power = (int)StrictMath.log10(value);
        if (power == 303)
        {
            String formattedNumber = formatter.format(value);
            if (formattedNumber.length() > 4)
            {
                StringBuilder sb = new StringBuilder(formattedNumber);
                for (int i = 4; i < formattedNumber.length(); i++) {
                    sb.deleteCharAt(i);
                }
                formattedNumber = sb.toString().replace(',', '.');
            }
            return formattedNumber + " Centillion";
        }
        if (power > 303)
        {
            System.out.println("Tried calculating " + value + " but power was above 303 (centillion power), so returned formatted number");
            return formatter.format(value);
        }
        if (power >= 100)
        {
            String formattedNumber = formatter.format(value);
            String reversed = new StringBuffer(formatter.format(value).replaceAll(",", "")).reverse().toString();
            StringBuilder sb = new StringBuilder(reversed);
            System.out.println(formatter.format(value).replaceAll(",", ""));
            for (int i = 0; i < 99; i++) {
                sb.deleteCharAt(i);
            }
            reversed = sb.toString();
            return formatter.format(Double.parseDouble(new StringBuffer(reversed).reverse().toString())) + " Googol";
        }
        value /= Math.pow(10.0D, power / 3 * 3);
        String formattedNumber = formatter.format(value);
        if (formattedNumber.length() > 4)
        {
            StringBuilder sb = new StringBuilder(formattedNumber);
            for (int i = 4; i < formattedNumber.length(); i++) {
                sb.deleteCharAt(i);
            }
            formattedNumber = sb.toString().replace(',', '.');
        }
        return formattedNumber + suffix[(power / 3)];
    }
}