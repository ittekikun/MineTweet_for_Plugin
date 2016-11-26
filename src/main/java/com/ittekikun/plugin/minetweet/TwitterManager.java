package com.ittekikun.plugin.minetweet;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TwitterManager
{
    private MineTweet mineTweet;
    private MineTweetConfig mtConfig;
    public Twitter twitter;
    private RequestToken requestToken;
    private APIKey apiKey;
    private Logger logger;

    public boolean canTweet = false;
    public boolean canAuth = false;

    public TwitterManager(MineTweet mineTweet, APIKey apiKey)
    {
        this.mineTweet = mineTweet;
        this.mtConfig = mineTweet.mtConfig;
        this.apiKey = apiKey;
        this.logger = MineTweet.mtLogger;
    }

    public void startSetup()
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(apiKey.getAnya());
        builder.setOAuthConsumerSecret(apiKey.getMiku());
        Configuration conf = builder.build();

        twitter = new TwitterFactory(conf).getInstance();

        AccessToken accessToken = loadAccessToken();

        //初期起動時(ファイルなし)
        if(accessToken == null)
        {
            startSetupGuide();
        }
        //ファイル有り
        else
        {
            twitter.setOAuthAccessToken(accessToken);

            canTweet = true;
        }
    }

    public void tweet(String tweet) throws TwitterException
    {
        StatusUpdate statusUpdate = makeUpdate(tweet);

        updateStatus(statusUpdate);
    }

    public void tweet(String tweet, File media) throws TwitterException
    {
        StatusUpdate statusUpdate = makeUpdate(tweet);
        statusUpdate.media(media);

        updateStatus(statusUpdate);
    }

    private StatusUpdate makeUpdate(String tweet)
    {
        StatusUpdate statusUpdate;

        if(mtConfig.addDate)
        {
            String time = VariousUtility.timeGetter(mtConfig.dateformat);
            statusUpdate = new StatusUpdate(tweet + "\n" + time);
        }
        else
        {
            statusUpdate = new StatusUpdate(tweet);
        }
        return statusUpdate;
    }

    private boolean checkCharacters(String tweet)
    {
        if(tweet.length() <= 140)
        {
            return true;
        }
        return false;
    }

    private void updateStatus(StatusUpdate statusUpdate) throws TwitterException
    {
        if(!canTweet)
        {
            logger.severe("現在ツイートできる状態でない為、下記ツイートは行われませんでした。");
            logger.severe(statusUpdate.getStatus());
            return;
        }
        if(!checkCharacters(statusUpdate.getStatus()))
        {
            logger.severe("文字数制限をオーバーしている為、下記ツイートは行われませんでした。");
            logger.severe(statusUpdate.getStatus());
            return;
        }

        twitter.updateStatus(statusUpdate);
    }

    protected URL createOAuthUrl()
    {
        URL url = null;

        try
        {
            requestToken = twitter.getOAuthRequestToken();
            url = new URL(requestToken.getAuthorizationURL());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return url;
    }



    public AccessToken getAccessToken(String pin) throws TwitterException
    {
        AccessToken accessToken =  twitter.getOAuthAccessToken(requestToken, pin);

        return accessToken;
    }

    public AccessToken loadAccessToken()
    {
        File f = createAccessTokenFileName();

        if(!f.exists())
        {
            return null;
        }

        try
        {
            return (AccessToken)VariousUtility.encryptionDecodeObject(f, "xiHEW7YShBpcruPy");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void storeAccessToken(AccessToken accessToken)
    {
        //ファイル名の生成
        File f = createAccessTokenFileName();

        //親ディレクトリが存在しない場合，親ディレクトリを作る．
        File d = f.getParentFile();
        if (!d.exists())
        {
            d.mkdirs();
        }

        try
        {
            VariousUtility.encryptionEncodeObject(f, accessToken, "xiHEW7YShBpcruPy");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (BadPaddingException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (InvalidKeyException e)
        {
            e.printStackTrace();
        }
    }

    private File createAccessTokenFileName()
    {
        String s = mineTweet.getDataFolder() + "/AccessToken.mtdf";
        return new File(s);
    }

    protected void startSetupGuide()
    {
        List<String> firstMes = new ArrayList<String>();

        firstMes.add("#################################################");
        firstMes.add("[[[[ Twitter連携ウィザード MineTweet by ittekikun ]]]]");
        firstMes.add("MineTweetのTwitter連携設定がされてません。");
        firstMes.add("下記URLから認証後、PINコードを /minetweet pin <pin> の様に打ち込み連携を完了して下さい。");
        //firstMes.add("※");
        try
        {
            firstMes.add("URL: " + VariousUtility.getShortUrl(createOAuthUrl().toString(), apiKey.getAsuka()));
            //firstMes.add("URL: " + (createOAuthUrl().toString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            logger.severe("認証URLの生成に失敗しました。");
            return;
        }
        firstMes.add("#################################################");

        infoFromList(firstMes);
        canAuth = true;
    }

    private void infoFromList(java.util.List<String> list)
    {
        for(int i = 0; i < list.size(); ++i)
        {
            logger.info(list.get(i).toString());
        }
    }
}