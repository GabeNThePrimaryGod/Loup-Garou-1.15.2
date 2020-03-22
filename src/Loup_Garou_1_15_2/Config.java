package Loup_Garou_1_15_2;

public class Config
{
    public static Main plugin = null;

    public static boolean isDebugMode() { return plugin.getConfig().getBoolean("debugMode"); }


}
