package Loup_Garou_1_15_2;

import java.util.Random;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Tools
{
    private static Random random = new Random();
    public static Main plugin = null;

    public static String ParseColor(String s)
    {
        return ChatColor.translateAlternateColorCodes('§', s);
    }

    public static int randomNumber()
    {
        return random.nextInt();
    }

    public static int randomNumber(int max)
    {
        return random.nextInt(max);
    }

    public static int randomNumber(int min, int max)
    {
        return random.nextInt((max - min) + 1) + min;
    }

    public static String getConfigString(String path)
    {
        String message =
            plugin.getConfig()
                .getString(path);
        return ParseColor(message);
    }

    public static String getConfigString(String path, String player)
    {
        String message =
            plugin.getConfig()
                .getString(path)
                .replace("<player>", player);
        return ParseColor(message);
    }

    public static String getConfigString(String path, Player player, int number)
    {
        String message =
            plugin.getConfig()
                .getString(path)
                .replace("<player>", player.getName())
                .replace("<number>", String.valueOf(number));
        return ParseColor(message);
    }

    public static void consoleLog(String msg)
    {
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    public static void consoleLogWarn(String msg)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Warning] " + msg);
    }

    public static void consoleLogError(String msg)
    {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Error] " +  msg);
    }

    public static void sendSidebarMessage(Player player, String message)
    {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
    }
}