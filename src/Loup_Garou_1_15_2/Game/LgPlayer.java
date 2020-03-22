package Loup_Garou_1_15_2.Game;

import Loup_Garou_1_15_2.Game.Roles.LgRole;
import Loup_Garou_1_15_2.Game.Roles.WereWolf;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class LgPlayer
{
    private Game game;

    public Player player;
    public LgRole role = null;

    public boolean isMayor()
    {
        if(game.mayor == this)
        {
            return true;
        }
        return false;
    }

    private static GUI mayorGUI = null;
    private static String mayorChoise = null;

    public Boolean isDead = false;

    private House house = null;
    public void setHouse(House house) { this.house = house; house.lgPlayer = this; }
    public House getHouse() { return house; }

    private GUI voteGUI = null;

    public static Map<String, String> votes = new HashMap<>();
    public boolean confirmVote = false;

    public LgPlayer(Player player)
    {
        this.player = player;
        game = Tools.plugin.gameManager.getGame();

        buildVoteGUI();
    }

    private void buildVoteGUI()
    {
        Bukkit.getConsoleSender().sendMessage("Building VoteGUI for LgPlayer " + player.getName());

        voteGUI = new GUI(18, "Vote", player);
        voteGUI.setBuilder(() ->
        {
            for (LgPlayer _lgPlayer : game.getLgPlayers().values())
            {
                voteGUI.addItem(Material.PLAYER_HEAD, _lgPlayer.player.getName(), "selectPlayer",
                    (GUI.Item item) ->
                    {
                        if (votes.containsValue(_lgPlayer.player.getName()))
                        {
                            for (Map.Entry<String, String> vote : votes.entrySet())
                            {
                                if (vote.getValue().equals(_lgPlayer.player.getName()))
                                {
                                    item.addLore(ChatColor.RED + vote.getKey() + ChatColor.RESET + " a voter pour lui !");
                                }
                            }
                        }
                    });
            }

            //ajout d'un element pour valier le vote

            voteGUI.addItem((confirmVote ? Material.LIME_DYE : Material.RED_DYE),
                (confirmVote ? ChatColor.GREEN + "Prêt" : ChatColor.RED + "Non Prêt"), "confirmSelection",
                (GUI.Item item) ->
                {
                    item.addLore(confirmVote ? ChatColor.WHITE + "Clique pour ne plus être pret" : ChatColor.WHITE +  "Clique pour te mettre en prêt");
                });
        });
    }

    private void mayorGUI(List<String> topVote)
    {
        LgPlayer.mayorGUI = new GUI(18, "Choisi qui vas mourir", player);

        for(String vote : topVote)
        {
            LgPlayer.mayorGUI.addItem(Material.PLAYER_HEAD, vote, "mayorVote");
        }

        player.closeInventory();
        mayorGUI.open();
    }

    public void onDay()
    {
        Tools.consoleLog("onDay " + player.getName());
        voteGUI.refresh();
    }

    public void onOpenVoteGUI()
    {
        if (voteGUI == null)
        {
            Tools.consoleLogError("OnOpenVoteGUI " + player.getName() + " : voteGUI is null");
            return;
        }

        voteGUI.refresh();
        voteGUI.open();
    }

    public void onGuiAction(String actionName, ItemStack itemStack, Inventory inventory)
    {
        switch(actionName)
        {
            case "selectPlayer":
                setVote(itemStack.getItemMeta().getDisplayName());
                break;

            case "confirmSelection":
                onConfirmSelection();
                break;

            case "mayorVote" :

                if(isMayor() && game.currentVote != Game.Vote.none)
                {
                    game.onVoteEnded(itemStack.getItemMeta().getDisplayName());
                }
                else
                {
                    Tools.consoleLogError("A not mayor trying to mayorVote");
                }
                break;
        }
    }

    private boolean isEveryoneConfirmed()
    {
        for(LgPlayer lgPlayer : game.getLgPlayers().values())
            if(!lgPlayer.confirmVote)
                return false;

        return true;
    }

    private void onConfirmSelection()
    {
        confirmVote = !confirmVote;

        // refresh du gui pour changer la couleur de l'item de validation
        voteGUI.refresh();

        Bukkit.broadcastMessage(player.getName() + " est " + (confirmVote ? ChatColor.GREEN + "prêt" : ChatColor.RED + "plus prêt") + " a voter");

        if(isEveryoneConfirmed())
        {
            for(LgPlayer lgPlayer : game.getLgPlayers().values())
            {
                lgPlayer.confirmVote = false;
                lgPlayer.voteGUI.close(lgPlayer.player);
            }

            List<String> topVote = getTopVote();

            // si il n'y a qu'un topVote
            if(topVote.size() == 1)
            {
                game.onVoteEnded(topVote.get(0));
            }
            // si il y des execo
            else
            {
                if(game.currentVote != Game.Vote.Mayor && game.mayor != null)
                {
                    for(LgPlayer lgPlayer : game.getLgPlayers().values())
                    {
                        if(lgPlayer.isMayor())
                        {
                            lgPlayer.mayorGUI(topVote);
                        }
                    }
                }
                else
                {
                    Tools.consoleLog("No mayor take a random topVote");
                    String voted = topVote.get(Tools.randomNumber(topVote.size()));
                    game.onVoteEnded(voted);
                }
            }
        }
    }


    private List<String> getTopVote()
    {
        Tools.consoleLog("Votes : " + votes.toString());

        if(LgPlayer.votes.size() == 0)
        {
            Tools.consoleLogWarn("getMostVoted : no vote");
            return null;
        }

        // remplisage de tableau votedBalance
        Map<String, Integer> votedBalance = new HashMap<>();

        for (String vote : votes.values())
        {
            if(!votedBalance.containsKey(vote))
            {
                votedBalance.put(vote, 1);
            }
            else
            {
                votedBalance.replace(vote, votedBalance.get(vote).intValue() + 1);
            }
        }

        // on envoie le recapitulatif des votes a tout les loups
        Tools.consoleLog("recap des votes : " + votedBalance.toString());

        List<String> topVote = new ArrayList<>();

        // si la valeur est >= de tout les autres votes on l'ajoute a topvote
        for(Map.Entry<String, Integer> balanceEntry : votedBalance.entrySet())
        {
            int greaterThanCount = 0;

            for(Map.Entry<String, Integer> compareBalanceEntry : votedBalance.entrySet())
            {
                if(balanceEntry.getValue() >= compareBalanceEntry.getValue().intValue())
                {
                    if(!compareBalanceEntry.getKey().equals(balanceEntry.getKey()))
                    {
                        greaterThanCount++;
                    }
                }
            }

            if(greaterThanCount == votedBalance.size() - 1) // -1 par ce qui il ne faut pas qu'il ce prenne en compte lui meme
            {
                topVote.add(balanceEntry.getKey());
            }
        }

        Tools.consoleLog("topVote : " + topVote.toString());
        return topVote;
    }

    private void setVote(String vote)
    {
        // si le joueur n'a pas voter on ajoute directement le vote
        if(votes.get(player.getName()) == null)
        {
            votes.put(player.getName(), vote);
            Bukkit.broadcastMessage(player.getName() + " a voter pour " + ChatColor.DARK_RED + vote);
        }
        // sinon replace sont ancien vote avant de le faire re voter
        else if (!vote.equals(votes.get(player.getName())))
        {
            votes.replace(player.getName(), vote);
            Bukkit.broadcastMessage(player.getName() + " a changer sont vote pour " + ChatColor.DARK_RED + vote);
        }

        // Refresh vote gui for all
        for(LgPlayer lgPlayer : game.getLgPlayers().values())
        {
            lgPlayer.voteGUI.refresh();
        }
    }
}
