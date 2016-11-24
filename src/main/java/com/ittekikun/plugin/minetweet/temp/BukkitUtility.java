package com.ittekikun.plugin.minetweet.temp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

public class BukkitUtility
{
    /**
     * @return 接続中の全てのプレイヤー
     * @author https://github.com/ucchyocean/
     * 現在接続中のプレイヤーを全て取得する
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Player> getOnlinePlayers()
    {
        // CB179以前と、CB1710以降で戻り値が異なるため、
        // リフレクションを使って互換性を（無理やり）保つ。
        try
        {
            if (Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).getReturnType() == Collection.class)
            {
                Collection<?> temp = ((Collection<?>) Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                return new ArrayList<Player>((Collection<? extends Player>) temp);
            }
            else
            {
                Player[] temp = ((Player[])Bukkit.class.getMethod("getOnlinePlayers", new Class<?>[0]).invoke(null, new Object[0]));
                ArrayList<Player> players = new ArrayList<Player>();
                for (Player t : temp)
                {
                    players.add(t);
                }
                return players;
            }
        }
        catch (NoSuchMethodException ex)
        {
            // never happen
        }
        catch (InvocationTargetException ex)
        {
            // never happen
        }
        catch (IllegalAccessException ex)
        {
            // never happen
        }
        return new ArrayList<Player>();
    }

    private static Boolean isCB19orLaterCache;
    /**
     * 現在動作中のCraftBukkitが、v1.9 以上かどうかを確認する
     * @author https://github.com/ucchyocean/
     * @return v1.9以上ならtrue、そうでないならfalse
     */
    public static boolean isCB19orLater()
    {
        if(isCB19orLaterCache == null)
        {
            isCB19orLaterCache = isUpperVersion(Bukkit.getBukkitVersion(), "1.9");
        }
        return isCB19orLaterCache;
    }

    /**
     * 指定されたバージョンが、基準より新しいバージョンかどうかを確認する
     * @author https://github.com/ucchyocean/
     * @param version 確認するバージョン
     * @param border 基準のバージョン
     * @return 基準より確認対象の方が新しいバージョンかどうか<br/>
     * ただし、無効なバージョン番号（数値でないなど）が指定された場合はfalseに、
     * 2つのバージョンが完全一致した場合はtrueになる。
     */
    private static boolean isUpperVersion(String version, String border)
    {
        int hyphen = version.indexOf("-");
        if ( hyphen > 0 ) {
            version = version.substring(0, hyphen);
        }

        String[] versionArray = version.split("\\.");
        int[] versionNumbers = new int[versionArray.length];
        for ( int i=0; i<versionArray.length; i++ ) {
            if ( !versionArray[i].matches("[0-9]+") )
                return false;
            versionNumbers[i] = Integer.parseInt(versionArray[i]);
        }

        String[] borderArray = border.split("\\.");
        int[] borderNumbers = new int[borderArray.length];
        for ( int i=0; i<borderArray.length; i++ ) {
            if ( !borderArray[i].matches("[0-9]+") )
                return false;
            borderNumbers[i] = Integer.parseInt(borderArray[i]);
        }

        int index = 0;
        while ( (versionNumbers.length > index) && (borderNumbers.length > index) ) {
            if ( versionNumbers[index] > borderNumbers[index] ) {
                return true;
            } else if ( versionNumbers[index] < borderNumbers[index] ) {
                return false;
            }
            index++;
        }
        if ( borderNumbers.length == index ) {
            return true;
        } else {
            return false;
        }
    }
}