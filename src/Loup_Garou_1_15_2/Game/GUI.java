package Loup_Garou_1_15_2.Game;

import Loup_Garou_1_15_2.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GUI
{
    public class Item extends SimpleEventEmitter
    {
        private class InitialData
        {
            public Material material;
            public String displayName;
            public String localizedName;
            public int amount;
            Consumer<Item> consumer;
        }

        InitialData initialData = new InitialData();

        public ItemStack itemStack;

        public Item(Material material, String displayName, String localizedName, int amount, Consumer<Item> consumer)
        {
            itemStack = new ItemStack(material, amount);
            setNames(displayName, localizedName);

            initialData.material = material;
            initialData.displayName = displayName;
            initialData.localizedName = localizedName;
            initialData.amount = amount;
            initialData.consumer = consumer;
        }

        public void refresh()
        {
            itemStack = new ItemStack(initialData.material, initialData.amount);
            setNames(initialData.displayName, initialData.localizedName);
        }

        public void addLore(String... lores)
        {
            ItemMeta meta = itemStack.getItemMeta();

            // si il n'y a pas de lore existant on crée une nouvelle list
            List<String> lore = (meta.getLore() == null) ? new ArrayList<>() : meta.getLore() ;

            for(String s : lores)
            {
                lore.add(s);
            }

            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        public void clearLore()
        {
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = new ArrayList<>();
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
        public void setNames(String display, String localized)
        {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(display);
            meta.setLocalizedName(localized);
            itemStack.setItemMeta(meta);
        }
        public void setDisplayName(String display)
        {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(display);
            itemStack.setItemMeta(meta);
        }

        public void setLocalizedName(String localized)
        {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setLocalizedName(localized);
            itemStack.setItemMeta(meta);
        }
    }

    private List<Item> items = new ArrayList<>();
    public Inventory inventory;

    private Player player;
    public void setPlayer(Player player) { this.player = player; }

    private Runnable builder;
    public void setBuilder(Runnable builder)
    {
        this.builder = builder;
        this.builder.run();
    }

    public GUI(InventoryType type) { Bukkit.createInventory(null, type, ""); }
    public GUI(InventoryType type, String title) { Bukkit.createInventory(null, type, title); }
    public GUI(InventoryType type, String title, Player player)
    {
        this.player = player;
        inventory = Bukkit.createInventory(null, type, title);
    }

    public GUI(int size) { inventory = Bukkit.createInventory(null, size, ""); }
    public GUI(int size, String title) { inventory = Bukkit.createInventory(null, size, title); }
    public GUI(int size, String title, Player player)
    {
        this.player = player;
        inventory = Bukkit.createInventory(null, size, title);
    }

    public Item addItem(@NotNull Material material, Consumer<Item> callback)
    { return addItem(material, "", "", 1, -1,callback); }

    public Item addItem(@NotNull Material material, @NotNull int amount, Consumer<Item> callback)
    { return addItem(material, "", "", amount, -1, callback); }

    public Item addItem(@NotNull Material material, @NotNull String displayName, Consumer<Item> callback)
    { return addItem(material, displayName, "", 1, -1, callback); }

    public Item addItem(@NotNull Material material, @NotNull String displayName, @NotNull String localizedName)
    { return addItem(material, displayName, localizedName, 1, -1, null); }

    public Item addItem(@NotNull Material material, @NotNull String displayName, @NotNull String localizedName, Consumer<Item> callback)
    { return addItem(material, displayName, localizedName, 1, -1, callback); }

    public Item addItem(@NotNull Material material, @NotNull int inventoryIndex, @NotNull String displayName, @NotNull String localizedName)
    { return addItem(material, displayName, localizedName, 1, inventoryIndex, null); }

    public Item addItem(@NotNull Material material, @NotNull int inventoryIndex, @NotNull String displayName, @NotNull String localizedName, Consumer<Item> callback)
    { return addItem(material, displayName, localizedName, 1, inventoryIndex, callback); }


    public Item addItem(@NotNull Material material,
                        @NotNull String displayName,
                        @NotNull String localizedName,
                        @NotNull int amount,
                        int inventoryIndex,
                        Consumer<Item> callback)
    {
        Item item = new Item(material, displayName, localizedName, amount, callback);

        if(inventory == null)
        {
            Tools.consoleLogError("Add item : no inventory");
            return null;
        }

        if(callback != null)
        {
            callback.accept(item);
        }

        // TODO : gerer la position de l'item dans l'inventaire
        if(inventoryIndex != -1)
        {
            items.set(inventoryIndex, item);
            inventory.setItem(inventoryIndex, item.itemStack);
        }
        else
        {
            items.add(item);
            inventory.addItem(item.itemStack);
        }

        return item;
    }


    public Item getItem(int index) { return items.get(index); }
    public Item getItem(String itemName)
    {
        for (GUI.Item item : items)
        {
            if(item.itemStack.getItemMeta().getDisplayName().equals(itemName))
            {
                return item;
            }
        }

        return null;
    }
    public Item getItem(String itemName, String actionName)
    {
        for (GUI.Item item : items)
        {
            if(item.itemStack.getItemMeta().getDisplayName().equals(itemName)
            && item.itemStack.getItemMeta().getLocalizedName().equals(actionName))
            {
                return item;
            }
        }

        return null;
    }

    public void clearItems() { items.clear(); }

    public void open() { open(0, this.player); }
    public void open(int delay) { open(delay, this.player); }
    public void open(Player player) { open(0, player); }

    public void open(int delay, Player player)
    {
        if(inventory == null)
        {
            Tools.consoleLogError("openInventory : inventory is null");
            return;
        }

        if(player == null)
        {
            Tools.consoleLogError("openInventory : player is null");
            return;
        }

        player.openInventory(inventory);

        // TODO : delay async event
        // piste : utiliser le scheduler de l'instance de BUKKIT
        // bug : on ne peut pas lancer la methode async .openInventory() dans une autre thread
        //


        /*final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        final Runnable test = () ->
        {
            player.sendMessage("Debug : " + player.toString());

            // openInventory ne s'execute pas dans une autre thread async
            player.openInventory(inventory);
        };

        executor.schedule(test, delay, TimeUnit.SECONDS);*/

        /*new java.util.Timer().schedule (

                new java.util.TimerTask()
                {
                    @Override
                    public void run()
                    {
                        player.sendMessage("Debug : " + player.toString());
                        player.openInventory(inventory);
                    }
                },
                0
        );*/
    }

    public void close()
    {
        close(player);
    }
    public void close(Player player)
    {
        if(player != null)
        {
            player.closeInventory();
        }
    }

    public void refresh()
    {
        inventory.clear();

        if(builder == null)
        {
            for (Item item : items)
            {
                if(item.initialData.consumer != null)
                {
                    item.initialData.consumer.accept(item);
                }

                inventory.addItem(item.itemStack);
            }
        }
        else
        {
            builder.run();
        }
    }
}