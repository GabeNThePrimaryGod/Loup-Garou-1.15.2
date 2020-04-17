package Loup_Garou_1_15_2.listeners;

import Loup_Garou_1_15_2.Config;
import Loup_Garou_1_15_2.Game.Game;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;
import com.mojang.authlib.BaseUserAuthentication;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class BlockListener implements Listener
{
    public BlockListener(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRightClickOnBlock(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK )
            return;

        Block block = event.getClickedBlock();

        if(block.getType() != Material.PLAYER_HEAD && block.getType() != Material.LECTERN)
            return;

        Game game = Tools.plugin.gameManager.getGame();
        Player player = event.getPlayer();

        if(game == null)
        {
            player.sendMessage(Config.getConfigString("noGameError"));
            return;
        }

        if(game.getLgPlayers().get(player.getName()) == null)
        {
            player.sendMessage(Config.getConfigString("notInGameError"));
            return;
        }

        LgPlayer lgPlayer = game.getLgPlayers().get(player.getName());
        event.setCancelled(true);

        if(game.time == Game.Time.day)
        {
            if(game.currentVote == Game.Vote.Kill && block.getType() == Material.PLAYER_HEAD)
                lgPlayer.onOpenVoteGUI();

            if(game.currentVote == Game.Vote.Mayor && block.getType() == Material.LECTERN)
                lgPlayer.onOpenVoteGUI();
        }
    }
}
