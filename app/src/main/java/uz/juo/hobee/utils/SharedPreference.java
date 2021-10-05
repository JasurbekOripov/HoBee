package uz.juo.hobee.utils;

import android.content.Context;
import android.content.SharedPreferences;

import uz.juo.hobee.models.Location;

public class SharedPreference {
    SharedPreferences prefs;
    private static SharedPreference sharePreference;
    SharedPreferences.Editor editor;

    public static SharedPreference getInstance(Context context) {
        if (sharePreference != null)
            return sharePreference;
        else return sharePreference = new SharedPreference(context);
    }

    private SharedPreference(Context context) {
        prefs = context.getSharedPreferences(getClass().getName(), Context.MODE_PRIVATE);
    }

    public void setLang(String lang) {
        editor = prefs.edit();
        editor.putString("lang", lang);
        editor.apply();
    }

    public String getLang() {
        return prefs.getString("lang", "1");
    }

    public void setTarget(boolean target) {
        editor = prefs.edit();
        editor.putBoolean("target", target);
        editor.apply();
    }

    public void setLocation(String lat, String longt, String name) {
        editor = prefs.edit();
        editor.putString("lat", lat);
        editor.putString("long", longt);
        editor.putString("name", name);
        editor.apply();
    }

    public Location getLocation() {
        return new Location(prefs.getString("lat", "en"), prefs.getString("long", "en"), prefs.getString("name", "Location"));
    }

    public void setHasLocation(boolean hasLang) {
        editor = prefs.edit();
        editor.putBoolean("hasLocation", hasLang);
        editor.apply();
    }

    public boolean getHasLocation() {
        return prefs.getBoolean("hasLocation", false);
    }

    public boolean getTarget() {
        return prefs.getBoolean("target", false);
    }

    public void setHasLang(boolean hasLang) {
        editor = prefs.edit();
        editor.putBoolean("hasLang", hasLang);
        editor.apply();
    }

    public boolean getHaslang() {
        return prefs.getBoolean("hasLang", false);
    }
//    public void setHasApikeyMap(boolean hasLang) {
//        editor = prefs.edit();
//        editor.putBoolean("keymap", hasLang);
//        editor.apply();
//    }
//
//    public boolean getHasApikeyMap() {
//        return prefs.getBoolean("keymap", false);
//    }

    public void setBranchId(String id) {
        editor = prefs.edit();
        editor.putString("branchid", id);
        editor.apply();
    }

    public String getBranchId() {
        return prefs.getString("branchid", "1");
    }
}
