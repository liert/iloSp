package com.github.startzyp.iloSp.Utils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.startzyp.iloSp.Main;
import org.bukkit.inventory.ItemStack;

public class NbtHandler {
    private String version = this.getTureVersion();

    public NbtHandler() {
        if (this.version == null) {
            Main.startFail = true;
        }
    }

    private String getTureVersion() {
        try {
            String pack = "net.minecraft.server.";
            pack = pack.replace('.', '/');
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pack);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                JarFile jar = ((JarURLConnection)url.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (!name.startsWith(pack) || !name.contains(".class")) continue;
                    name = name.replaceAll(pack, "");
                    String[] raws = name.split("/");
                    return raws[0];
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Class<?> getItemClass() {
        try {
            return Class.forName("net.minecraft.server." + this.version + ".ItemStack");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getNBTTagCompoundClass() {
        try {
            return Class.forName("net.minecraft.server." + this.version + ".NBTTagCompound");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getNBTTagCompoundInstance() {
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + this.version + ".NBTTagCompound");
            return clazz.newInstance();
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getNBTReadLimiterClass() {
        try {
            return Class.forName("net.minecraft.server." + this.version + ".NBTReadLimiter");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getNBTReadLimiter() {
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + this.version + ".NBTReadLimiter");
            Field field = clazz.getField("a");
            return field.get(null);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getCraftItemStackClass() {
        try {
            return Class.forName("org.bukkit.craftbukkit." + this.version + ".inventory.CraftItemStack");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object asCraftMirror(Object object) {
        try {
            Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + this.version + ".inventory.CraftItemStack");
            Method method = clazz.getMethod("asCraftMirror", this.getItemClass());
            return method.invoke(null, object);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object asCraftCopy(Object object) {
        try {
            Class<?> clazz = Class.forName("org.bukkit.craftbukkit." + this.version + ".inventory.CraftItemStack");
            Method method = clazz.getMethod("asCraftCopy", ItemStack.class);
            return method.invoke(null, object);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getItemStack(Object object) {
        if (this.isHighVersion()) {
            return this.getNewItemStack(object);
        }
        return this.getCreateItemStack(object);
    }

    private boolean isHighVersion() {
        String[] raws = this.version.split("_");
        String m = raws[1];
        int n = Integer.parseInt(m);
        return n >= 11;
    }

    private Object getNewItemStack(Object object) {
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + this.version + ".ItemStack");
            Constructor<?> cons = clazz.getConstructor(this.getNBTTagCompoundClass());
            return cons.newInstance(object);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object getCreateItemStack(Object object) {
        try {
            Class<?> clazz = Class.forName("net.minecraft.server." + this.version + ".ItemStack");
            Method method = clazz.getMethod("createStack", this.getNBTTagCompoundClass());
            return method.invoke(null, object);
        }
        catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getItemSatckClass() {
        try {
            return Class.forName("net.minecraft.server." + this.version + ".ItemStack");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
