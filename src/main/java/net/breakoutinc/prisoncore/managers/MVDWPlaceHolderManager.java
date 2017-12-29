/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.managers;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import net.breakoutinc.prisoncore.PrisonCore;

/**
 * Created by Andrew on 13/12/2017.
 */
public class MVDWPlaceHolderManager {
    PrisonCore main;
    public MVDWPlaceHolderManager(PrisonCore pc){
        main = pc;
        PlaceholderAPI.registerPlaceholder(main, "pcprestige", new PlaceholderReplacer(){
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(e.getPlayer(), "%prisoncore_prestige%");
            }
        });
        PlaceholderAPI.registerPlaceholder(main, "pcmine", new PlaceholderReplacer(){
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(e.getPlayer(), "%prisoncore_mine%");
            }
        });
        PlaceholderAPI.registerPlaceholder(main, "pctickets", new PlaceholderReplacer(){
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(e.getPlayer(), "%prisoncore_tickets%");
            }
        });

        PlaceholderAPI.registerPlaceholder(main, "pcprogress", new PlaceholderReplacer(){
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(e.getPlayer(), "%prisoncore_progress%");
            }
        });

    }
}
