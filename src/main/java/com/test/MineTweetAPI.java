package com.test;

import twitter4j.TwitterException;

public class MineTweetAPI
{
    private TwitterManager twitterManager;

    public MineTweetAPI(TwitterManager twitterManager)
    {
        this.twitterManager = twitterManager;
    }

    public void tweet(String tweet) throws TwitterException
    {
        twitterManager.tweet(tweet);
    }
}
