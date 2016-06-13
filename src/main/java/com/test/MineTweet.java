package com.test;

import com.ittekikun.plugin.itkcore.locale.MessageFileLoader;
import com.ittekikun.plugin.itkcore.logger.LogFilter;
import com.ittekikun.plugin.itkcore.utility.VariousUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class MineTweet extends JavaPlugin
{
	private static MineTweet instance;
	private static Logger mineTweetLogger;
	private static PluginManager pluginManager;
	private static boolean forceDisableMode;

	private APIKey apiKey;

	protected MineTweetConfig mineTweetConfig;
	protected TwitterManager twitterManager;
	protected MessageFileLoader messageFileLoader;
	protected BotManager botManager;

	private MineTweetAPI mineTweetAPI;

	protected static final String prefix = "[MineTweet_for_Plugin] ";

	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

    @Override
    public void onEnable()
    {
		instance = this;
		pluginManager = instance.getServer().getPluginManager();

		mineTweetLogger = Logger.getLogger("minetweet");
		mineTweetLogger.setFilter(new LogFilter(prefix));

		mineTweetConfig = new MineTweetConfig(instance);
		mineTweetConfig.loadConfig();

		messageFileLoader = new MessageFileLoader(instance.getDataFolder(), instance.getPluginJarFile(), "languages", "messages", mineTweetConfig.messageLanguage);

		if(!(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.7))
		{
			//JAVA6以前の環境では動きません
			mineTweetLogger.severe(messageFileLoader.loadMessage("system.load.error.java"));
			mineTweetLogger.severe(messageFileLoader.loadMessage("system.load.error.disable"));
			forceDisableMode = true;
			pluginManager.disablePlugin(this);

			return;
		}

		apiKey = decodeAPIKey();

		twitterManager = new TwitterManager(instance, apiKey);
		twitterManager.startSetup();

		botManager = new BotManager(this);
		botManager.botSetup();

		mineTweetAPI = new MineTweetAPI(twitterManager);

		mineTweetLogger.info(messageFileLoader.loadMessage("language.name") + " " + messageFileLoader.loadMessage("system.load.language"));
		mineTweetLogger.info(messageFileLoader.loadMessage("system.load.complete"));
    }

	private APIKey decodeAPIKey()
	{
		try
		{
			APIKey apiKey = (APIKey)VariousUtility.decodeObject(getPluginJarFile(), "yuzu");
			return apiKey;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private void serverStartTweet()
	{
		if (mineTweetConfig.serverStartTweet)
		{
			try
			{
				twitterManager.tweet(mineTweetConfig.start_message_temp);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDisable()
	{
		if(forceDisableMode)
		{
			return;
		}

		serverStopTweet();
	}

	private void serverStopTweet()
	{
		if (mineTweetConfig.serverStopTweet)
		{
			try
			{
				twitterManager.tweet(mineTweetConfig.stop_message_temp);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
	{
		if (cmd.getName().equalsIgnoreCase("mt") || cmd.getName().equalsIgnoreCase("minetweet"))
		{
			if(args.length == 0)
			{
				// 引数ゼロはヘルプ表示
				args = new String[]{"help"};
			}

			outer:
			for (BaseCommand command : commands.toArray(new BaseCommand[0]))
			{
				String[] cmds = command.getName().split(" ");
				for (int i = 0; i < cmds.length; i++){
					if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])){
						continue outer;
					}
					// 実行
					return command.run(this, sender, args, commandLabel);
				}
			}
			// 有効コマンドなし ヘルプ表示
			new HelpCommand().run(this, sender, args, commandLabel);
			return true;
		}

		return false;
	}

	private void registerCommands()
	{
		commands.add(new HelpCommand());
		//commands.add(new TweetCommand());
		//commands.add(new ConfigReloadCommand());
	}

	public List<BaseCommand> getCommands()
	{
		return commands;
	}

	public static MineTweet getInstance()
	{
		return instance;
	}

	protected static File getPluginJarFile()
	{
		return getInstance().getFile();
	}

	protected static Logger getMineTweetLogger()
	{
		return mineTweetLogger;
	}

	public MineTweetAPI getMineTweetAPI()
	{
		return mineTweetAPI;
	}
}