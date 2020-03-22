package Loup_Garou_1_15_2.Game;

import org.bukkit.Location;

public class House
{
    public Location location;
    public LgPlayer lgPlayer = null;

    public House(Location location)
    {
        this.location = location;
    }

    public void tpTo()
    {
        lgPlayer.player.teleport(location);
    }
}
