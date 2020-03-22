package Loup_Garou_1_15_2.listeners;

import Loup_Garou_1_15_2.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener
{
    private Main plugin;

    public JoinListener(Main plugin)
    {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        //plugin.JoinGame(event.getPlayer());
    }
}
