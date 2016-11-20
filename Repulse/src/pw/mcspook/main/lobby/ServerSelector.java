package pw.mcspook.main.lobby;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mason.main.utils.BukkitUtils;

import pw.mcspook.main.builders.ItemStackBuilder;
import pw.mcspook.main.managers.PlayerEvents;

public class ServerSelector implements Listener
{
    static List<String> serverSelectorLore = new ArrayList<>();
    static List<String> hardcoreFactionsLore = new ArrayList<>();
    static List<String> developmentLore = new ArrayList<>();

    static {
        serverSelectorLore.add(ChatColor.translateAlternateColorCodes('&', "&6&lRIGHT CLICK &eto play a server"));
        
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------------"));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&eOnline Players &6»"));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', ""));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&eMap Kit &6» &fProtection 1 Sharpness 1"));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&eFaction Limits &6» &f30 man 1 0 Ally"));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', ""));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&6&lRIGHT CLICK&e to join queue"));
        hardcoreFactionsLore.add(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------------"));
        
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------------"));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&eOnline Players &6»"));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', ""));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&6&lRIGHT CLICK&e to join queue"));
        developmentLore.add(ChatColor.translateAlternateColorCodes('&', "&7&m-------------------------------------------"));
    }

    
    public static ItemStack selector = ItemStackBuilder.get(Material.WATCH, 1, (short)0, "&6Server Selector", serverSelectorLore);
    public static ItemStack factions = ItemStackBuilder.get(Material.CLAY_BALL, 1, (short)0, "&6Hardcore Factions", hardcoreFactionsLore);
    public static ItemStack lobbies = ItemStackBuilder.get(Material.ENDER_PEARL, 1, (short)0, "&6Development", developmentLore);
    public static Inventory inv = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', "&bServer Selector"));

    public ServerSelector()
    {
        inv.setItem(0, factions);
        inv.setItem(8, lobbies);
    }

    @EventHandler
    public void InventoryClick(InventoryClickEvent e)
    {
        e.setCancelled(true);
        if ((!(e.getWhoClicked() instanceof Player)) || (e.getCurrentItem() == null)) {
            return;
        }
        if (e.getInventory().getType().equals(InventoryType.PLAYER)) {
            e.setCancelled(false);
        }
        Player p = (Player)e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if ((item.getType().equals(Material.ENDER_PEARL)) && (e.getSlot() + 1 == item.getAmount()))
        {
            PlayerEvents.sendPlayerToServer(p, "dev" + item.getAmount());
            p.closeInventory();
        }
        else if (item.isSimilar(factions))
        {
            PlayerEvents.sendPlayerToServer(p, "hcf");
            p.closeInventory();
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
        if ((!e.getAction().equals(Action.PHYSICAL)) && (e.getItem() != null) && (e.getItem().isSimilar(selector)))
        {
            Player p = e.getPlayer();
            p.openInventory(inv);
            e.setCancelled(true);
        }
    }
}
