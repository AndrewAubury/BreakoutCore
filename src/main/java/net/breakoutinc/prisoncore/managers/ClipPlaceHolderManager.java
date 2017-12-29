/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.managers;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import org.bukkit.entity.Player;
import me.clip.placeholderapi.external.EZPlaceholderHook;

/**
 * Created by Andrew on 12/12/2017.
 */
public class ClipPlaceHolderManager extends EZPlaceholderHook {

    private PrisonCore ourPlugin;

    public ClipPlaceHolderManager(PrisonCore ourPlugin) {
        // this is the plugin that is registering the placeholder and the identifier for our placeholder.
        // the format for placeholders is this:
        // %<placeholder identifier>_<anything you define as an identifier in your method below>%
        // the placeholder identifier can be anything you want as long as it is not already taken by another
        // registered placeholder.

        super(ourPlugin, "prisoncore");
        // this is so we can access our main class below
        this.ourPlugin = ourPlugin;
    }

    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        //UNDER THIS POINT IS PLAYER ONLY PLACEHOLDERS!!!
        // always check if the player is null for placeholders related to the player!
        if (p == null) {
            return "";
        }
        // placeholder: %prisoncore_prestige%
        if (identifier.equals("prestige")) {
            PrisonPlayer pp = ourPlugin.getPM().getPlayer(p);
            return (pp.getPrestige());
        }

        // placeholder: %prisoncore_rank%
        if (identifier.equals("mine")) {
            PrisonPlayer pp = ourPlugin.getPM().getPlayer(p);
            return (pp.getRank());
        }

        if (identifier.equals("tickets")) {
            PrisonPlayer pp = ourPlugin.getPM().getPlayer(p);
            return (pp.getTickets()+"");
        }

        if (identifier.equals("displayname")) {
            return p.getDisplayName()   ;
            //return p.getCustomName();
        }
        if (identifier.equals("suffix")) {
            return PrisonCore.getChat().getPlayerSuffix(PrisonCore.getInstance().getServer().getWorlds().get(0).getName(),p);
            //return PrisonCore.getChat().getPlayerSuffix(p);
        }
        if (identifier.equals("progress")) {
            return PrisonCore.getInstance().getRM().getProgressBar(p);
        }
        if (identifier.equals("minenext")) {
            RankManager rm = PrisonCore.getInstance().getRM();
            if(rm.isMax(p)){
                return "MAX";
            }
            return rm.getNext(p);
        }
        if (identifier.equals("remaining")) {
            RankManager rm = PrisonCore.getInstance().getRM();
            return rm.getRemaining(p)+"";
        }

        // anything else someone types is invalid because we never defined %customplaceholder_<what they want a value for>%
        // we can just return null so the placeholder they specified is not replaced.
        return null;
    }
}
