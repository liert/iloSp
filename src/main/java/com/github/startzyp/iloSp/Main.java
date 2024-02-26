package com.github.startzyp.iloSp;

import com.github.startzyp.iloSp.Commands.OnCommands;
import com.github.startzyp.iloSp.Config.ConfigurationLoader;
import com.github.startzyp.iloSp.Config.Settings;
import com.github.startzyp.iloSp.Listeners.PlayerListeners;
import com.github.startzyp.iloSp.SQL.SQLQuery;
import com.mchim.ItemLoreOrigin.ItemLoreData.ItemLoreManager;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.startzyp.iloSp.Utils.NbtHandler;

public class Main extends JavaPlugin implements Listener {
   public static Main main;
   public static NbtHandler nbtHandler;
   public static boolean startFail;
   List<ItemStack> items = new ArrayList<>();
   private String info = "";

   public void onEnable() {
      main = this;
      nbtHandler = new NbtHandler();
      if (startFail) {
         this.getLogger().warning("饰品启动失败...");
         return;
      }
      ConfigurationLoader.loadYamlConfiguration(this, Settings.class, true);
      SQLQuery.createTable();
      File config = new File(this.getDataFolder() + File.separator + "config.yml");
      if (!config.exists()) {
         this.getConfig().options().copyDefaults(true);
      }
      this.saveDefaultConfig();
      this.ReloadConfig();
      Bukkit.getPluginCommand("ils").setExecutor(new OnCommands());
      Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
   }

   private void ReloadConfig() {
      this.reloadConfig();
      this.info = this.getConfig().getString("iloSpLore");
   }

   public void onDisable() {
      super.onDisable();
   }

   public void Update(Player p){
      List<ItemStack> items = SQLQuery.getPlayerItem(p);
      for (ItemStack item : items) {
         item.setAmount(1);
      }
      this.items = items.stream().distinct().collect(Collectors.toList());
   }

   public void AddiloSp(Player p, List<ItemLoreManager> list) {
      if (list != null) {
         for (ItemStack item : this.items) {
            if (item != null && item.hasItemMeta()) {
               ItemMeta meta = item.getItemMeta();
               if (meta.hasLore()) {
                  String s = meta.getLore().toString();
                  if (s.contains(Settings.I.iloSpLore)) {
                     ItemLoreManager ilm = new ItemLoreManager(p);
                     ilm.initLoreData(item);
                     list.add(ilm);
                  }
               }
            }
         }
      }
   }
}
