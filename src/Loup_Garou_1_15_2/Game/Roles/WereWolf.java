package Loup_Garou_1_15_2.Game.Roles;

import Loup_Garou_1_15_2.Config;
import Loup_Garou_1_15_2.Game.GUI;
import Loup_Garou_1_15_2.Game.Game;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;
import net.md_5.bungee.chat.SelectorComponentSerializer;
import net.minecraft.server.v1_15_R1.AbstractDragonControllerLanded;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.xml.bind.Marshaller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WereWolf extends LgRole
{
    private static Map<String, String> votes = new HashMap<>();

    public GUI matesGUI = null;
    public GUI voteGUI = null;
    public boolean confimVote = false;

    public WereWolf(LgPlayer lgPlayer)
    {
        super(lgPlayer);
        description = Config.getConfigString("roleDescription_WereWolf");

        buildMatesGUI();
        buildVoteGUI();
    }

    private static void sendMessage(String message)
    {
        Tools.plugin.gameManager.getGame().broadcastByRole(WereWolf.class,
                (LgPlayer wolf) -> { wolf.player.sendMessage(message); });
    }

    @Override
    public String toString()
    {
        return Config.getConfigString("roleName_WereWolf");
    }

    @Override
    public void onNight()
    {
        //TODO : changement de team pour pouvoir parler en /teammsg

        printWolfs();

        matesGUI.refresh();
        matesGUI.open();
    }

    private void printWolfs()
    {
        StringBuilder playersString = new StringBuilder("Les loups : " + ChatColor.DARK_RED);

        for(LgPlayer lgPlayer : game.getPlayersByRole(WereWolf.class))
            playersString.append(lgPlayer.player.getName() + " ");

        lgPlayer.player.sendMessage(playersString.toString());
    }

    @Override
    public void onGuiAction(String actionName, ItemStack itemStack, Inventory inventory)
    {
        switch (actionName)
        {
            case "skip":
                lgPlayer.player.closeInventory();

                voteGUI.refresh();
                voteGUI.open();
                break;

            case "selectPlayer":
                setVote(itemStack.getItemMeta().getDisplayName());
                break;

            case "confirmSelection":
                onConfirmSelection();
                break;
        }
    }

    private void buildMatesGUI()
    {
        Tools.consoleLog("Building MatesGUI for WereWolf " + lgPlayer.player.getName());
        matesGUI = new GUI(18, "Mates", lgPlayer.player);

        matesGUI.setBuilder(() ->
        {
            for(LgPlayer _lgPlayer : game.getPlayersByRole(WereWolf.class))
            {
                if(_lgPlayer != lgPlayer)
                {
                    matesGUI.addItem(Material.PLAYER_HEAD, ChatColor.DARK_RED + _lgPlayer.player.getName(),
                            "interruption");
                }
            }

            //ajout d'un element en Material barrier pour passer au vote
            matesGUI.addItem(Material.BARRIER, ChatColor.DARK_GREEN + "Skip", "skip",
                (GUI.Item item) ->
                {
                    item.addLore(ChatColor.GREEN + "Passer au vote");
                });
        });
    }

    public void buildVoteGUI()
    {
        Bukkit.getConsoleSender().sendMessage("Building VoteGUI for WereWolf " + lgPlayer.player.getName());
        voteGUI = new GUI(18, "Vote pour la victime de la nuit", lgPlayer.player);

        voteGUI.setBuilder(() ->
        {
            List<LgPlayer> wereWolfs = game.getPlayersByRole(WereWolf.class);

            for(LgPlayer _lgPlayer : game.getLgPlayers().values())
            {
                voteGUI.addItem(Material.PLAYER_HEAD, _lgPlayer.player.getName(), "selectPlayer",
                (GUI.Item item) ->
                {
                    // si _lgPlayer est un loup-garou
                    if(wereWolfs.contains(_lgPlayer))
                    {
                        item.addLore(ChatColor.RED + "C'est ton pote faut pas le tuer !");
                    }

                    // Rajoute toute les personne qui on voter pour _lgPlayer dans le lore de l'item
                    if(votes.containsValue(_lgPlayer.player.getName()))
                    {
                        for(Map.Entry<String, String> vote : votes.entrySet())
                        {
                            if(vote.getValue().equals(_lgPlayer.player.getName()))
                            {
                                item.addLore(ChatColor.RED + vote.getKey() + ChatColor.RESET + " a voter pour lui !" );
                            }
                        }
                    }
                });
            }

            //ajout d'un element en Material LIME_DYE pour valier le vote

            Material readyMat = confimVote ? Material.LIME_DYE : Material.RED_DYE;

            String lore = confimVote ? "Clique pour ne plus être pret" : "Clique pour te mettre en prêt";
            String displayName = confimVote ? "Prêt" : "Non Prêt";

            voteGUI.addItem(readyMat, displayName, "confirmSelection",
            (GUI.Item item) ->
            {
                item.addLore(confimVote ? "Clique pour ne plus être pret" : "Clique pour te mettre en prêt");
            });
        });
    }

    private void onConfirmSelection()
    {
        confimVote = !confimVote;

        String message = lgPlayer.player.getName() + " est " + (confimVote ? ChatColor.GREEN + "prêt" : ChatColor.RED + "plus prêt") + " a tuer";
        game.broadcastByRole(WereWolf.class, (wereWolf) -> wereWolf.player.sendMessage(message));

        if(isEveryoneConfirmed())
        {
            // open récupère le nom du mort de la nuit pour verifier qu'il est valide
            String votedName = getMostVoted();

            if(votedName != null)
            {
                LgPlayer voted = game.getLgPlayers().get(votedName);
                game.deadThisNight.add(voted);

                game.broadcastByRole(WereWolf.class, (wereWolf) ->
                {
                    wereWolf.player.sendMessage("Vous avez dévorer " + voted.player.getName());
                    ((WereWolf)wereWolf.role).confimVote = false;

                    wereWolf.player.closeInventory();
                });
            }

            // si il n'y a pas de voté
            else
            {
                game.broadcastByRole(WereWolf.class, (wereWolf) ->
                {
                    wereWolf.player.sendMessage(ChatColor.RED + "Vous n'avez dévoré personne");
                    ((WereWolf)wereWolf.role).confimVote = false;

                    wereWolf.player.closeInventory();
                });
            }

            WereWolf.votes.clear();
            game.nextRoleTurn();
        }
    }

    private String getMostVoted()
    {
        Map<String, Integer> votedBalance = new HashMap<>();

        for (String vote : votes.values())
        {
            if(!votedBalance.containsKey(vote))
                votedBalance.put(vote, 1);
            else
                votedBalance.replace(vote, votedBalance.get(vote).intValue() + 1);
        }

        // on envoie le recapitulatif des votes a tout les loups
        game.broadcastByRole(WereWolf.class, (wereWolf) -> wereWolf.player.sendMessage("recap des votes : " + votedBalance.toString()));

        if(votedBalance.size() == 0)
        {
            Tools.consoleLogWarn("getMostVoted : no vote");
            return null;
        }

        List<String> topVote = new ArrayList<>();

        // si la valeur est >= de tout les autres votes on l'ajoute a topvote
        for(Map.Entry<String, Integer> balanceEntry : votedBalance.entrySet())
        {
            int greaterThanCount = 0;

            for(Map.Entry<String, Integer> compareBalanceEntry : votedBalance.entrySet())
            {
                if(balanceEntry.getValue() >= compareBalanceEntry.getValue().intValue())
                    if(!compareBalanceEntry.getKey().equals(balanceEntry.getKey()))
                        greaterThanCount++;
            }

            if(greaterThanCount >= votedBalance.size() - 1) // -1 par ce qui il ne faut pas qu'il ce prenne en compte lui meme
                topVote.add(balanceEntry.getKey());
        }

        // si il n'y a qu'un topVote
        if(topVote.size() == 1)
        {
            return topVote.get(0);
            // si il y des execo
        }
        else
        {
            Tools.consoleLog("random");

            // on prend une valeur random
            String voted = topVote.get(Tools.randomNumber(topVote.size()));
            return (voted == null) ? null : voted;
        }
    }

    private boolean isEveryoneConfirmed()
    {
        for(LgPlayer wereWolf : game.getPlayersByRole(WereWolf.class))
            if(!((WereWolf)wereWolf.role).confimVote)
                return false;

        return true;
    }

    private void setVote(String vote)
    {
        if(game.getPlayersByRole(WereWolf.class).contains(game.getLgPlayers().get(vote)))
        {
            lgPlayer.player.sendMessage(ChatColor.RED + "Tu ne peut pas voter pour un loup-garou !");
            return;
        }

        // si le joueur n'a pas voter on ajoute directement le vote
        if(votes.get(lgPlayer.player.getName()) == null)
        {
            votes.put(lgPlayer.player.getName(), vote);

            // Refresh all vote gui for wolfs
            game.broadcastByRole(WereWolf.class, (wereWolf) ->
            {
                wereWolf.player.sendMessage(lgPlayer.player.getName() + " a voter pour tuer "
                        + ChatColor.DARK_RED + vote);

                ((WereWolf)wereWolf.role).voteGUI.refresh();
            });
        }
        // sinon on retire sont ancien vote avant de le faire re voter
        else if (!vote.equals(votes.get(lgPlayer.player.getName())))
        {
            votes.remove(lgPlayer.player.getName());
            votes.put(lgPlayer.player.getName(), vote);

            // Refresh all vote gui for all wolfs
            game.broadcastByRole(WereWolf.class, (wereWolf) ->
            {
                wereWolf.player.sendMessage(lgPlayer.player.getName() + " a changer sont vote pour "
                        + ChatColor.DARK_RED + vote);

                ((WereWolf)wereWolf.role).voteGUI.refresh();
            });

            votes.put(lgPlayer.player.getName(), vote);
        }
    }

}