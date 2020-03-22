package Loup_Garou_1_15_2.Game.Roles;

import Loup_Garou_1_15_2.Game.Game;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class LgRole
{
    public LgPlayer lgPlayer;
    public String description = "";

    protected Game game;

    public LgRole(LgPlayer lgPlayer)
    {
        this.lgPlayer = lgPlayer;
        game = Tools.plugin.gameManager.getGame();
    }

    public void onNight()
    {
        lgPlayer.player.sendMessage("if you see this it's mean your role's onNight() event is not set");
        game.nextRoleTurn();
    }

    // actionName correspond a LocalizedName, itemStack a l'item qui avec laquel il y a eut interaction et inventory l'inventaire qui contient l'item
    public void onGuiAction(String actionName, ItemStack itemStack, Inventory inventory)
    {
        lgPlayer.player.sendMessage("if you see this it's mean your role's onGuiAction() event is not set");
    }

    public void onDeath()
    {
        Bukkit.broadcastMessage(lgPlayer.player.getName() + " est mort cette nuit il étais " + toString());

        lgPlayer.player.setHealth(0);       //seul moyen de tuer un joueur
        lgPlayer.isDead = true;

        lgPlayer.player.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public String toString()
    {
        return "LgRole";
    }
}
