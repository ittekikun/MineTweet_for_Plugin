package com.ittekikun.plugin.minetweet;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.ittekikun.plugin.minetweet.Messenger.MessageType.INFO;
import static com.ittekikun.plugin.minetweet.Messenger.MessageType.SEVERE;
import static com.ittekikun.plugin.minetweet.Messenger.MessageType.WARNING;

public class Messenger
{
    public static void messageToSender(CommandSender sender, MessageType messageType, String message)
    {
        if(messageType == INFO)
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(MineTweet.prefix + ChatColor.AQUA + "[情報] " + ChatColor.WHITE + message);
            } else
            {
                MineTweet.mtLogger.info(message);
            }
        }
        else if(messageType == WARNING)
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(MineTweet.prefix + ChatColor.YELLOW + "[警告] " + ChatColor.WHITE + message);
            } else
            {
                MineTweet.mtLogger.warning(message);
            }
        }
        else if(messageType == SEVERE)
        {
            if (sender instanceof Player)
            {
                sender.sendMessage(MineTweet.prefix + ChatColor.RED + "[重大] " + ChatColor.WHITE + message);
            }
            else
            {
                MineTweet.mtLogger.severe(message);
            }
        }
    }

    public enum MessageType
    {
        INFO,
        WARNING,
        SEVERE
    }
}