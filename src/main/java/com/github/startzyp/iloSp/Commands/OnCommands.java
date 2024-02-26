package com.github.startzyp.iloSp.Commands;

import com.github.startzyp.iloSp.Data.PackData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OnCommands implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player p = (Player)sender;
        PackData pd = PackData.getPackData(p);
        pd.openInv(p);
        return true;
    }
}
