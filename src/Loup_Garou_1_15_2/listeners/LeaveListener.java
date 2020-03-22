package Loup_Garou_1_15_2.listeners;

import Loup_Garou_1_15_2.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener
{
    private Main plugin;

    public LeaveListener(Main plugin)
    {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event)
    {
        plugin.gameManager.LeaveGame(event.getPlayer());
    }
}
