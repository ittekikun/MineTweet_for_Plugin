package com.ittekikun.plugin.minetweet.temp;

import java.util.List;

public class MineTweetConfig
{
    private MineTweet mineTweet;

    private ConfigAccessor system;
    private ConfigAccessor twitter;
    private ConfigAccessor bot;

    private ConfigAccessor achievement;

    //system
    protected String messageLanguage;

    protected Boolean versionCheck;

    protected Boolean GUICertify;

    protected String consumerKey;
    protected String consumerSecret;
    protected String accessToken;
    protected String accessTokenSecret;

    //twitter
    public Boolean addDate;
    public String dateformat;

    public String cmd_message_temp;

    public Boolean serverStartTweet;
    public Boolean serverStopTweet;
    public String start_message_temp;
    public String stop_message_temp;

    public Boolean playerJoinTweet;
    public Boolean playerQuitTweet;
    public String join_message_temp;
    public String quit_message_temp;
    public Boolean tweetWithImage;

    public Boolean achievementAwardedTweet;
    public String achievement_message_temp;
    //public String achievement_message_name;

    public Boolean playerDeathTweet;
    public Boolean playerDeathByPlayerTweet;
    public String player_death_by_player_message_temp;

    public int mcBansBanTweet;
    public Boolean mcBansKickTweet;
    public String bantype_gban;
    public String bantype_lban;

    public String kick_message_temp;
    public String ban_message_temp;

    public Boolean votifierReceiveTweet;
    public String votifier_message_temp;

    public Boolean lunaChatTweet;
    public String channel_create_message_temp;
    public String channel_delete_message_temp;

    //BOT
    public Boolean useBot;
    public List<String> botMessageList;
    public int tweetCycle;

    public MineTweetConfig(MineTweet mineTweet)
    {
        this.mineTweet = mineTweet;
    }

    public void loadConfig()
    {
        system = new ConfigAccessor(mineTweet, mineTweet.getPluginJarFile(), "main.yml");
        twitter = new ConfigAccessor(mineTweet, mineTweet.getPluginJarFile(), "twitter.yml");
        bot = new ConfigAccessor(mineTweet, mineTweet.getPluginJarFile(), "bot.yml");

        achievement = new ConfigAccessor(mineTweet, mineTweet.getPluginJarFile(), "achievement.yml");

        system.saveDefaultConfig();
        twitter.saveDefaultConfig();
        bot.saveDefaultConfig();

        achievement.saveDefaultConfig();

        //system
        this.messageLanguage = system.getConfig().getString("Language", "ja");

        this.GUICertify = system.getConfig().getBoolean("GUICertify", true);
        this.consumerKey = system.getConfig().getString("consumerKey", "xxxxxxxxxx");
        this.consumerSecret = system.getConfig().getString("consumerSecret", "xxxxxxxxxx");
        this.accessToken = system.getConfig().getString("accessToken", "xxxxxxxxxx");
        this.accessTokenSecret = system.getConfig().getString("accessTokenSecret", "xxxxxxxxxx");

        this.versionCheck = system.getConfig().getBoolean("VersionCheck", true);

        //twitter
        this.addDate = twitter.getConfig().getBoolean("AddDate", true);
        this.dateformat = twitter.getConfig().getString("DateFormat", "EEE MMM d HH:mm:ss z");

        this.cmd_message_temp = twitter.getConfig().getString("CommandTweetTemplate", "(サーバーから$userが投稿) $message");

        this.serverStartTweet = twitter.getConfig().getBoolean("ServerStartTweet", false);
        this.start_message_temp = twitter.getConfig().getString("ServerStartTemplate", "サーバーを起動しました。【自動投稿】");
        this.serverStopTweet = twitter.getConfig().getBoolean("ServerStopTweet", false);
        this.stop_message_temp = twitter.getConfig().getString("ServerStopTemplate", "サーバーが停止しました。【自動投稿】");

        this.playerJoinTweet = twitter.getConfig().getBoolean("PlayerJoinTweet", true);
        this.join_message_temp = twitter.getConfig().getString("JoinMessageTemplate", "$userさんがサーバーにログインしました。現在$number人がログインしています。【自動投稿】");
        this.playerQuitTweet = twitter.getConfig().getBoolean("PlayerQuitTweet", true);
        this.quit_message_temp = twitter.getConfig().getString("QuitMessageTemplate", "$userさんがサーバーからログアウトしました。現在$number人がログインしています。【自動投稿】");
        this.tweetWithImage = twitter.getConfig().getBoolean("TweetWithImage", false);

        this.achievementAwardedTweet = twitter.getConfig().getBoolean("AchievementAwardedTweet", false);
        this.achievement_message_temp = twitter.getConfig().getString("AchievementAwardedTemplate", "$userさんが、「$achievement」という実績を取得しました。【自動投稿】");

        this.playerDeathTweet = twitter.getConfig().getBoolean("PlayerDeathTweet", false);
        this.playerDeathByPlayerTweet = twitter.getConfig().getBoolean("PlayerDeathByPlayerTweet", false);
        this.player_death_by_player_message_temp = twitter.getConfig().getString("PlayerDeathByPlayerTemplate", "$deaderさんが$killerさんによってキルされました。(武器: $item)");

        this.mcBansBanTweet = twitter.getConfig().getInt("MCBansBANTweet", 0);
        this.ban_message_temp = twitter.getConfig().getString("BanMessageTemplate", "$userさんが、「$reason」という理由で、$senderによって$bantypeされました。【自動投稿】");
        this.bantype_gban = twitter.getConfig().getString("GlobalBAN", "クローバルBAN");
        this.bantype_lban = twitter.getConfig().getString("LocalBAN", "ローカルBAN");

        this.mcBansKickTweet = twitter.getConfig().getBoolean("MCBansKICKTweet", false);
        this.kick_message_temp = twitter.getConfig().getString("KickMessageTemplate", "$userさんが、「$reason」という理由で、$senderによってKICKされました。【自動投稿】");

        this.votifierReceiveTweet = twitter.getConfig().getBoolean("VotifierReceiveTweet");
        this.votifier_message_temp = twitter.getConfig().getString("VotifierReceiveTemplate", "$userさんが、「$service」で当鯖に投票しました。【自動投稿】");

        this.lunaChatTweet = twitter.getConfig().getBoolean("LunaChatTweet");
        this.channel_create_message_temp = twitter.getConfig().getString("ChannelCreateTemplate", "チャットチャンネル「$channel」が作成されました。【自動投稿】");
        this.channel_delete_message_temp = twitter.getConfig().getString("ChannelDeleteTemplate", "チャットチャンネル「$channel」が削除されました。【自動投稿】");

        //BOT
        this.useBot = bot.getConfig().getBoolean("UseBot");
        this.botMessageList = bot.getConfig().getStringList("BotMessageList");
        this.tweetCycle = bot.getConfig().getInt("TweetCycle");
    }

    public String loadAchievementName(String source)
    {
        String name = achievement.getConfig().getString(source, source);
        return name;
    }
}