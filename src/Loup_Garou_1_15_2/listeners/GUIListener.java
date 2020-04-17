package Loup_Garou_1_15_2.listeners;

import Loup_Garou_1_15_2.Game.GUI;
import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class GUIListener implements Listener
{
    public GUIListener(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerClickGUI(InventoryClickEvent event)
    {
        if(event.getCurrentItem() == null)
        {
            return;
        }

        ItemStack itemStack = event.getCurrentItem();

        for(GUI gui : GUI.registeredGUI)
        {
            GUI.Item item = gui.getItem(itemStack.getItemMeta().getDisplayName(), itemStack.getItemMeta().getLocalizedName());

            if(item != null)
            {
                item.emit("onClick", event);
            }
        }
    }
}
