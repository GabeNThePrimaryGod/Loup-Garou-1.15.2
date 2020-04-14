package Loup_Garou_1_15_2.listeners;

import Loup_Garou_1_15_2.Game.Game;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class InventoryListener implements Listener
{
    private List<String> lgEvents = new ArrayList<>();

    public InventoryListener(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerClickInventory(InventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        Game game = Tools.plugin.gameManager.getGame();

        if(event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null || event.getCurrentItem().getItemMeta().getLocalizedName() == null)
            return;

        this.lgEvents = Tools.plugin.getConfig().getStringList("guiEvents");
        String eventName = event.getCurrentItem().getItemMeta().getLocalizedName();

        if(!lgEvents.contains(eventName))
            return;

        if(game == null)
        {
            player.sendMessage(Tools.getConfigString("noGameError"));
            return;
        }

        if(game.getLgPlayers().get(player.getName()) == null)
        {
            player.sendMessage(Tools.getConfigString("notInGameError"));
            return;
        }

        LgPlayer lgPlayer = game.getLgPlayers().get(player.getName());

        Tools.consoleLog("GUI event " + eventName + " on " + lgPlayer.role.toString()+ " " + lgPlayer.player.getName());

        if(game.time == Game.Time.night)
        {
            // on envoie les donnée nécéssaires a l'evenement onGuiAction du role
            event.setCancelled(true);

            // check is it's Inventory event come from the current role playing
            if(lgPlayer.role.getClass().equals(game.getCurrentRoleRound()))
            {
                lgPlayer.role.onGuiAction(eventName, event.getCurrentItem(), inventory);
            }
        }
        else if(game.time == Game.Time.day)
        {
            // on envoie les données nécéssaires a l'evenement onGuiAction de lgPlayer
            event.setCancelled(true);
            lgPlayer.onGuiAction(eventName, event.getCurrentItem(), inventory);
        }
    }
}
