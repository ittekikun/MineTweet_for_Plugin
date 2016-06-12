package com.ittekikun.plugin.minetweet.twitter;

import com.ittekikun.plugin.itkcore.utility.VariousUtility;
import com.ittekikun.plugin.minetweet.MineTweetConfig;
import com.ittekikun.plugin.minetweet.gui.Swing.CertifyGui_Swing;
import com.ittekikun.plugin.minetweet.MineTweet;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

public class TwitterManager
{
    public MineTweet plugin;
    public Twitter twitter;
	public TwitterStream userStream;
	public MineTweetConfig mtConfig;
	public AccessToken accesstoken;

	public static Boolean status;

	public TwitterManager(MineTweet plugin)
	{
		this.plugin = plugin;
		this.mtConfig = plugin.mtConfig;
	}

	public void startSetup()
	{
		if(mtConfig.GUICertify)
		{
			ConfigurationBuilder builder = new ConfigurationBuilder();

			builder.setOAuthConsumerKey(plugin.apiKey.getIdol());
			builder.setOAuthConsumerSecret(plugin.apiKey.getMaster());
			Configuration conf = builder.build();

			twitter = new TwitterFactory(conf).getInstance();
			userStream = new TwitterStreamFactory(conf).getInstance();

			accesstoken = loadAccessToken();

			//初期起動時(ファイルなし)
			if(accesstoken == null)
			{
				status = false;

				plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						final CertifyGui_Swing certifyGui_swing = new CertifyGui_Swing(plugin);
						certifyGui_swing.openGui();

						//詰んだ
//						final CertifyGui_FX certifyGui_FX = new CertifyGui_FX(plugin);
//						certifyGui_FX.openGui();
					}
				});
			}
			//ファイル有り
			else
			{
				status = true;

				twitter.setOAuthAccessToken(accesstoken);
				userStream.setOAuthAccessToken(accesstoken);
			}
		}
		//GUI未使用時
		else
		{
			status = true;

			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(mtConfig.consumerKey);
			builder.setOAuthConsumerSecret(mtConfig.consumerSecret);
			builder.setOAuthAccessToken(mtConfig.accessToken);
			builder.setOAuthAccessTokenSecret(mtConfig.accessTokenSecret);
			Configuration conf = builder.build();

			twitter = new TwitterFactory(conf).getInstance();
			//userStream = new TwitterStreamFactory(conf).getInstance();
		}

//		if((mtConfig.noticeEew || mtConfig.receiveStream) && status)
//		{
//			startRecieveStream();
//		}
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
		//// TODO: 2016/05/30
		if(!status)
		{
			MineTweet.log.severe("現在ツイートができない状況の為、下記のツイートは行われませんでした。");
			MineTweet.log.severe(statusUpdate.getStatus());
			return;
		}
		if(!checkCharacters(statusUpdate.getStatus()))
		{
			MineTweet.log.severe("文字数がオーバーしている為、下記のツイートは行われませんでした。");
			MineTweet.log.severe(statusUpdate.getStatus());
			return;
		}

		twitter.updateStatus(statusUpdate);
	}

    public RequestToken openOAuthPage()
    {
        RequestToken requestToken = null;
        Desktop desktop = Desktop.getDesktop();

        try
        {
            requestToken = twitter.getOAuthRequestToken();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            URI uri = new URI(requestToken.getAuthorizationURL());
            desktop.browse(uri);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return requestToken;
    }

	public AccessToken loadAccessToken()
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
			if(is != null){
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

    public AccessToken getAccessToken(RequestToken  requesttoken,String pin) throws TwitterException
    {
        AccessToken accessToken = null;

        accessToken = twitter.getOAuthAccessToken(requesttoken, pin);

        return accessToken;
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

    public File createAccessTokenFileName()
    {
        String s = plugin.getDataFolder() + "/AccessToken.yml";
        return new File(s);
    }

//	public void startRecieveStream()
//	{
//		userStream.addListener(new UserStream(mtConfig));
//
//		userStream.user();
//
//		//214358709 = @eewbot
//		//long[] list = {214358709L};
//		//FilterQuery query = new FilterQuery(list);
//		//userStream.filter(query);
//	}
//
//	public void shutdownRecieveStream()
//	{
//		userStream.shutdown();
//	}
}