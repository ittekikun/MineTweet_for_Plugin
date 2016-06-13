package com.ittekikun.plugin.minetweet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ittekikun.plugin.itkcore.locale.MessageFileLoader;
import com.ittekikun.plugin.itkcore.utility.MessageUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import twitter4j.TwitterException;

import static com.ittekikun.plugin.itkcore.utility.MessageUtility.MessageType.INFO;
import static com.ittekikun.plugin.itkcore.utility.MessageUtility.MessageType.SEVERE;

public abstract class BaseCommand
{
    protected CommandSender sender;
    protected List<String> args = new ArrayList<String>();
    protected String name;
    protected int argLength = 0;
    protected String usage;
    protected boolean bePlayer = true;
    protected Player player;
    protected String command;
    protected MineTweet mineTweet;
    protected MineTweetConfig mineTweetConfig;
    protected TwitterManager twitterManager;
    protected Logger logger;
    protected MessageFileLoader messageFileLoader;

    public boolean run(final MineTweet mineTweet, final CommandSender sender, final String[] preArgs, final String cmd)
    {
        this.mineTweet = mineTweet;
        this.mineTweetConfig = mineTweet.mineTweetConfig;
        this.twitterManager = mineTweet.twitterManager;
        this.messageFileLoader = mineTweet.messageFileLoader;
        this.logger = mineTweet.getMineTweetLogger();

        this.sender = sender;
        this.command = cmd;

        // 引数をソート
        args.clear();
        for (String arg : preArgs)
        {
            args.add(arg);
        }

        // 引数からコマンドの部分を取り除く
        // (コマンド名に含まれる半角スペースをカウント、リストの先頭から順にループで取り除く)
        for (int i = 0; i < name.split(" ").length && i < args.size(); i++)
        {
            args.remove(0);
        }

        // 引数の長さチェック
        if (argLength > args.size())
        {
            sendUsage();
            return true;
        }

        // 実行にプレイヤーであることが必要かチェックする
        if (bePlayer && !(sender instanceof Player))
        {
            MessageUtility.messageToSender(sender, SEVERE, messageFileLoader.loadMessage("command.error.console"), MineTweet.prefix, logger);
            return true;
        }
        if (sender instanceof Player)
        {
            player = (Player)sender;
        }

        // 権限チェック
        if (!permission())
        {
            MessageUtility.messageToSender(sender, SEVERE, messageFileLoader.loadMessage("command.error.permission"), MineTweet.prefix, logger);
            return true;
        }

        // 実行
        try
        {
            execute();
        }
        catch (Exception ex)
        {
            Throwable error = ex;
            while (error instanceof CommandException)
            {
                error = error.getCause();
                error.printStackTrace();
            }
        }

        return true;
    }

    /**
     * コマンドを実際に実行する
     * @return 成功すればtrue それ以外はfalse
     * @throws CommandException CommandException
     */
    public abstract void execute() throws CommandException, TwitterException;

    /**
     * コマンド実行に必要な権限を持っているか検証する
     * @return trueなら権限あり、falseなら権限なし
     */
    public abstract boolean permission();

    /**
     * コマンドの使い方を送信する
     */
    public void sendUsage()
    {
        MessageUtility.messageToSender(sender, INFO, "&c/"+this.command + " "+ name + " " + usage, MineTweet.prefix, logger);
    }

    public String getName()
    {
        return this.name;
    }
}