package com.ittekikun.plugin.minetweet;

import com.ittekikun.plugin.itkcore.utility.BukkitUtility;
import com.ittekikun.plugin.itkcore.utility.VariousUtility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;
import twitter4j.TwitterException;

import static com.ittekikun.plugin.minetweet.Keyword.*;

public class BotManager
{
    public MineTweet mineTweet;
    public TwitterManager twitterManager;
    public MineTweetConfig mineTweetConfig;
    public List<String> botMessageList;
    public BukkitScheduler bukkitScheduler;

    public BotManager(MineTweet mineTweet)
    {
        this.mineTweet = mineTweet;
        this.mineTweetConfig = mineTweet.mineTweetConfig;
        this.twitterManager = mineTweet.twitterManager;
    }

    public void botSetup()
    {
        if (this.mineTweetConfig.useBot)
        {
            this.bukkitScheduler = Bukkit.getServer().getScheduler();

            this.botMessageList = new ArrayList(this.mineTweetConfig.botMessageList);
            this.bukkitScheduler.runTaskTimer(this.mineTweet, new BotTweetTask(this.botMessageList), 0L, convertSecondToTick(this.mineTweetConfig.tweetCycle));
        }
    }

    public void taskCancel()
    {
        if (this.mineTweetConfig.useBot)
        {
            bukkitScheduler.cancelTasks(this.mineTweet);
        }
    }

    //簡単だけど分かりやすくするために
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
            if (result.contains(KEYWORD_NUMBER))
            {
                ArrayList players = BukkitUtility.getOnlinePlayers();
                String number = Integer.toString((players.size()));

                result = result.replace(KEYWORD_NUMBER, number);
            }
            if (result.contains(KEYWORD_NEWLINE))
            {
                result = result.replace(KEYWORD_NEWLINE, SOURCE_NEWLINE);
            }
            if (result.contains(KEYWORD_TIME))
            {
                String time = VariousUtility.timeGetter(mineTweetConfig.dateformat);

                result = result.replace(KEYWORD_TIME, time);
            }
            return result;
        }
    }
}