package com.test;

import com.ittekikun.plugin.itkcore.locale.MessageFileLoader;
import com.ittekikun.plugin.itkcore.utility.VariousUtility;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TwitterManager
{
    private MineTweet mineTweet;
    private MineTweetConfig mineTweetConfig;
    private MessageFileLoader messageFileLoader;
    private Twitter twitter;
    private TwitterStream eewStream;
    private RequestToken requestToken;
    private APIKey apiKey;
    private Logger logger;

    private boolean canTweet = false;
    private boolean canAuth = false;

    //private boolean streamStatus = false;

    public TwitterManager(MineTweet mineTweet, APIKey apiKey)
    {
        this.mineTweet = mineTweet;
        this.apiKey = apiKey;
        this.logger = mineTweet.getMineTweetLogger();
        this.messageFileLoader = mineTweet.messageFileLoader;
    }

    public void startSetup()
    {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(apiKey.getIdol());
        builder.setOAuthConsumerSecret(apiKey.getMaster());
        Configuration conf = builder.build();

        AccessToken accessToken = null;

        twitter = new TwitterFactory(conf).getInstance();
        eewStream = new TwitterStreamFactory(conf).getInstance();

        accessToken = loadAccessToken();

        //初期起動時(ファイルなし)
        if(accessToken == null)
        {
            startSetupGuide();
        }
        //ファイル有り
        else
        {
            twitter.setOAuthAccessToken(accessToken);
            eewStream.setOAuthAccessToken(accessToken);

            //startRecieveStream();

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

        if(mineTweetConfig.addDate)
        {
            String time = VariousUtility.timeGetter(mineTweetConfig.dateformat);
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
            logger.severe(messageFileLoader.loadMessage("error.canttweet"));
            logger.severe(statusUpdate.getStatus());
            return;
        }
        if(!checkCharacters(statusUpdate.getStatus()))
        {
            logger.severe(messageFileLoader.loadMessage("error.文字数"));
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

    protected AccessToken loadAccessToken()
    {
        File f = createAccessTokenFileName();

        ObjectInputStream is = null;
        try
        {
            is = new ObjectInputStream(new FileInputStream(f));
            AccessToken accessToken = (AccessToken)is.readObject();
            return accessToken;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            if(is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected AccessToken getAccessToken(String pin) throws TwitterException
    {
        AccessToken accessToken = null;

        accessToken = twitter.getOAuthAccessToken(requestToken, pin);

        return accessToken;
    }

    protected void storeAccessToken(AccessToken accessToken)
    {
        //ファイル名の生成
        File f = createAccessTokenFileName();

        //親ディレクトリが存在しない場合，親ディレクトリを作る．
        File d = f.getParentFile();
        if (!d.exists())
        {
            d.mkdirs();
        }

        //ファイルへの書き込み
        ObjectOutputStream os = null;
        try
        {
            os = new ObjectOutputStream(new FileOutputStream(f));
            os.writeObject(accessToken);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (os != null)
            {
                try
                {
                    os.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private File createAccessTokenFileName()
    {
        String s = mineTweet.getDataFolder() + "/AccessToken.dat";
        return new File(s);
    }

    protected void startSetupGuide()
    {
        java.util.List<String> firstMes = new ArrayList<String>();

        firstMes.add("#################################################");
        firstMes.add("[[[[ Twitter連携ウィザード MineTweet by ittekikun ]]]]");
        firstMes.add("MineTweetのTwitter連携設定がされてません。");
        firstMes.add("下記URLから認証後、PINコードを /eew pin <pin> の様に打ち込み連携を完了して下さい。");
        //firstMes.add("※");
        try
        {
            firstMes.add("URL: " + VariousUtility.getShortUrl(createOAuthUrl().toString(), apiKey.getLove()));
            //firstMes.add("URL: " + (createOAuthUrl().toString()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            logger.severe("認証URLの生成に失敗しました。");
            return;
        }
        firstMes.add("#################################################");

        infoList(firstMes);
        canAuth = true;
    }

    private void infoList(java.util.List<String> list)
    {
        for(int i = 0; i < list.size(); ++i)
        {
            logger.info(list.get(i).toString());
        }
    }

//    public void startRecieveStream()
//    {
//        eewStream.addListener(new EEWStream(eewAlert));
//        eewStream.user();
//
//        streamStatus = true;
//
//        EEWAlert.log.info("ユーザーストリームに接続します。");
//
//        //214358709 = @eewbot
//        long[] list = {214358709L};
//        FilterQuery query = new FilterQuery(list);
//        eewStream.filter(query);
//    }
//
//    public void shutdownRecieveStream()
//    {
//        if(streamStatus)
//        {
//            eewStream.shutdown();
//            EEWAlert.log.info("ユーザーストリームから切断しました。");
//
//            streamStatus = false;
//        }
//    }
}