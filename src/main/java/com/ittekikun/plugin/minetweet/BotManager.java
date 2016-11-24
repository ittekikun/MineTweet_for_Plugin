package com.ittekikun.plugin.minetweet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import twitter4j.TwitterException;

public class BotManager
{
    private MineTweet mineTweet;
    private TwitterManager twitterManager;
    private MineTweetConfig mineTweetConfig;
    private List<String> botMessageList;
    private BukkitScheduler bukkitScheduler;

    public BotManager(MineTweet mineTweet)
    {
        this.mineTweet = mineTweet;
        this.mineTweetConfig = mineTweet.mtConfig;
        this.twitterManager = mineTweet.twitterManager;
    }

    public void botSetup()
    {
        this.bukkitScheduler = Bukkit.getServer().getScheduler();

        this.botMessageList = new ArrayList(this.mineTweetConfig.botMessageList);
        this.bukkitScheduler.runTaskTimer(this.mineTweet, new BotTweetTask(this.botMessageList), 0L, convertSecondToTick(this.mineTweetConfig.tweetCycle));
    }

    public void taskCancel()
    {
        if (this.mineTweetConfig.useBot)
        {
            bukkitScheduler.cancelTasks(this.mineTweet);
        }
    }

    public int convertSecondToTick(int second)
    {
        return second * 20;
    }

    public class BotTweetTask implements Runnable
    {
        public List<String> botMessageList;

        public BotTweetTask(List<String> botMessageList)
        {
            this.botMessageList = botMessageList;
        }

        public void run()
        {
            Collections.rotate(this.botMessageList, 1);
            String message = replaceKeywords(this.botMessageList.get(0));
            try
            {
                BotManager.this.twitterManager.tweet(message);
            }
            catch (TwitterException e)
            {
                e.printStackTrace();
            }
        }

        private String replaceKeywords(String source)
        {
            String result = source;
            if (result.contains(Keyword.KEYWORD_NUMBER))
            {
                ArrayList players = BukkitUtility.getOnlinePlayers();
                String number = Integer.toString((players.size()));

                result = result.replace(Keyword.KEYWORD_NUMBER, number);
            }
            if (result.contains(Keyword.KEYWORD_NEWLINE))
            {
                result = result.replace(Keyword.KEYWORD_NEWLINE, Keyword.SOURCE_NEWLINE);
            }
            if (result.contains(Keyword.KEYWORD_TIME))
            {
                String time = VariousUtility.timeGetter(mineTweetConfig.dateformat);

                result = result.replace(Keyword.KEYWORD_TIME, time);
            }
            return result;
        }
    }
}