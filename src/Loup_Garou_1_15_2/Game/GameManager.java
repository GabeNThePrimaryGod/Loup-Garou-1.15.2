package Loup_Garou_1_15_2.Game;

import Loup_Garou_1_15_2.Main;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameManager
{
    private Main plugin;
    public GameManager(Main plugin) { this.plugin = plugin; }

    private Game game = null;
    public List<House> houses = new ArrayList<>();
    public Location executionRoom = null;

    public Game getGame() { return game; }

    public void CreateGame(Player player)
    {
        if(game == null)
        {
            game = new Game(plugin);
            Bukkit.broadcastMessage(Tools.getConfigString("lgCreateGameMessage", player.getName()));
        }
        else
            player.sendMessage(Tools.getConfigString("lgCreateGameMessage_Error", player.getName()));
    }

    public void DeleteGame(Player player)
    {
        if(game != null)
        {
            game = null;
            Bukkit.broadcastMessage(Tools.getConfigString("lgDeleteGameMessage", player.getName()));
        }
        else
            player.sendMessage(Tools.getConfigString("lgDeleteGameMessage_Error", player.getName()));
    }

    public void JoinGame(Player player)
    {
        if(game != null)
            game.Join(player);
        else
            player.sendMessage(Tools.getConfigString("noGameError"));
    }

    public void LeaveGame(Player player)
    {
        if(game != null)
            game.Leave(player);
        else
            player.sendMessage(Tools.getConfigString("noGameError"));
    }

    public void StartGame(Player player)
    {
        // if no game return
        if(game == null)
        {
            player.sendMessage(Tools.getConfigString("noGameError"));
            return;
        }

        // enough players ?
        if(game.getLgPlayers().size() < plugin.getConfig().getInt("minPlayers"))
        {
            player.sendMessage(Tools.getConfigString("startGame_NotEnoughtPlayersError"));
            return;
        }

        // enough houses ?
        if(houses.size() < game.getLgPlayers().size())
        {
            player.sendMessage(Tools.getConfigString("startGame_NotEnoughtHousesError"));
            return;
        }

        // houses distribution
        // c'est moche mais ça marche
        int i = 0;
        for(LgPlayer lgPlayer : game.getLgPlayers().values())
        {
            lgPlayer.setHouse(houses.get(i));

            Bukkit.broadcastMessage(Tools.getConfigString("distributeHousesMessage", lgPlayer.player, i));
            i++;
        }

        game.Start();
    }
}