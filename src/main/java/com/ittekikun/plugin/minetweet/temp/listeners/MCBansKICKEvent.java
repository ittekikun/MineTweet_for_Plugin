package com.ittekikun.plugin.minetweet.temp.listeners;

import com.ittekikun.plugin.minetweet.temp.*;
import com.mcbans.firestar.mcbans.events.PlayerKickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import twitter4j.TwitterException;

public class MCBansKICKEvent implements Listener
{
	MineTweet plugin;
	MineTweetConfig mtConfig;
	TwitterManager twittermanager;

	public MCBansKICKEvent(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twittermanager = plugin.twitterManager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerKick(PlayerKickEvent event) throws TwitterException
	{
		if(event.isCancelled())
			return;

		String message = replaceKeywords(mtConfig.kick_message_temp, event.getPlayer(), event.getReason(), event.getSender(), event.getPlayerUUID().toString());

		twittermanager.tweet(message);
	}

	private String replaceKeywords(String source,String name, String reason, String sender, String UUID)
	{
		String result = source;
		if ( result.contains(Keyword.KEYWORD_UUID) )
		{
			result = result.replace(Keyword.KEYWORD_UUID, UUID);
		}
        if ( result.contains(Keyword.KEYWORD_PLAYER) )
        {
            result = result.replace(Keyword.KEYWORD_PLAYER, name);
        }
        if ( result.contains(Keyword.KEYWORD_REASON) )
        {
            result = result.replace(Keyword.KEYWORD_REASON, reason);
        }
        if ( result.contains(Keyword.KEYWORD_SENDER) )
        {
            result = result.replace(Keyword.KEYWORD_SENDER, sender);
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