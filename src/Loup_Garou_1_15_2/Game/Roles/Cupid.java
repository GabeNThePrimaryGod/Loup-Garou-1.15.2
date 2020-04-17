package Loup_Garou_1_15_2.Game.Roles;

import Loup_Garou_1_15_2.Config;
import Loup_Garou_1_15_2.Game.LgPlayer;
import Loup_Garou_1_15_2.Tools;

public class Cupid extends LgRole
{
    public Cupid(LgPlayer lgPlayer)
    {
        super(lgPlayer);
        description = Config.getConfigString("roleDescription_Cupid");
    }

    @Override
    public String toString()
    {
        return Config.getConfigString("roleName_Cupid");
    }
}
