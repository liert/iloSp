package com.github.startzyp.iloSp.Data;

import com.github.startzyp.iloSp.Config.Settings;
import com.github.startzyp.iloSp.SQL.SQLQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PackData {
    private static final Map<UUID, PackData> packMap = new HashMap<>();
    private final int slot;
    private List<ItemStack> items;

    public PackData(int slot, List<ItemStack> items) {
        this.slot = slot;
        this.items = items;
    }

    public void openInv(Player p) {
        int max = this.slot;
        String title = Settings.I.Title;
        Inventory inv = Bukkit.createInventory(null, max, title);
        int i = 0;
        while (i < this.items.size()) {
            if (i >= max) break;
            if (this.items.get(i) != null) {
                inv.setItem(i, this.items.get(i));
            }
            ++i;
        }
        p.openInventory(inv);
    }

    public void closeInv(Inventory inv) {
        int i = 0;
        while (i < inv.getSize()) {
            if (this.items.size() >= i + 1) {
                this.items.set(i, inv.getItem(i));
            } else {
                this.items.add(inv.getItem(i));
            }
            ++i;
        }
    }

    public static PackData getPackData(Player p) {
        PackData pd = SQLQuery.getPlayerData(p);
        packMap.put(p.getUniqueId(), pd);
        return pd;
    }

    public static void SavePackData(Player p, Inventory inv) {
        if (!packMap.containsKey(p.getUniqueId())) {
            return;
        }
        PackData pd = packMap.get(p.getUniqueId());
        pd.closeInv(inv);
        SQLQuery.savePlayerData(p, pd);
    }

    public List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PackData)) {
            return false;
        }
        PackData other = (PackData)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<ItemStack> this$items = this.getItems();
        List<ItemStack> other$items = other.getItems();
        return !(this$items == null ? other$items != null : !((Object)this$items).equals(other$items));
    }

    protected boolean canEqual(Object other) {
        return other instanceof PackData;
    }

    public int hashCode() {
        int result = 1;
        List<ItemStack> $items = this.getItems();
        result = result * 59 + ($items == null ? 0 : ((Object)$items).hashCode());
        return result;
    }

    public String toString() {
        return "PackData(slot=" + this.slot + ", items=" + this.getItems() + ")";
    }
}
