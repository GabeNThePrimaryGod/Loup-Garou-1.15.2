package Loup_Garou_1_15_2.Game;

import Loup_Garou_1_15_2.Config;
import Loup_Garou_1_15_2.Game.Roles.*;
import Loup_Garou_1_15_2.Main;
import Loup_Garou_1_15_2.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class Game
{
    private Map<String, LgPlayer> lgPlayers = new HashMap<>();
    public Map<String, LgPlayer> getLgPlayers()
    {
        Map<String, LgPlayer> _lgPlayers = new HashMap<>();

        for(Map.Entry<String, LgPlayer> lgPlayerEntry : lgPlayers.entrySet())
            if(!lgPlayerEntry.getValue().isDead)
                _lgPlayers.put(lgPlayerEntry.getKey(), lgPlayerEntry.getValue());

        return _lgPlayers;
    }

    public enum  Time { day, night, none }
    public Time time = Time.none;

    private Class<? extends LgRole> currentRoleRound = null;
    public Class<? extends LgRole> getCurrentRoleRound() { return currentRoleRound; }

    public List<LgPlayer> deadThisNight = new ArrayList<>();

    public enum Vote { Mayor, Kill, none }
    public Vote currentVote = Vote.none;

    public LgPlayer mayor = null;

    private Main plugin;
    public Game(Main plugin)
    {
        this.plugin = plugin;

        //TODO : ajouter une scheduled task depuis la class GUI ou une item d'ouverture
    }

    public void Join(Player player)
    {
        if(!lgPlayers.containsKey(player.getName()))
        {
            lgPlayers.put(player.getName(), new LgPlayer(player));
            Bukkit.broadcastMessage(Tools.getConfigString("lgJoinGameMessage", player.getName()));
        }
        else
            player.sendMessage(Tools.getConfigString("lgJoinGameMessage_InError", player.getName()));
    }

    public void Leave(Player player)
    {
        if(lgPlayers.containsKey(player.getName()))
        {
            lgPlayers.remove(player.getName());
            Bukkit.broadcastMessage(Tools.getConfigString("leaveGameMessage", player.getName()));
        }
        else
            player.sendMessage(Tools.getConfigString("leaveGameMessage_NotInError", player.getName()));
    }

    public LgPlayer getPlayerByRole(Class<? extends LgRole> roleClass)
    {
        for(LgPlayer lgPlayer : getLgPlayers().values())
            if(roleClass.isInstance(lgPlayer.role))
                return lgPlayer;

        return null;
    }

    public List<LgPlayer> getPlayersByRole(Class<? extends LgRole> roleClass)
    {
        List<LgPlayer> getLgPlayers = new ArrayList<>();

        for(LgPlayer lgPlayer : getLgPlayers().values())
            if(roleClass.isInstance(lgPlayer.role))
                getLgPlayers.add(lgPlayer);

        return getLgPlayers;
    }

    public void broadcastByRole(Class<? extends LgRole> role, Consumer<LgPlayer> callback)
    {
        for (LgPlayer werewolf : getPlayersByRole(role))
            callback.accept(werewolf);
    }

    private boolean isAllPlayersHaveRole()
    {
        for(LgPlayer lgPlayer : lgPlayers.values())
            if(lgPlayer.role == null)
                return false;

        return true;
    }

    public void Start()
    {
        if(Config.isDebugMode())
        {
            Bukkit.broadcastMessage(ChatColor.RED + "[Warning] La partie de loup-Garou est lancée en mode-debug");
        }

        Bukkit.broadcastMessage(Tools.getConfigString("startGameMessage"));

        for(LgPlayer lgPlayer : getLgPlayers().values())
        {
            Tools.sendSidebarMessage(lgPlayer.player, "test message");
        }

        // met tout le monde en gameMode adventure
        for(LgPlayer lgPlayer : lgPlayers.values())
        {
            lgPlayer.player.setGameMode(GameMode.ADVENTURE);
        }

        // nombre de role dans la partie
        int wereWolf = 2;
        int seer = 1;
        int witch = 1;
        int cupid = 0;

        /*
        * répartition des roles dans l'odre de priorité suivant :
        * loup-Garou
        * Voyante
        * Cupidon
        * Sorcière
        * Villageois
        */
        while(!isAllPlayersHaveRole())
        {
            LgPlayer lgPlayer = (LgPlayer) getLgPlayers().values().toArray()[Tools.randomNumber(getLgPlayers().size())];

            if(lgPlayer.role == null)
            {
                if(wereWolf > 0)
                {
                    lgPlayer.role = new WereWolf(lgPlayer);
                    wereWolf--;
                }
                else if (seer > 0)
                {
                    lgPlayer.role = new Seer(lgPlayer);
                    seer--;
                }
                else if (witch > 0)
                {
                    lgPlayer.role = new Witch(lgPlayer);
                    witch--;
                }
                else if (cupid > 0)
                {
                    lgPlayer.role = new Cupid(lgPlayer);
                    cupid--;
                }
                else
                {
                    lgPlayer.role = new Villager(lgPlayer);
                }
            }
        }

        for(LgPlayer lgPlayer : lgPlayers.values())
        {
            lgPlayer.player.sendMessage("Ton role est : " + lgPlayer.role.toString());
            lgPlayer.player.sendMessage(lgPlayer.role.description);

            Tools.sendSidebarMessage(lgPlayer.player, ChatColor.RED +  "Ton role est : " + lgPlayer.role.toString());
            lgPlayer.player.sendTitle("Tu est " + lgPlayer.role.toString(), lgPlayer.role.description, 50, 50, 50);
        }

        time = Time.night;
        onNight();
    }

    private void onNight()
    {
        time = Time.night;

        Bukkit.broadcastMessage(Tools.getConfigString("onNightMessage"));

        //TODO : voir comment recupere le nom du monde courant
        // maybe a partir de l'UUID d'un joueur ?

        //changer l'heure pour que ce soit la nuit
        Objects.requireNonNull(Bukkit.getWorld("World")).setTime(15000);

        // tout le monde tp dans ça maison
        for(LgPlayer lgPlayer : getLgPlayers().values())
        {
            lgPlayer.getHouse().tpTo();
        }

        //TODO : faire en sorte que les joueurs ne puisse pas bouger pendant la nuit

        nextRoleTurn();
    }

    private void onDay()
    {
        time = Time.day;

        //changer l'heure pour que ce soit le jour
        Objects.requireNonNull(Bukkit.getWorld("World")).setTime(0);

        Bukkit.broadcastMessage(Tools.getConfigString("onDayMessage"));

        if(deadThisNight.size() == 0)
        {
            Bukkit.broadcastMessage(Tools.getConfigString("noDeathMessage"));
        }
        else
        {
            //annonce des morts de la nuit et de leurs roles, peut etre override pour rajouter des events
            for(LgPlayer dead : deadThisNight)
            {
                if(mayor != null && dead.player.getName().equals(mayor.player.getName()))
                {
                    mayor = null;
                }

                Bukkit.broadcastMessage(dead.player.getName() + " est mort cette nuit il étais " + dead.role.toString());
                dead.role.onDeath();
            }
        }

        deadThisNight.clear();

        if(isSomeoneWon())
        {
            if(Config.isDebugMode())
            {
                Bukkit.broadcastMessage(ChatColor.RED + "La partie devrais etre fin mais on est en debug mode donc non");
            }
            else
            {
                onEndGame();
                return;
            }
        }

        for(LgPlayer lgPlayer : getLgPlayers().values())
        {
            lgPlayer.onDay();
        }

        // lancement des votes
        if(mayor == null)
        {
            setCurrentVote(Vote.Mayor);
        }
        else
        {
            setCurrentVote(Vote.Kill);
        }

        time = Time.day;
    }

    void onEndGame()
    {
        for(LgPlayer lgPlayer : lgPlayers.values())
        {
            lgPlayer.player.setGameMode(GameMode.ADVENTURE);
        }

        time = Time.none;
        currentVote = Vote.none;
        mayor = null;
    }

    /*  fonctionnement du syteme de verification de role
     *
     *  a partir d'une list de Class<?> on vérifie si le role est une instance d'au moin un element de la list
     *  si oui on le rajoute a une list qui sera retourner
     */

    //region is<Class>

    Class<?>[] villagerClasses = { Villager.class, Cupid.class, Seer.class, Witch.class };

    boolean isVillager(LgRole role)
    {
        for(Class<?> villagerClass : villagerClasses)
        {
            if(villagerClass.isInstance(role))
            {
                return true;
            }
        }

        return false;
    }

    List<LgPlayer> getVillagers()
    {
        List<LgPlayer> villagers = new ArrayList<>();

        for (LgPlayer lgPlayer : getLgPlayers().values())
        {
            if(isVillager(lgPlayer.role))
            {
                villagers.add(lgPlayer);
            }
        }

        return  villagers;
    }



    Class<?>[] wereWolfClasses = { WereWolf.class };

    boolean isWereWolf(LgRole role)
    {
        for(Class<?> wereWolfClass : wereWolfClasses)
        {
            if(wereWolfClass.isInstance(role))
            {
                return true;
            }
        }

        return false;
    }

    List<LgPlayer> getWereWolfs()
    {
        List<LgPlayer> werewolfs = new ArrayList<>();

        for (LgPlayer lgPlayer : getLgPlayers().values())
        {
            if(isWereWolf(lgPlayer.role))
            {
                werewolfs.add(lgPlayer);
            }
        }

        return werewolfs;
    }

    //endregion is<Class>

    boolean isSomeoneWon()
    {
        //TODO : conceptualiser un systeme modulaire de verification de victoire qui inclu les vitoires solo

        List<LgPlayer> villagers = getVillagers();
        List<LgPlayer> wereWolfs = getWereWolfs();


        if(wereWolfs.size() == 0 && villagers.size() == 0)
        {
            // on recupere meme les joueurs qui sont morts
            for(LgPlayer lgPlayer : lgPlayers.values())
            {
                Tools.sendSidebarMessage(lgPlayer.player, "Tout le monde a perdu !");
                lgPlayer.player.sendTitle("Fin de la Partie", ChatColor.RED + "Pas de vainqueurs !", 50, 50, 50);
            }

            return true;
        }

        if(wereWolfs.size() == 0)
        {
            for(LgPlayer villager : villagers)
            {
                Tools.sendSidebarMessage(villager.player, ChatColor.RED +  "victoire des villagois");
                villager.player.sendTitle("Les " + ChatColor.DARK_GREEN + "Villagois " + ChatColor.WHITE + "remporte la partie !", ChatColor.RED + "vous avez gagner !", 50, 50, 50);
            }

            for(LgPlayer wereWolf : wereWolfs)
            {
                Tools.sendSidebarMessage(wereWolf.player, ChatColor.GREEN +  "vous avez perdu");
                wereWolf.player.sendTitle("Les " + ChatColor.DARK_GREEN + "Villagois " + ChatColor.WHITE + "remporte la partie !", ChatColor.GREEN + "vous avez perdu la partie", 50, 50, 50);
            }

            return true;
        }

        if(villagers.size() == 0)
        {
            for(LgPlayer wereWolf : wereWolfs)
            {
                Tools.sendSidebarMessage(wereWolf.player, ChatColor.GREEN +  "victoire des loups");
                wereWolf.player.sendTitle("Les " + ChatColor.DARK_RED + "Loup-Garou " + ChatColor.WHITE + "remporte la partie !", ChatColor.GREEN + "vous avez gagner !", 50, 50, 50);
            }

            for(LgPlayer villager : villagers)
            {
                Tools.sendSidebarMessage(villager.player, ChatColor.RED +  "vous avez perdu");
                villager.player.sendTitle("Les " + ChatColor.DARK_RED + "Loup-Garou " + ChatColor.WHITE + "remporte la partie !", ChatColor.RED + "vous avez perdu la partie", 50, 50, 50);
            }

            return true;
        }

        return false;
    }

    public void setCurrentVote(Vote currentVote)
    {
        this.currentVote = currentVote;

        if(currentVote == Vote.Mayor)
        {
            Bukkit.broadcastMessage(Tools.getConfigString("mayorElectionMessage"));
        }
        if(currentVote == Vote.Kill)
        {
            Bukkit.broadcastMessage((Tools.getConfigString("killElectionMessage")));
        }
    }

    public void onVoteEnded(String vote)
    {
        LgPlayer.votes.clear();

        if(vote == null)
        {
            Bukkit.broadcastMessage(Tools.getConfigString("noVotedMessage"));
        }

        if(currentVote == Vote.Mayor)
        {
            if(vote != null)
            {
                Bukkit.broadcastMessage(Tools.getConfigString("newMayorMessage", vote));

                if(getLgPlayers().get(vote) != null)
                {
                    mayor = getLgPlayers().get(vote);
                }
            }

            setCurrentVote(Vote.Kill);
        }

        else if(currentVote == Vote.Kill)
        {
            if(vote != null)
            {
                Bukkit.broadcastMessage(Tools.getConfigString("votedDeathMessage", vote));

                if(getLgPlayers().get(vote) != null)
                {
                    getLgPlayers().get(vote).role.onDeath();
                }
            }

            setCurrentVote(Vote.none);
            onNight();
        }
    }

    public boolean roleIsInGame(Class<? extends LgRole> role)
    {
        List<LgPlayer> lgRoles = getPlayersByRole(role);

        if(lgRoles.size() == 0)
            return false;

        int deadCount = 0;
        for(LgPlayer lgPlayer : lgRoles)
        {
            if(lgPlayer.isDead)
                deadCount++;
        }

        if(deadCount == lgRoles.size())
        {
            return false;
        }

        return true;
    }

     /*
     * A chaques appel fait tourné les roles dans ce sens
     * Cupidon
     * Voyante
     * Loup
     * Sorcière
     */

     int currentRoleIndex = 0;

    Class<?>[] roleRounds = { Cupid.class, Seer.class, WereWolf.class, Witch.class };

    public void nextRoleTurn()
    {

        if(currentRoleIndex == roleRounds.length)
        {
            currentRoleRound = null;
            currentRoleIndex = 0;
            onDay();
            return;
        }

        currentRoleRound = (Class<? extends LgRole>) roleRounds[currentRoleIndex++];

        if(roleIsInGame(currentRoleRound))
        {
            onRoleRound();
        }
        else
        {
            nextRoleTurn();
        }
    }

    private void onRoleRound()
    {
        Bukkit.broadcastMessage("C'est le tour des " + currentRoleRound.toString());

        for(LgPlayer lgPlayer : getPlayersByRole(currentRoleRound))
        {
            lgPlayer.role.onNight();
        }
    }
}