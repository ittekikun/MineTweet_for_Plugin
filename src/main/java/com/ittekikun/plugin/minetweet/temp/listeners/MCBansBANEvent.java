package com.ittekikun.plugin.minetweet.temp.listeners;

import com.ittekikun.plugin.minetweet.temp.*;
import com.mcbans.firestar.mcbans.events.PlayerBanEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import twitter4j.TwitterException;

public class MCBansBANEvent implements Listener
{
	MineTweet plugin;
	MineTweetConfig mtConfig;
	TwitterManager twittermanager;

	public MCBansBANEvent(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twittermanager = plugin.twitterManager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerBanned(PlayerBanEvent event) throws TwitterException
    {
    	if(event.isCancelled())
    		return;

		String message = "null";

		switch(mtConfig.mcBansBanTweet)
		{
			//GBAN LBAN
			case 1:
				if(event.isGlobalBan())
				{
					message = replaceKeywords(mtConfig.ban_message_temp, event.getPlayerName(), event.getReason(), event.getSenderName(), event.getPlayerUUID().toString(), mtConfig.bantype_gban);
				}
				else if(event.isLocalBan())
				{
					message = replaceKeywords(mtConfig.ban_message_temp, event.getPlayerName(), event.getReason(), event.getSenderName(), event.getPlayerUUID().toString(), mtConfig.bantype_lban);
				}
				break;

			//GBAN
			case 2: 
				if (event.isGlobalBan())
					message =  replaceKeywords(mtConfig.ban_message_temp, event.getPlayerName(), event.getReason(), event.getSenderName(), event.getPlayerUUID().toString(), mtConfig.bantype_gban);
				break;

			//LBAN
			case 3:
				if (event.isLocalBan())
					message = replaceKeywords(mtConfig.ban_message_temp, event.getPlayerName(), event.getReason(), event.getSenderName(), event.getPlayerUUID().toString(), mtConfig.bantype_lban);
				break;
	    }
        twittermanager.tweet(message);
    }

	private String replaceKeywords(String source, String name, String reason, String sender, String UUID, String banType)
	{
		String result = source;
		if ( result.contains(Keyword.KEYWORD_UUID) )
		{
			result = result.replace(Keyword.KEYWORD_UUID, UUID);
		}
		if ( result.contains(Keyword.KEYWORD_BANTYPE) )
		{
			result = result.replace(Keyword.KEYWORD_BANTYPE, banType);
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