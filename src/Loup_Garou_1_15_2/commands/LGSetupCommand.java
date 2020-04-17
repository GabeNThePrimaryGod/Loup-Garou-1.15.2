package Loup_Garou_1_15_2.commands;

import Loup_Garou_1_15_2.Config;
import Loup_Garou_1_15_2.Game.House;
import Loup_Garou_1_15_2.Main;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LGSetupCommand implements CommandExecutor
{
    private Main plugin;

    public LGSetupCommand(Main plugin)
    {
        this.plugin = plugin;
        this.plugin.getCommand("lgSetup").setExecutor(this);
    }

    RewardSystem rewardSystem = new RewardSystem();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage("Only players may use this command");
            return true;
        }

        Player player = (Player) sender;

        switch (args[0])
        {
            case "addhouse":
            case "addHouse" :
                plugin.gameManager.houses.add(new House(player.getLocation()));
                player.sendMessage("You set a new house in " + player.getLocation().toVector().toString());
                break;

            case "houseslist" :
            case "housesList" :
                housesList(player);
                break;

            case "setexecutionroom":
            case "setExecutionRoom":
                plugin.gameManager.executionRoom = player.getLocation();
                player.sendMessage("You set the execution room in " + player.getLocation().toVector().toString());
                break;

            case "reward":
                rewardSystem.onJoin(player, (args[1].equals("new")) ? true : false);
                break;

            default:
                return false;
        }
        return true;
    }

    private void housesList(Player player)
    {
        if(!plugin.gameManager.houses.isEmpty())
        {
            StringBuilder housesString = new StringBuilder("Houses :");

            for (int i = 0; i < plugin.gameManager.houses.size(); i++)
            {
                housesString.append("\nMaison " + i + " : " + plugin.gameManager.houses.get(i).location.toVector().toString());

                if(plugin.gameManager.houses.get(i).lgPlayer != null)
                    housesString.append("\nPlayer : " + plugin.gameManager.houses.get(i).lgPlayer.player.getName());
            }

            player.sendMessage(housesString.toString());
        }
        else
        {
            player.sendMessage(Config.getConfigString("lgHouseListMessage_NoHousesError"));
        }
    }
}
