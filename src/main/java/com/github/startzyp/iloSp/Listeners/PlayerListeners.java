package com.github.startzyp.iloSp.Listeners;

import com.github.startzyp.iloSp.Config.Settings;
import com.github.startzyp.iloSp.Data.PackData;
import com.github.startzyp.iloSp.Main;
import com.github.startzyp.iloSp.SQL.SQLQuery;
import com.mchim.ItemLoreOrigin.Event.ItemLoreDamageEvent;
import com.mchim.ItemLoreOrigin.Event.ItemLoreStatusEvent;
import com.mchim.ItemLoreOrigin.Event.ItemLoreTickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

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
        if (!(e.getWhoClicked() instanceof Player) || !inv.getTitle().startsWith(Settings.I.Title)) {
            return;
        }
        if (PackData.indexs.contains(clickIndex)) {
            e.setCancelled(true);
        }
        PackData.SavePackData(p, inv);
        main.Update(p);
        ItemStack item = null;
        ItemMeta meta;
        if (clickIndex > 9) {
            ClickType clickType = e.getClick();
            ItemStack itemStack = e.getCurrentItem();
//            p.sendMessage(itemStack.toString() + clickType.isShiftClick());
            meta = itemStack.getItemMeta();
            if (meta == null) return;
            if (itemStack.getAmount() > 1 && clickType.isShiftClick()) {
                p.sendMessage(this.format(Settings.I.LimitMsg, meta));
                e.setCancelled(true);
            }else if (clickType.isShiftClick()){
                item = itemStack;
            }
            if (item != itemStack) return;
        } else {
            InventoryAction action = e.getAction();
            if (action.equals(InventoryAction.PICKUP_ALL)) return;
            item = e.getCursor().getAmount() == 0 ? e.getCurrentItem() : e.getCursor();
            meta = item.getItemMeta();
            if (meta == null) {
                e.setCancelled(true);
                return;
            }
            if (item.getAmount() > 1 && (
                    action.equals(InventoryAction.PLACE_ALL) ||
                            action.equals(InventoryAction.PLACE_SOME))) {
                p.sendMessage(this.format(Settings.I.LimitMsg, meta));
                e.setCancelled(true);
                return;
            }
        }
        ItemStack copyItem = new ItemStack(item);
        copyItem.setAmount(1);
        List<ItemStack> items = SQLQuery.getPlayerItem(p);
        if (items.contains(copyItem)) {
            e.setCancelled(true);
            p.sendMessage(this.format(Settings.I.ActivateMsg, meta));
            return;
        }
        if (meta.hasLore()) {
            String s = meta.getLore().toString();
            if (s.contains(Settings.I.iloSpLore)) {
                p.sendMessage(this.format(Settings.I.Msg, meta));
            } else {
                e.setCancelled(true);
            }
        }
    }

    public String format(String text, ItemMeta itemMeta) {
        String msg = text.replace("$item", "%s");
        msg = String.format(msg, itemMeta.getDisplayName());
        return msg;
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
