package com.ittekikun.plugin.minetweet;

import com.ittekikun.plugin.itkcore.locale.MessageFileLoader;
import com.ittekikun.plugin.itkcore.logger.LogFilter;
import com.ittekikun.plugin.itkcore.utility.VariousUtility;
import com.ittekikun.plugin.minetweet.api.MineTweetAPI;
import com.ittekikun.plugin.minetweet.command.BaseCommand;
import com.ittekikun.plugin.minetweet.command.ConfigReloadCommand;
import com.ittekikun.plugin.minetweet.command.HelpCommand;
import com.ittekikun.plugin.minetweet.command.TweetCommand;
import com.ittekikun.plugin.minetweet.listener.RegistrationListener;
import com.ittekikun.plugin.minetweet.twitter.BotManager;
import com.ittekikun.plugin.minetweet.twitter.TwitterManager;
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
	public static MineTweet instance;
    public static Logger log;
	public static final String prefix = "[MineTweet_for_Plugin] ";
	public static PluginManager pluginManager;
	public static boolean forceDisableMode;
	public MineTweetConfig mtConfig;
	public APIKey apiKey;
	public MessageFileLoader messageLoader;
	public TwitterManager twitterManager;
	public BotManager botManager;

	private List<BaseCommand> commands = new ArrayList<BaseCommand>();

    @Override
    public void onEnable()
    {
		instance = this;
		pluginManager = instance.getServer().getPluginManager();

	    log = Logger.getLogger("minetweet");
	    log.setFilter(new LogFilter(prefix));

		mtConfig = new MineTweetConfig(this);
		mtConfig.loadConfig();

		messageLoader = new MessageFileLoader(instance.getDataFolder(), instance.getPluginJarFile(), "languages", "messages", mtConfig.messageLanguage);
		messageLoader.saveMessages();

		if(!(Double.parseDouble(System.getProperty("java.specification.version")) >= 1.7))
		{
			//JAVA6以前の環境では動きません
			log.severe(messageLoader.loadMessage("system.load.error.java"));
			log.severe(messageLoader.loadMessage("system.load.error.disable"));
			forceDisableMode = true;
			pluginManager.disablePlugin(this);

			return;
		}

		apiKey = getApiKey();
		twitterManager = new TwitterManager(this);
	    twitterManager.startSetup();

	    botManager = new BotManager(this);
	    botManager.botSetup();

	    registerCommands();
	    RegistrationListener.registrationListener(instance);

		serverStartTweet();

		log.info(messageLoader.loadMessage("language.name") + " " + messageLoader.loadMessage("system.load.language"));
		log.info(messageLoader.loadMessage("system.load.complete"));
    }

	public APIKey getApiKey()
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

	@Override
	public void onDisable()
	{
		if(forceDisableMode)
		{
			return;
		}

		serverStopTweet();
	}

	private void serverStartTweet()
	{
		if (mtConfig.serverStartTweet)
		{
			try
			{
				twitterManager.tweet(mtConfig.start_message_temp);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void serverStopTweet()
	{
		if (mtConfig.serverStopTweet)
		{
			try
			{
				twitterManager.tweet(mtConfig.stop_message_temp);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void registerCommands()
	{
		commands.add(new HelpCommand());
		commands.add(new TweetCommand());
		commands.add(new ConfigReloadCommand());
	}

	//使わなくない？
	public ClassLoader getPluginClassLoader()
	{
		return this.getClassLoader();
	}

	public File getPluginJarFile()
	{
		return this.getFile();
	}

	public List<BaseCommand> getCommands()
	{
		return commands;
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
//		詰んだ
//		else if(cmd.getName().equalsIgnoreCase("tw") || cmd.getName().equalsIgnoreCase("tweet"))
//		{
//			new TweetCommand().run(this, sender, args, commandLabel);
//			return true;
//		}
		return false;
	}
}