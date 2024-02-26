package com.github.startzyp.iloSp.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.github.startzyp.iloSp.Main;
import org.apache.commons.codec.binary.Base64;

public class ItemUtil {
    public static final ItemUtil DEFAULT = new ItemUtil();
    private final Method cachedNBTTagWrite;
    private final Method cachedNBTTagLoad;
    private final Method cachedItemStackSave;
    private final Field cachedCraftItemHandle;
    private final Base64 base64 = new Base64();

    private ItemUtil() {
        this.cachedNBTTagWrite = this.getNBTTagWriteMethod();
        this.cachedNBTTagLoad = this.getNBTTagLoadMethod();
        this.cachedCraftItemHandle = this.getCraftItemHandle();
        this.cachedItemStackSave = this.getItemStackSave();
    }

    private Method getItemStackSave() {
        try {
            return Main.nbtHandler.getItemClass().getMethod("save", Main.nbtHandler.getNBTTagCompoundClass());
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Field getCraftItemHandle() {
        try {
            Field handle = Main.nbtHandler.getCraftItemStackClass().getDeclaredField("handle");
            handle.setAccessible(true);
            return handle;
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getNBTTagLoadMethod() {
        try {
            Method load = Main.nbtHandler.getNBTTagCompoundClass().getDeclaredMethod("load", DataInput.class, Integer.TYPE, Main.nbtHandler.getNBTReadLimiterClass());
            load.setAccessible(true);
            return load;
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Method getNBTTagWriteMethod() {
        try {
            Method write = Main.nbtHandler.getNBTTagCompoundClass().getDeclaredMethod("write", DataOutput.class);
            write.setAccessible(true);
            return write;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object getItemStack(String string) {
        DataInputStream input = new DataInputStream(new ByteArrayInputStream(this.base64.decode(string)));
        Object tag = Main.nbtHandler.getNBTTagCompoundInstance();
        try {
            this.cachedNBTTagLoad.invoke(tag, input, 0, Main.nbtHandler.getNBTReadLimiter());
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Main.nbtHandler.asCraftMirror(Main.nbtHandler.getItemStack(tag));
    }

    public String getString(Object stack) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Object object = Main.nbtHandler.getNBTTagCompoundInstance();
            this.cachedItemStackSave.invoke(this.cachedCraftItemHandle.get(stack), object);
            this.cachedNBTTagWrite.invoke(object, new DataOutputStream(out));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this.base64.encodeToString(out.toByteArray());
    }
}
