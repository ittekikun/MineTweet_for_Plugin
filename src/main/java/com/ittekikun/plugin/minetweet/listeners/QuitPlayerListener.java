package com.ittekikun.plugin.minetweet.listeners;

import com.ittekikun.plugin.minetweet.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import twitter4j.TwitterException;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class QuitPlayerListener implements Listener
{
	MineTweet plugin;
	MineTweetConfig mtConfig;
	TwitterManager twitterManager;

	public QuitPlayerListener(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twitterManager = plugin.twitterManager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuitPlayer(final PlayerQuitEvent event) throws TwitterException
	{
		if (!mtConfig.tweetWithImage)
		{
			ArrayList players = BukkitUtility.getOnlinePlayers();
			String number = Integer.toString((players.size()));

			String Message = replaceKeywords(mtConfig.quit_message_temp, event.getPlayer().getName(), number, event.getPlayer().getUniqueId().toString());

			twitterManager.tweet(Message);
		}
		else
		{
			ArrayList players = BukkitUtility.getOnlinePlayers();
			final String number = Integer.toString((players.size() - 1));

			//画像生成でラグが起きるので別スレッド
			plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						String uuid = UUID.randomUUID().toString();
						File tweetImage = new File(plugin.getDataFolder(), uuid + ".png");

						VariousUtility.generationPlayerImage(event.getPlayer().getName(), "LEFT THE GAME!", tweetImage);

						String message = replaceKeywords(mtConfig.quit_message_temp, event.getPlayer().getName(), number, event.getPlayer().getUniqueId().toString());
						twitterManager.tweet(message, tweetImage);
						tweetImage.delete();
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			});
		}
	}

	private String replaceKeywords(String source, String name, String number, String UUID)
	{
		String result = source;
		if ( result.contains(Keyword.KEYWORD_UUID) )
		{
			result = result.replace(Keyword.KEYWORD_UUID, UUID);
		}
		if (result.contains(Keyword.KEYWORD_PLAYER))
		{
			result = result.replace(Keyword.KEYWORD_PLAYER, name);
		}
		if (result.contains(Keyword.KEYWORD_NUMBER))
		{
			result = result.replace(Keyword.KEYWORD_NUMBER, number);
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