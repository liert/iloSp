package com.github.startzyp.iloSp.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

public class ConfigurationLoader {
    public static Map<String, String> messages = new HashMap<String, String>();
    public static Map<String, MessageFormat> format_cache = new HashMap<String, MessageFormat>(32);

    public static void loadMessages(FileConfiguration cfg) {
        messages.clear();
        for (String s : cfg.getConfigurationSection("Messages").getKeys(false)) {
            messages.put(s, cfg.getString("Messages." + s));
        }
    }

    public static String lang(String key) {
        return messages.get(key);
    }

    public static String format(String key, Object ... args) {
        MessageFormat cache = format_cache.get(key);
        if (cache != null) {
            return cache.format(args);
        }
        cache = new MessageFormat(ConfigurationLoader.lang(key));
        format_cache.put(key, cache);
        return cache.format(args);
    }

    public static <T> T loadYamlConfiguration(Plugin plugin, Class<T> type, boolean autoGen) {
        File dataFolder = plugin.getDataFolder();
        File configFile = new File(dataFolder, "config.yml");
        if (!configFile.exists()) {
            if (autoGen) {
                if (!dataFolder.exists()) {
                    dataFolder.mkdirs();
                }
                plugin.saveResource("config.yml", false);
            } else {
                return null;
            }
        }
        try {
            FileInputStream fin = new FileInputStream(configFile);
            Object object = new Yaml(new CustomClassLoaderConstructor(type.getClassLoader())).loadAs(fin, type);
            fin.close();
            return (T)object;
        }
        catch (IOException iOException) {
            return null;
        }
    }

    public static boolean saveYamlConfiguration(Plugin plugin, Object obj, boolean overWrite) throws IOException {
        File configFile;
        block6: {
            block5: {
                File dataFolder = plugin.getDataFolder();
                configFile = new File(dataFolder, "config.yml");
                if (!configFile.exists()) break block5;
                if (overWrite) {
                    return false;
                }
                break block6;
            }
            if (configFile.createNewFile()) break block6;
            return false;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(configFile);
        fileOutputStream.write(new Yaml(new CustomClassLoaderConstructor(obj.getClass().getClassLoader())).dump(obj).getBytes(StandardCharsets.UTF_8));
        fileOutputStream.close();
        return true;
    }
}
