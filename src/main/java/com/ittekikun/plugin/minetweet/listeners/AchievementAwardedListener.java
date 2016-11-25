package com.ittekikun.plugin.minetweet.listeners;

import com.ittekikun.plugin.minetweet.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import twitter4j.TwitterException;

public class AchievementAwardedListener implements Listener
{
	MineTweet plugin;
	MineTweetConfig mtConfig;
	TwitterManager twitterManager;

	public AchievementAwardedListener(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twitterManager = plugin.twitterManager;
	}

	@EventHandler
	public void onAchievementAwarded(PlayerAchievementAwardedEvent event) throws TwitterException
	{
		String player = event.getPlayer().getName();
		String achievement = event.getAchievement().name();

		//指定された項目がなければ渡された物がそのまま帰ってくる（はず）
		String name = mtConfig.loadAchievementName(achievement);

		String message = replaceKeywords(mtConfig.achievement_message_temp, player, name);

		twitterManager.tweet(message);
	}

	private String replaceKeywords(String source, String name, String achievement)
	{
		String result = source;
		if (result.contains(Keyword.KEYWORD_PLAYER) )
		{
			result = result.replace(Keyword.KEYWORD_PLAYER, name);
		}
		if (result.contains(Keyword.KEYWORD_ACHIEVEMENT) )
		{
			result = result.replace(Keyword.KEYWORD_ACHIEVEMENT, achievement);
		}
		if (result.contains(Keyword.KEYWORD_NEWLINE))
		{
			result = result.replace(Keyword.KEYWORD_NEWLINE, Keyword.SOURCE_NEWLINE);
		}
		if (result.contains(Keyword.KEYWORD_TIME))
		{
			String time = VariousUtility.timeGetter(mtConfig.dateformat);

			result = result.replace(Keyword.KEYWORD_TIME, time);
		}
		return result;
	}
}