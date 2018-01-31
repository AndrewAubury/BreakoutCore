package net.breakoutinc.prisoncore.core.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.breakoutinc.prisoncore.Config;
import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.managers.ChatManager;
import net.breakoutinc.prisoncore.objects.DiscordOfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// ------------------------------
// Copyright (c) PiggyPiglet 2017
// https://www.piggypiglet.me
// ------------------------------
public class ChatHandler {
    public FileConfiguration cfg;
    public ArrayList<String> formatList;
    private Config config;
    private ConfigurationSection section;
    private ConfigurationSection defaultFormat;
    public ChatHandler() {
        final PrisonCore main = PrisonCore.getInstance();
        formatList = new ArrayList<String>();
        config = new Config(main.getDataFolder().getPath(), "chat.yml");

        loadConfigs();
    }

    public String formatMessage(Player p, String message){
        formatList.clear();
        config.reload();
        cfg = config.getConfig();
        section = cfg.getConfigurationSection("formats");
        for(String str : section.getKeys(false)){
            if(section.getConfigurationSection(str).getString("perm").equalsIgnoreCase("default")){
                defaultFormat = section.getConfigurationSection(str);
            }
            formatList.add(str);
        }


        ConfigurationSection finalFormat = null;
        for(String format : formatList){
            ConfigurationSection formatSection = section.getConfigurationSection(format);
            if(p.hasPermission(formatSection.getString("perm"))){
                if(finalFormat == null){
                    finalFormat = formatSection;
                }else{
                    if(formatSection.getInt("priority") > finalFormat.getInt("priority")){
                        finalFormat = formatSection;
                    }
                }
            }
        }
        if(finalFormat == null){
            finalFormat = defaultFormat;
        }
        String formatString = finalFormat.getString("format");

        if(!p.hasPermission("prisoncore.bypassfilter")){
            message = filterText(message);
        }

//        if(p.getName().equalsIgnoreCase("ItsYahGalEmily")){
//            message = message.replaceAll("(?i)"+ Pattern.quote("andrew"), "daddy");
//            message = message.replaceAll("(?i)"+ Pattern.quote("andrewa2012"), "daddy");
//        }

        if(p instanceof DiscordOfflinePlayer){
            String prefix = new Config(PrisonCore.getInstance().getDataFolder().getPath(),"discord.yml").getConfig().getString("messageprefix");
            prefix = ChatColor.translateAlternateColorCodes('&',prefix);
            formatString = prefix + formatString;
        }

        String withPlaceholdersSet = PlaceholderAPI.setPlaceholders(p, formatString);


        if(p.hasPermission("breakout.colorchat")){
            message = ChatColor.translateAlternateColorCodes('&',message);
        }

        withPlaceholdersSet = withPlaceholdersSet.replaceAll("%message%","");
        withPlaceholdersSet = withPlaceholdersSet + message;
        return withPlaceholdersSet;
    }


    static Map<String, String[]> words;
    static int largestWordLength = 0;
    public static void loadConfigs() {
        words = new HashMap<>();
        largestWordLength = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://docs.google.com/spreadsheets/d/1lsVf_r9P4G89ZVZmSZUpwiRdsFV1eP-FKkO2FwDDOzg/export?format=csv").openConnection().getInputStream()));
            String line = "";
            int counter = 0;
            while((line = reader.readLine()) != null) {
                counter++;
                String[] content = null;
                try {
                    content = line.split(",");
                    if(content.length == 0) {
                        continue;
                    }
                    String word = content[0];
                    String[] ignore_in_combination_with_words = new String[]{};
                    if(content.length > 1) {
                        ignore_in_combination_with_words = content[1].split("_");
                    }

                    if(word.length() > largestWordLength) {
                        largestWordLength = word.length();
                    }
                    words.put(word.replaceAll(" ", ""), ignore_in_combination_with_words);

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
            System.out.println("Loaded " + counter + " words to filter out");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Iterates over a String input and checks whether a cuss word was found in a list, then checks if the word should be ignored (e.g. bass contains the word *ss).
     * @param input
     * @return
     */
    public static ArrayList<String> badWordsFound(String input) {
        if(input == null) {
            return new ArrayList<>();
        }

        // remove leetspeak
        String ori = input;
//        input = input.replaceAll("1","i");
//        input = input.replaceAll("!","i");
//        input = input.replaceAll("3","e");
//        input = input.replaceAll("4","a");
//        input = input.replaceAll("@","a");
//        input = input.replaceAll("5","s");
//        input = input.replaceAll("7","t");
//        input = input.replaceAll("0","o");
//        input = input.replaceAll("9","g");

        ArrayList<String> badWords = new ArrayList<>();
        input = input.toLowerCase().replaceAll("[^a-zA-Z]", "");
        ori = ori.toLowerCase().replaceAll("[^a-zA-Z]", "");

        // iterate over each letter in the word
        for(int start = 0; start < input.length(); start++) {
            // from each letter, keep going to find bad words until either the end of the sentence is reached, or the max word length is reached.
            for(int offset = 1; offset < (input.length()+1 - start) && offset < largestWordLength; offset++)  {
                String wordToCheck = input.substring(start, start + offset).toLowerCase();
                String wordInMessage = ori.substring(start, start + offset).toLowerCase();
                if(words.containsKey(wordToCheck)) {
                    // for example, if you want to say the word bass, that should be possible.
                    String[] ignoreCheck = words.get(wordToCheck);
                    boolean ignore = false;
                    for(int s = 0; s < ignoreCheck.length; s++ ) {
                        if(input.contains(ignoreCheck[s])) {
                            ignore = true;
                            break;
                        }
                    }
                    if(!ignore) {
                        badWords.add(wordInMessage);
                    }
                }
            }
        }


        for(String s: badWords) {
            //Server.getSlackManager().queue(s + " qualified as a bad word in a username");
        }
        return badWords;

    }


    public static String filterText(String input) {
        ArrayList<String> badWords = badWordsFound(input);
        for(String badWord : badWords){
            Material replacer = Material.values()[new Random().nextInt(Material.values().length)];
            String replacement = replacer.name();
            replacement = replacement.toLowerCase();
            replacement = replacement.replaceAll("_"," ");

            input = input.replaceAll("(?i)"+Pattern.quote(badWord), replacement);
        }
        return input;
    }

}
