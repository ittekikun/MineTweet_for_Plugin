package com.ittekikun.plugin.minetweet.listeners;
import com.ittekikun.plugin.minetweet.Messenger;
import com.ittekikun.plugin.minetweet.MineTweet;
import com.ittekikun.plugin.minetweet.MineTweetConfig;
import com.ittekikun.plugin.minetweet.VariousUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.List;

public class VersionCheckListener implements Listener
{
    private MineTweet plugin;
    private MineTweetConfig mtConfig;

    public VersionCheckListener(MineTweet plugin)
    {
        this.plugin = plugin;
        this.mtConfig = plugin.mtConfig;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        if(player.isOp() || player.hasPermission("minetweet.vcheck"))
        {
            double nowVer = Double.valueOf(plugin.getServer().getPluginManager().getPlugin("MineTweet").getDescription().getVersion());

            String url = "MineTweet_for_plugin";

            Thread updateCheck = new Thread(new UpdateCheck(player ,url ,nowVer));
            updateCheck.start();
        }
    }

    public class UpdateCheck implements Runnable
    {
        private Player player;
        private String nurl;
        private double nowVer;

        String temp = "§e$Name§fの新しいバージョン§f(§6New=$LastVer§f, §cNow=$NowVer§f)が利用できます。§a$Reason";

        public UpdateCheck(Player player, String url, double nowVer)
        {
            this.player = player;
            this.nowVer = nowVer;

            this.nurl = "http://verche.ittekikun.com/" + url + "/lastver.txt";
        }

        public void run()
        {
            List<String> text = null;
            try
            {
                text = VariousUtility.getHttpServerText(nurl);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            String name = text.get(0);

            double ver = Double.valueOf(text.get(1));

            String note = text.get(2);

            if(ver > nowVer)
            {
                String message = replaceKeywords(temp, ver, nowVer, name, note);
                Messenger.messageToSender(player, Messenger.MessageType.INFO, message);
            }
        }

        private String replaceKeywords(String source,Double LastVer, Double NowVer, String name, String reason)
        {
            String result = source;
            if ( result.contains("$LastVer") )
            {
                result = result.replace("$LastVer", LastVer.toString());
            }
            if ( result.contains("$NowVer") )
            {
                result = result.replace("$NowVer", NowVer.toString());
            }
            if ( result.contains("$Name") )
            {
                result = result.replace("$Name", name);
            }
            if ( result.contains("$Reason") )
            {
                result = result.replace("$Reason", reason);
            }
            return result;
        }
    }
}