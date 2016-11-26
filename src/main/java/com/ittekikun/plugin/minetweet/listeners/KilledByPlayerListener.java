package com.ittekikun.plugin.minetweet.listeners;

import com.ittekikun.plugin.minetweet.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import static com.ittekikun.plugin.minetweet.Keyword.*;

public class KilledByPlayerListener  implements Listener
{
    MineTweet plugin;
    MineTweetConfig mtConfig;
    TwitterManager twitterManager;

    public KilledByPlayerListener(MineTweet plugin)
    {
        this.plugin = plugin;
        this.mtConfig = plugin.mtConfig;
        this.twitterManager = plugin.twitterManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player death = event.getEntity();
        EntityDamageEvent cause = event.getEntity().getLastDamageCause();

        if (cause instanceof EntityDamageByEntityEvent)
        {
            Entity entity = ((EntityDamageByEntityEvent)cause).getDamager();

            if (entity instanceof Player)
            {
                Player kill = (Player)entity;

                ItemStack hand = BukkitUtility.getItemInHand(kill);
                String handItem = hand.getType().toString();

                replaceKeywords(mtConfig.join_message_temp, death.getName(), kill.getName(), handItem);
            }
        }
    }

    private String replaceKeywords(String source, String killer, String deader, String item)
    {
        String result = source;
        if (result.contains(KEYWORD_KILLER))
        {
            result = result.replace(KEYWORD_KILLER, killer);
        }
        if (result.contains(KEYWORD_DEADER))
        {
            result = result.replace(KEYWORD_DEADER, deader);
        }
        if (result.contains(KEYWORD_ITEM))
        {
            result = result.replace(KEYWORD_ITEM, item);
        }
        if (result.contains(KEYWORD_NEWLINE))
        {
            result = result.replace(KEYWORD_NEWLINE, SOURCE_NEWLINE);
        }
        if (result.contains(KEYWORD_TIME))
        {
            String time = VariousUtility.timeGetter(mtConfig.dateformat);

            result = result.replace(KEYWORD_TIME, time);
        }
        return result;
    }
}