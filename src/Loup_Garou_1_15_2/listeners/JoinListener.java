package Loup_Garou_1_15_2.listeners;

import Loup_Garou_1_15_2.Main;
import Loup_Garou_1_15_2.Tools;
import Loup_Garou_1_15_2.commands.RewardSystem;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JoinListener implements Listener
{
    private Main plugin;


    public JoinListener(Main plugin)
    {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    //RewardSystem rewardSystem = new RewardSystem();

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        //rewardSystem.onJoin(event);
    }
}
