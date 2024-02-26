package com.github.startzyp.iloSp.Listeners;

import com.github.startzyp.iloSp.Config.Settings;
import com.github.startzyp.iloSp.Data.PackData;
import com.github.startzyp.iloSp.Main;
import com.mchim.ItemLoreOrigin.Event.ItemLoreDamageEvent;
import com.mchim.ItemLoreOrigin.Event.ItemLoreStatusEvent;
import com.mchim.ItemLoreOrigin.Event.ItemLoreTickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners
implements Listener {
    Main main = Main.main;

    @EventHandler
    public void damage(ItemLoreDamageEvent e) {
        if (e.getDamager() != null && e.getDamager() instanceof Player) {
            main.AddiloSp((Player)e.getDamager(), e.getDamagerManager());
        }

    }
    @EventHandler
    public void tick(ItemLoreTickEvent e) {
        main.AddiloSp(e.getEntity(), e.getManager());
    }
    @EventHandler
    public void status(ItemLoreStatusEvent e) {
        main.AddiloSp(e.getEntity(), e.getManager());
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        main.Update(p);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        if (!(e.getWhoClicked() instanceof Player) || !inv.getTitle().startsWith(Settings.I.Title) || e.getRawSlot() > Settings.I.Max) {
            return;
        }
        InventoryAction action = e.getAction();
        Player p = (Player)e.getWhoClicked();
        if (action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_ONE) || action.equals(InventoryAction.PLACE_SOME)) {
//            p.sendMessage("Solt: " + e.getSlot());
//            p.sendMessage("RawSolt: " + e.getRawSlot());
//            ItemStack item = e.getCurrentItem();
            ItemStack item = e.getCursor();
//            ItemStack slotItem = inv.getItem(e.getSlot());
//            ItemStack RawSlotItem = inv.getItem(e.getRawSlot());
//            p.sendMessage("item" + item.toString());
//            p.sendMessage("cursorItem" + cursorItem.toString());
//            p.sendMessage("slotItem" + slotItem.toString());
//            p.sendMessage("RawSlotItem" + RawSlotItem.toString());
            p.sendMessage(item.getData().toString()+ " 激活成功");
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player)e.getPlayer();
        Inventory inv = e.getInventory();
        if (!inv.getTitle().startsWith(Settings.I.Title)) {
            return;
        }
        PackData.SavePackData(p, inv);
        main.Update(p);
    }
}
