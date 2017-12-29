/*
 * =-------------------------------------=
 * = Copyright (c) AndrewAubury 2017 =
 * =  https://www.AndrewAubury.me   =
 * =-------------------------------------=
 */

package net.breakoutinc.prisoncore.core.discord;

import net.breakoutinc.prisoncore.PrisonCore;
import net.breakoutinc.prisoncore.core.chat.ChatHandler;
import net.breakoutinc.prisoncore.objects.DiscordOfflinePlayer;
import net.breakoutinc.prisoncore.objects.PrisonPlayer;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.react.PrivateMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;

/**
 * Created by Andrew on 14/12/2017.
 */
public class MessageReciveEvent extends ListenerAdapter {
    BreakoutBot bot;
    JDA jda;
    PrisonCore core;

    public MessageReciveEvent() {
        bot = BreakoutBot.getInstance();

    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        jda = bot.jda;
        core = bot.core;

        if (e.getAuthor() == null) return;
        if (e.getAuthor().isBot()) return;

        if (e.isFromType(ChannelType.PRIVATE)) {
            String msg = bot.cfg.getConfig().getString("messages.noprivate");
            String mcuuid = bot.bridge.getMinecraftUUID(e.getAuthor().getId());

            OfflinePlayer op = PrisonCore.getInstance().getServer().getOfflinePlayer(UUID.fromString(mcuuid));
            DiscordOfflinePlayer dop = new DiscordOfflinePlayer(op,e.getAuthor().getId());
            if(e.getMessage().getContent().startsWith("/")){
                String cmd = e.getMessage().getContent().replaceFirst("/","");
                dop.performCommand(cmd);
                return;
            }

            e.getChannel().sendMessage(msg).complete();
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
            jda = bot.jda;
            core = bot.core;

            if (e.getMember() == null) return;
            if (e.getAuthor().isBot()) return;

            if (e.isFromType(ChannelType.PRIVATE)) {
                String msg = bot.cfg.getConfig().getString("messages.noprivate");
                String mcuuid = bot.bridge.getMinecraftUUID(e.getAuthor().getId());

                OfflinePlayer op = PrisonCore.getInstance().getServer().getOfflinePlayer(UUID.fromString(mcuuid));
                DiscordOfflinePlayer dop = new DiscordOfflinePlayer(op,e.getAuthor().getId());
                if(e.getMessage().getContent().startsWith("/")){
                    String cmd = e.getMessage().getContent().replaceFirst("/","");
                    dop.performCommand(cmd);
                    e.getMessage().delete().complete();
                    return;
                }

                e.getChannel().sendMessage("msg").complete();
            } else {
                if (e.getChannel().getId().equalsIgnoreCase(bot.cfg.getConfig().getString("linkchannelid"))) {
                    //Start Link Request
                    //e.getChannel().sendMessage("Fuck! im meant to be making "+e.getAuthor().getAsMention()+" a link code!? ").complete();
                    e.getMessage().delete().queue();
                    String code = bot.bridge.makeCode(e.getAuthor().getId());
                    String msg = bot.cfg.getConfig().getString("messages.linkcode");
                    if(msg.contains("%code%")){
                        msg = msg.replace("%code%",code);
                    }
                    e.getAuthor().openPrivateChannel().complete().sendMessage(msg).complete();
                    //bot.bridge.assignRole(e.getAuthor().getId());
                    return;
                }
                if (e.getChannel().getId().equalsIgnoreCase(bot.cfg.getConfig().getString("chatchannelid"))) {
                    String mcuuid = bot.bridge.getMinecraftUUID(e.getAuthor().getId());
                    if(mcuuid == null){
                        e.getMessage().delete().queue();
                    }
                    e.getMessage().getContentRaw();
                    OfflinePlayer op = PrisonCore.getInstance().getServer().getOfflinePlayer(UUID.fromString(mcuuid));
                    DiscordOfflinePlayer dop = new DiscordOfflinePlayer(op,e.getAuthor().getId());


                    if(op != null){
                        if(op.isBanned()){
                            e.getMessage().delete().complete();
                            return;
                        }
                        if(e.getMessage().getContent().startsWith("/")){
                            String cmd = e.getMessage().getContent().replaceFirst("/","");
                            dop.performCommand(cmd);
                            e.getMessage().delete().complete();
                            return;
                        }

                        dop.chat(e.getMessage().getContentStripped());
                    }
                    e.getMessage().delete().queue();
                    return;

                }
                if(e.getMessage().getContent().startsWith(".announce ")){
                    Channel chan = bot.guild.getTextChannelById(bot.cfg.getConfig().getString("announcementchannelid"));
                    chan.getManager();
                    if(e.getMember().hasPermission(chan, Permission.MESSAGE_WRITE)){
                        String msg = e.getMessage().getContent();
                        msg = msg.replaceFirst(".announce ","");

                        Color c = Color.decode(bot.cfg.getConfig().getString("announcementcolor"));
                        MessageEmbed me = new EmbedBuilder().setDescription(msg).setColor(c).build();
                        TextChannel tc = (TextChannel) chan;
                        tc.sendMessage(me).queue();
                        e.getMessage().delete().queue();
                    }

                }


            }
    }
}
