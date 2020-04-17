package Loup_Garou_1_15_2.commands;

import Loup_Garou_1_15_2.Main;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RewardSystem
{
    Map<UUID, Date> rewardedPlayers;

    Main plugin;

    public RewardSystem()
    {
        plugin = Tools.plugin;

        if(plugin.getConfig().get("rewardedPlayers") == null)
        {
            rewardedPlayers = new HashMap<>();
            saveConfig();
        }
        else
        {
            rewardedPlayers = (Map<UUID, Date>)plugin.getConfig().get("rewardedPlayers");
        }
    }

    public void onJoin(Player player, Boolean isNew)
    {
        if(player == null)
        {
            return;
        }

        Date currentDate = new Date(System.currentTimeMillis());

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Tools.consoleLog("current Date : " + formatter.format(currentDate));

        if(isNew)
        {
            Tools.consoleLog("New Player reward save");

            Date customDate = (Date)currentDate.clone();
            customDate.setDate(customDate.getDate() - 1);

            Tools.consoleLog("custom Date : " + formatter.format(customDate));

            rewardedPlayers.put(player.getUniqueId(), customDate);
            saveConfig();
        }
        else
        {
            Tools.consoleLog("Old Player reward test");

            Date lastRewardDate = rewardedPlayers.get(player.getUniqueId());

            Tools.consoleLog("last reward date : " + formatter.format(lastRewardDate));

            if(lastRewardDate.getYear() >= currentDate.getYear()
            && lastRewardDate.getMonth() >= currentDate.getMonth()
            && lastRewardDate.getDay() >= currentDate.getDay() + 1)
            {
                giveReward(player, currentDate);
            }
            else
            {
                Bukkit.broadcastMessage("pas recompense reviens dans un jour");
            }
        }
    }

    private void saveConfig()
    {
        plugin.getConfig().set("rewardedPlayers", rewardedPlayers);
        plugin.saveConfig();
    }

    private void giveReward(Player player, Date currentDate)
    {
        Bukkit.broadcastMessage("Le joueur " + player.getName() + " a gagner une recompense");

        rewardedPlayers.replace(player.getUniqueId(), currentDate);
        saveConfig();
    }
}
