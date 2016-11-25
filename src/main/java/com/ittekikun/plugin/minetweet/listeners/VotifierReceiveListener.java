package com.ittekikun.plugin.minetweet.listeners;

import com.ittekikun.plugin.minetweet.*;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import twitter4j.TwitterException;

public class VotifierReceiveListener implements Listener
{
	MineTweet plugin;
	MineTweetConfig mtConfig;
	TwitterManager twitterManager;

	public VotifierReceiveListener(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
		this.twitterManager = plugin.twitterManager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVotifier(VotifierEvent event) throws TwitterException
	{
		Vote vote = event.getVote();

		String name = vote.getUsername();
		String service = vote.getServiceName();

		String message = replaceKeywords(mtConfig.votifier_message_temp, name, service);

		twitterManager.tweet(message);
	}

	private String replaceKeywords(String source, String name, String service)
	{
		String result = source;
		if (result.contains(Keyword.KEYWORD_PLAYER) )
		{
			result = result.replace(Keyword.KEYWORD_PLAYER, name);
		}
		if (result.contains(Keyword.KEYWORD_SERVICE) )
		{
			result = result.replace(Keyword.KEYWORD_SERVICE, service);
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