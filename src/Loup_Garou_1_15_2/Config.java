package Loup_Garou_1_15_2;

import org.bukkit.entity.Player;

public class Config
{
    public static Main plugin = null;

    public static boolean isDebugMode() { return plugin.getConfig().getBoolean("debugMode"); }

    public static String getConfigString(String path)
    {
        String message =
                plugin.getConfig()
                        .getString(path);
        return Tools.ParseColor(message);
    }

    public static String getConfigString(String path, String player)
    {
        String message =
                plugin.getConfig()
                        .getString(path)
                        .replace("<player>", player);
        return Tools.ParseColor(message);
    }

    public static String getConfigString(String path, Player player, int number)
    {
        String message =
                plugin.getConfig()
                        .getString(path)
                        .replace("<player>", player.getName())
                        .replace("<number>", String.valueOf(number));
        return Tools.ParseColor(message);
    }
}
