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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
        int clickIndex = e.getRawSlot();
        Player p = (Player)e.getWhoClicked();
        if (!(e.getWhoClicked() instanceof Player) || !inv.getTitle().startsWith(Settings.I.Title) || clickIndex > 9 ) {
            return;
        }
        if (PackData.indexs.contains(clickIndex)) {
            e.setCancelled(true);
        }
        ItemStack item = e.getCursor();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (meta.hasLore()) {
            String s = meta.getLore().toString();
            if (s.contains(Settings.I.iloSpLore)) {
                String msg = Settings.I.Msg.replace("$item", "%s");
                msg = String.format(msg, meta.getDisplayName());
                p.sendMessage(msg);
            }
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
