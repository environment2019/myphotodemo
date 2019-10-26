package com.example.myphoto.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class SPPhotoAlbum {

    private static String SP_FILE_PHOTO_ALBUM = "SP_FILE_PHOTO_ALBUM";
    private static String SP_KEY_PHOTO = "SP_KEY_PHOTO";

    /**
     * Serialize photo list object
     *
     * @param listPhoto
     * @return
     * @throws IOException
     */
    public static String serialize(List<PhotoRecord> listPhoto) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                byteArrayOutputStream);
        objectOutputStream.writeObject(listPhoto);
        String serStr = byteArrayOutputStream.toString("ISO-8859-1");
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
        objectOutputStream.close();
        byteArrayOutputStream.close();

        return serStr;
    }

    /**
     * deSerialization for object
     *
     * @param str
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object deSerialization(String str) throws IOException,
            ClassNotFoundException {
        String redStr = java.net.URLDecoder.decode(str, "UTF-8");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                redStr.getBytes("ISO-8859-1"));
        ObjectInputStream objectInputStream = new ObjectInputStream(
                byteArrayInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        byteArrayInputStream.close();

        return object;
    }

    public static void saveObject(Context context, String strObject) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_PHOTO_ALBUM, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(SP_KEY_PHOTO, strObject);
        edit.apply();
    }

    public static String readObject(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_PHOTO_ALBUM, Context.MODE_PRIVATE);
        return sp.getString(SP_KEY_PHOTO, null);
    }

}
