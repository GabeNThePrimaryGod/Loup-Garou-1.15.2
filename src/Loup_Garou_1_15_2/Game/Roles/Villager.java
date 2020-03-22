package Loup_Garou_1_15_2.Game.Roles;

import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;

public class Villager extends LgRole
{
    public Villager(LgPlayer lgPlayer)
    {
        super(lgPlayer);
        description = Tools.getConfigString("roleDescription_Villager");
    }

    @Override
    public String toString()
    {
        return Tools.getConfigString("roleName_Villager");
    }
}
