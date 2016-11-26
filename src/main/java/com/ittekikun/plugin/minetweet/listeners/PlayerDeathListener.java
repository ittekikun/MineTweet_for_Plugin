package com.ittekikun.plugin.minetweet.listeners;

import com.ittekikun.plugin.minetweet.MineTweet;
import com.ittekikun.plugin.minetweet.MineTweetConfig;
import com.ittekikun.plugin.minetweet.TwitterManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import twitter4j.TwitterException;

public class PlayerDeathListener implements Listener
{
    MineTweet plugin;
    MineTweetConfig mtConfig;
    TwitterManager twitterManager;

    public PlayerDeathListener(MineTweet plugin)
    {
        this.plugin = plugin;
        this.mtConfig = plugin.mtConfig;
        this.twitterManager = plugin.twitterManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) throws TwitterException
    {
        String message = event.getDeathMessage();

        twitterManager.tweet(message);
    }
}
