package com.ittekikun.plugin.minetweet;

import com.ittekikun.plugin.minetweet.listeners.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class MineTweet extends JavaPlugin
{
	private static MineTweet instance;
	public static Logger mtLogger;
	private  PluginManager pluginManager;
	private static boolean forceDisableMode = false;

	public MineTweetConfig mtConfig;
	public TwitterManager twitterManager;
	public BotManager botManager;

	protected static final String prefix = "[MineTweet] ";

	@Override
	public void onEnable()
	{
		instance = this;

		settingLogger();
		if(!(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.7))
		{
			//JAVA6以前の環境では動きません。
			mtLogger.severe("JAVA7以上がインストールされていません。");
			mtLogger.severe("プラグインを無効化します。");
			forceDisableMode = true;
			pluginManager.disablePlugin(instance);

			return;
		}
		settingConfig();
		registerListeners();
		settingBot();
		APIKey apiKey = loadAPIKeys();
		settingTwitter(apiKey);
	}

	@Override
	public void onDisable()
	{
		if(forceDisableMode)
		{
			return;
		}
	}

	private void settingLogger()
	{
		mtLogger = Logger.getLogger("minetweet");
		mtLogger.setFilter(new LogFilter(prefix));
	}

	private void settingConfig()
	{
		mtConfig = new MineTweetConfig(instance);
		mtLogger.info( "Configファイルを読み込みます。");
		mtConfig.loadConfig();
	}

	private void registerListeners()
	{
		//バージョンチェック
		if (mtConfig.versionCheck)
		{
			pluginManager.registerEvents(new VersionCheckListener(instance), instance);
			mtLogger.info( "バージョンチェックが有効になりました。");
		}

		//ログイン
		if (mtConfig.playerJoinTweet)
		{
			pluginManager.registerEvents(new JoinPlayerListener(instance), instance);
			mtLogger.info( "ログイン時ツイートが有効になりました。");
		}

		//ログアウト
		if (mtConfig.playerQuitTweet)
		{
			pluginManager.registerEvents(new QuitPlayerListener(instance), instance);
			mtLogger.info( "ログアウト時ツイートが有効になりました。");
		}

		if (mtConfig.achievementAwardedTweet)
		{
			pluginManager.registerEvents(new AchievementAwardedListener(instance), instance);
			mtLogger.info( "実績取得時ツイートが有効になりました。");
		}

		//MCBANSとのBAN連携
		if(mtConfig.mcBansBanTweet != 0)
		{
			if(mtConfig.mcBansBanTweet >= 1 && mtConfig.mcBansBanTweet <= 3)
			{
				if (pluginManager.isPluginEnabled("MCBans") )
				{
					pluginManager.registerEvents(new MCBansBANListener(instance), instance);
					mtLogger.info("MCBansと連携しました。(BAN)");
				}
				else
				{
					mtLogger.warning("MCBansが導入されてないので連携を無効化します。(BAN)");
				}
			}
			else
			{
				mtLogger.warning("MCBansとの連携設定が正しく設定されていません。(BAN)");
			}
		}

		//MCBansとのKICK連携
		if(mtConfig.mcBansKickTweet)
		{
			if (pluginManager.isPluginEnabled("MCBans") )
			{
				pluginManager.registerEvents(new MCBansKICKListener(instance), instance);
				mtLogger.info( "MCBansと連携しました。(KICK)");
			}
			else
			{
				mtLogger.warning("MCBansが導入されてないので連携を無効化します。(KICK)");
			}
		}

		if (mtConfig.votifierReceiveTweet)
		{
			if (pluginManager.isPluginEnabled("Votifier"))
			{
				pluginManager.registerEvents(new VotifierReceiveListener(instance), instance);
				mtLogger.info( "Votifierと連携しました。");
			}
			else
			{
				mtLogger.warning("Votifierが導入されてないので連携を無効化します。");
			}
		}
	}

	private void settingBot()
	{
		if(mtConfig.useBot)
		{
			botManager = new BotManager(instance);
			mtLogger.info( "BOT機能を有効にします。");
			botManager.botSetup();
		}
	}

	private void settingTwitter(APIKey apiKey)
	{
		twitterManager = new TwitterManager(instance, apiKey);
		mtLogger.info( "Twitter関連の読み込みを行います。");
		twitterManager.startSetup();
	}

	private APIKey loadAPIKeys()
	{
		try
		{
			return (APIKey)VariousUtility.decodeObjectfromJar(getPluginJarFile(), "dmas", "gFcdrbLJAzNTjsLE");
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


	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("minetweet") || cmd.getName().equalsIgnoreCase("mt"))
		{
			if(args.length == 0)
			{
				Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "引数がありません。");
				help(sender);
				return true;
			}

			if (args[0].equalsIgnoreCase("pin"))
			{
				if(checkPermission(sender, "minetweet.pin"))
				{
					if(twitterManager.canAuth)
					{
						if(args.length == 2)
						{
							if(VariousUtility.checkIntParse(args[1]))
							{
								if(args[1].length() == 7)
								{
									AccessToken accessToken;
									try
									{
										accessToken = twitterManager.getAccessToken(args[1]);

										twitterManager.storeAccessToken(accessToken);
										Messenger.messageToSender(sender, Messenger.MessageType.INFO, "Twitterと正しく認証されました。");
										twitterManager.startSetup();
									}
									catch (TwitterException e)
									{
										e.printStackTrace();
										Messenger.messageToSender(sender, Messenger.MessageType.SEVERE, "正しく認証されませんでした。(PINコードが使えなかった。もしくは無効になっている。)");
										Messenger.messageToSender(sender, Messenger.MessageType.SEVERE, "お手数ですがもう一度お試し下さい。");
									}
									return true;
								}
								else
								{
									Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "PINコードが正しく入力されていません。(正しいPINコードの桁数は7です。)");
									return true;
								}
							}
							else
							{
								Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "PINコードが正しく入力されていません。(整数値に変換できません。)");
								return true;
							}
						}
						else
						{
							Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "PINコードが正しく入力されていません。(コマンド構文が間違ってます。)");
							return true;
						}
					}
					else
					{
						Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "このコマンドは現在実行できません。(認証時のみ使用)");
						return true;
					}
				}
				else
				{
					Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "そのコマンドを実行する権限がありません。");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("tweet"))
			{
				if(checkPermission(sender, "minetweet.tweet"))
				{
					if(twitterManager.canTweet)
					{
						if(args.length >= 2)
						{
							try
							{
								twitterManager.twitter.updateStatus(VariousUtility.joinArray(args, 1));
								Messenger.messageToSender(sender, Messenger.MessageType.INFO, "ツイートに成功しました。");
								return true;
							}
							catch (TwitterException e)
							{
								e.printStackTrace();
								Messenger.messageToSender(sender, Messenger.MessageType.SEVERE, "何らかの理由でツイートに失敗しました。");
								return true;
							}
						}
						else
						{
							Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "ツイートする文章が含まれていません。");
						}
					}
					else
					{
						Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "このコマンドは現在実行できません。(未認証)");
					}
				}
				else
				{
					Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "そのコマンドを実行する権限がありません。");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("reload"))
			{
				if(checkPermission(sender, "minetweet.reload"))
				{
					HandlerList.unregisterAll(instance);
					botManager.taskCancel();

					mtConfig.loadConfig();
					if(mtConfig.useBot)
					{
						botManager.botSetup();
					}
					registerListeners();
					Messenger.messageToSender(sender, Messenger.MessageType.INFO, "Configファイルの再読込を行いました。");

					return true;
				}
				else
				{
					Messenger.messageToSender(sender, Messenger.MessageType.WARNING, "そのコマンドを実行する権限がありません。");
					return true;
				}
			}
			else if(args[0].equalsIgnoreCase("help"))
			{
				help(sender);
				return true;
			}
			else if((args[0].equalsIgnoreCase("test")))
			{

				return true;
			}
			else
			{
				help(sender);
				return true;
			}
		}
		return false;
	}

	public void help(CommandSender sender)
	{
		List<String> firstMes = new ArrayList<String>();

		firstMes.add("---------------ヘルプコマンド---------------");
		firstMes.add("現在使えるコマンドは以下の通りです。");
		firstMes.add("mt <=> minetweet");
		firstMes.add("/mt pin <pin>    ※認証時のみ使用します。");
		firstMes.add("/mt reload       ※設定ファイルを再読み込みします。");
		firstMes.add("/mt tweet <text> ※textの内容をツイートします。");
		firstMes.add("/mt help         ※helpを表示します。");

		for(int i = 0; i < firstMes.size(); ++i)
		{
			Messenger.messageToSender(sender, Messenger.MessageType.INFO, firstMes.get(i).toString());
		}
	}

	public boolean checkPermission(CommandSender sender, String permission)
	{
		if(sender instanceof Player)
		{
			Player player = (Player)sender;
			if(player.hasPermission(permission) || player.isOp())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		//コンソールは管理者扱い
		else
		{
			return true;
		}
	}


	protected static File getPluginJarFile()
	{
		return getInstance().getFile();
	}

	public static MineTweet getInstance()
	{
		return instance;
	}

	class LogFilter implements Filter
	{
		private String prefix;

		public LogFilter(String prefix)
		{
			this.prefix = prefix;
		}

		public boolean isLoggable(LogRecord logRecord)
		{
			logRecord.setMessage(prefix + logRecord.getMessage());
			return true;
		}
	}
}