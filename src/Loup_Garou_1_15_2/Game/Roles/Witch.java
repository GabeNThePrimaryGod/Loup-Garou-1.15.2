package Loup_Garou_1_15_2.Game.Roles;

import Loup_Garou_1_15_2.Game.GUI;
import Loup_Garou_1_15_2.Game.Game;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Witch extends LgRole
{
    private enum ActionType { death, life, none }

    ActionType action = ActionType.none;
    String selectedName = null;

    boolean haveLifePotion = true;
    boolean haveDeathPotion = true;

    private GUI choseActionGUI = null;
    private GUI selectPlayerGUI = null;

    public Witch(LgPlayer lgPlayer)
    {
        super(lgPlayer);
        description = Tools.getConfigString("roleDescription_Witch");

        buildChoseActionGUI();
        buildSelectPlayerGUI();
    }

    @Override
    public String toString()
    {
        return Tools.getConfigString("roleName_Witch");
    }

    private void printDeads()
    {
        StringBuilder deadString = new StringBuilder("Mort cette nuit : ");

        for(LgPlayer dead : game.deadThisNight)
        {
            deadString.append(dead.player.getName() + " ");
        }

        lgPlayer.player.sendMessage(deadString.toString());
    }

    @Override
    public void onNight()
    {
        printDeads();

        choseActionGUI.refresh();
        choseActionGUI.open();
    }

    private void buildChoseActionGUI()
    {
        choseActionGUI = new GUI(InventoryType.BREWING, "Choix de l'action", lgPlayer.player);

        choseActionGUI.setBuilder(()->
            {
                choseActionGUI.addItem(Material.PLAYER_HEAD, 4, (selectedName == null ? ChatColor.RED + "Vous n'avez pas choisi de joueur" : selectedName),
                    "selectPlayerGUI",
                    (GUI.Item item) ->
                    {
                        item.addLore("Clique ici pour choisir un joueur");
                    });

                choseActionGUI.addItem(Material.BARRIER, 1, Tools.ParseColor("§4§l§nNe rien faire"), "noAction");

                if(haveDeathPotion)
                {
                    choseActionGUI.addItem(Material.SPLASH_POTION, 0, Tools.ParseColor("§c§l§nPotion de Mort"), "deathPotion");
                }
                if(haveLifePotion)
                {
                    choseActionGUI.addItem(Material.POTION, 2, Tools.ParseColor("§e§l§nPotion de Vie"), "lifePotion");
                }
            });
    }

    private void buildSelectPlayerGUI()
    {
        selectPlayerGUI = new GUI(18, "Choisi un joueur a tuer ou sauver", lgPlayer.player);

        selectPlayerGUI.setBuilder(() ->
            {
                for (LgPlayer _lgPlayer : game.getLgPlayers().values())
                {
                    selectPlayerGUI.addItem(Material.PLAYER_HEAD, _lgPlayer.player.getName(), "selectPlayer",
                        (GUI.Item item) ->
                        {
                            if(game.deadThisNight.contains(_lgPlayer) && haveLifePotion)
                            {
                                item.addLore(ChatColor.RED + "Il est mort cette nuit !");
                            }
                        });
                }

                selectPlayerGUI.addItem(Material.BARRIER, Tools.ParseColor("§4§l§nBack"), "choseActionGUI");
            });
    }

    @Override
    public void onGuiAction(String actionName, ItemStack itemStack, Inventory inventory)
    {
        switch (actionName)
        {
            case "noAction" :
                endTurn();
                break;

            case "choseActionGUI" :

                lgPlayer.player.closeInventory();

                choseActionGUI.refresh();
                choseActionGUI.open();
                break;

            case "selectPlayerGUI" :

                lgPlayer.player.closeInventory();

                selectPlayerGUI.refresh();
                selectPlayerGUI.open();
                break;

            case "selectPlayer" :

                lgPlayer.player.closeInventory();

                selectedName = itemStack.getItemMeta().getDisplayName();

                choseActionGUI.refresh();
                choseActionGUI.open();
                break;

            case "lifePotion":
                lifePotion();
                break;

            case "deathPotion":
                deathPotion();
                break;
        }
    }

    void lifePotion()
    {
        if(selectedName == null)
        {
            Tools.consoleLogWarn("no lifePotion target " + selectedName + " on Witch " + lgPlayer.player.getName());
            lgPlayer.player.sendMessage(Tools.getConfigString("noSelectionError"));
            return;
        }

        // temporaire

        if(!game.deadThisNight.contains(game.getLgPlayers().get(selectedName)))
        {
            Tools.consoleLogWarn("wrong lifePotion Target " + selectedName + " on Witch " + lgPlayer.player.getName());
            lgPlayer.player.sendMessage(Tools.getConfigString("wrongLifePotionTargetError"));
            return;
        }

        for(int i = 0; i < game.deadThisNight.size(); i++)
        {
            if(game.deadThisNight.get(i).player.getName().equals(selectedName))
            {
                game.deadThisNight.remove(i);
                lgPlayer.player.sendMessage(Tools.getConfigString("savingMessage", selectedName));
            }
        }

        haveLifePotion = false;

        endTurn();
    }

    void deathPotion()
    {
        if(selectedName == null)
        {
            Tools.consoleLogWarn("no deathPotion target " + selectedName + " on Witch " + lgPlayer.player.getName());
            lgPlayer.player.sendMessage(Tools.getConfigString("noSelectionError"));
            return;
        }

        if(!game.getLgPlayers().containsKey(selectedName))
        {
            Tools.consoleLogError("Witch deathPotion : selected player's not in game");
            return;
        }

        game.deadThisNight.add(game.getLgPlayers().get(selectedName));
        lgPlayer.player.sendMessage(Tools.getConfigString("killingMessage", selectedName));

        haveDeathPotion = false;

        endTurn();
    }

    private void endTurn()
    {
        choseActionGUI.close();
        action = ActionType.none;
        selectedName = null;

        game.nextRoleTurn();
    }
}