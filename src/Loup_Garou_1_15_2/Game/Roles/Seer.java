package Loup_Garou_1_15_2.Game.Roles;

import Loup_Garou_1_15_2.Game.GUI;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Seer extends LgRole
{
    GUI seeGui = null;

    List<String> seen = new ArrayList<>();

    public Seer(LgPlayer lgPlayer)
    {
        super(lgPlayer);
        description = Tools.getConfigString("roleDescription_Seer");

        buildSeeGUI();
    }

    void buildSeeGUI()
    {
        seeGui = new GUI(18, "Qui veut tu voir", lgPlayer.player);

        seeGui.setBuilder(()->
        {
            for(LgPlayer _lgPlayer : game.getLgPlayers().values())
            {
                if(_lgPlayer.player != lgPlayer.player)
                {
                    seeGui.addItem(Material.PLAYER_HEAD, lgPlayer.player.getName(), "selectPlayer",
                        (GUI.Item item) ->
                        {
                            if(seen.contains(_lgPlayer.player.getName()))
                            {
                                item.addLore("Il est : " + _lgPlayer.role.toString());
                            }
                        });
                }
            }
        });
    }

    @Override
    public String toString()
    {
        return Tools.getConfigString("roleName_Seer");
    }

    @Override
    public void onNight()
    {
        seeGui.refresh();
        seeGui.open();
    }

    @Override
    public void onGuiAction(String actionName, ItemStack itemStack, Inventory inventory)
    {
        switch (actionName)
        {
            case "selectPlayer":
                see(itemStack.getItemMeta().getDisplayName());
                break;
        }
    }

    void see(String seenName)
    {
        seen.add(seenName);
        seeGui.refresh();
        lgPlayer.player.sendMessage(seenName + " est " + game.getLgPlayers().get(seenName).role.toString());

        game.nextRoleTurn();
    }
}
