package com.test;

import com.ittekikun.plugin.itkcore.utility.MessageUtility;

import static com.ittekikun.plugin.itkcore.utility.MessageUtility.MessageType.INFO;

public class HelpCommand extends BaseCommand
{
    public HelpCommand()
    {
        bePlayer = false;
        name = "help";
        argLength = 0;
        usage = "<- show command help";
    }

    @Override
    public void execute()
    {
        MessageUtility.messageToSender(sender, INFO, "&c===================================", mineTweet.prefix, logger);
        MessageUtility.messageToSender(sender, INFO, "&b" + mineTweet.getDescription().getName() + " Plugin version &3"+ mineTweet.getDescription().getVersion()+" &bby ittekikun", mineTweet.prefix, logger);
        MessageUtility.messageToSender(sender, INFO, " &b<>&f = required, &b[]&f = optional", mineTweet.prefix, logger);
        // 全コマンドをループで表示
        for (BaseCommand cmd : mineTweet.getCommands().toArray(new BaseCommand[0]))
        {
            cmd.sender = this.sender;
            if (cmd.permission())
            {
                MessageUtility.messageToSender(sender, INFO ,"&8-&7 /"+command+" &c" + cmd.name + " &7" + cmd.usage, mineTweet.prefix, logger);
            }
        }
        MessageUtility.messageToSender(sender, INFO, "&c===================================", mineTweet.prefix, logger);
        return;
    }

    @Override
    public boolean permission()
    {
        return true;
    }
}