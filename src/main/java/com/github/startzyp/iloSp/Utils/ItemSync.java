package com.github.startzyp.iloSp.Utils;

import com.github.startzyp.iloSp.Data.PackData;
import com.github.startzyp.iloSp.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemSync {
    private static final ItemUtil util = ItemUtil.DEFAULT;

    public static ItemStack[] getStringData(JsonArray value) {
        return ItemSync.arrayToStacks(value.get(0).getAsJsonArray());
    }

    public static String getDataString(PackData pd) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        ItemStack[] inventory = new ItemStack[pd.getItems().size()];
        inventory = pd.getItems().toArray(inventory);
        builder.append(ItemSync.getString(inventory));
        builder.append("]");
        return builder.toString();
    }

    private static ItemStack[] arrayToStacks(JsonArray array) {
        ArrayList<ItemStack> stackList = new ArrayList<ItemStack>();
        for (JsonElement element : array) {
            if (element.isJsonNull()) {
                stackList.add(new ItemStack(Material.AIR));
                continue;
            }
            stackList.add((ItemStack)util.getItemStack(element.getAsString()));
        }
        return stackList.toArray(new ItemStack[array.size()]);
    }

    private static String getString(ItemStack[] stacks) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        while (i < stacks.length) {
            ItemStack stack;
            if (i > 0) {
                builder.append(",");
            }
            if ((stack = stacks[i]) != null && stack.getType() != Material.AIR) {
                Object copy = Main.nbtHandler.asCraftCopy(stack);
                builder.append("\"");
                builder.append(util.getString(copy));
                builder.append("\"");
            } else {
                builder.append("null");
            }
            ++i;
        }
        builder.append("]");
        return builder.toString();
    }
}
