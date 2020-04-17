package Loup_Garou_1_15_2.commands;

import Loup_Garou_1_15_2.Config;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Main;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LGGameCommand implements CommandExecutor
{
    private Main plugin;

    public LGGameCommand(Main plugin)
    {
        this.plugin = plugin;
        this.plugin.getCommand("lgGame").setExecutor(this);
    }

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
            case "create" :
                plugin.gameManager.CreateGame(player);
                break;
            case "delete" :
                plugin.gameManager.DeleteGame(player);
                break;
            case "join" :
                plugin.gameManager.JoinGame(player);
                break;
            case "leave" :
                plugin.gameManager.LeaveGame(player);
                break;
            case "list" :
                printPlayersList(player);
                break;
            case "start":
                plugin.gameManager.StartGame(player);
                break;
            default:
                return false;
        }
        return true;
    }

    private void printPlayersList(Player player)
    {
        if(plugin.gameManager.getGame() != null)
        {
            if(!plugin.gameManager.getGame().getLgPlayers().isEmpty())
            {
                String playersString = "Joueurs dans la partie :";

                for (LgPlayer lgPlayer : plugin.gameManager.getGame().getLgPlayers().values())
                {
                    playersString = playersString + "\n" + lgPlayer.player.getName() + " Role: " + lgPlayer.role.toString();
                }

                player.sendMessage(playersString);
            }
            else
                player.sendMessage(Config.getConfigString("noPlayersError"));
        }
        else
            player.sendMessage(Config.getConfigString("noGameError"));
    }
}
