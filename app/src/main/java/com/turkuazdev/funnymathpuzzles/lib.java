package com.turkuazdev.funnymathpuzzles;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class lib {

    public static Context context;

    public static int charAt(int VALUE, int INDEX) {
        return Integer.parseInt(String.valueOf(String.valueOf(VALUE).charAt(INDEX)));
    }

    public static int sumChars(int VALUE) {
        int result = 0;

        while (VALUE != 0) {
            result += VALUE % 10;

            VALUE /= 10;
        }

        return result;
    }


    //region ERROR
    public static void err(int ERR_ID, Exception ERR) {
        try {
            FirebaseCrash.log(ERR_ID + "");
            FirebaseCrash.report(ERR);
            Log.e("ERR_" + ERR_ID, "" + ERR);
        } catch (Exception ex) {
        }
    }

    public static void err(int ERR_ID, Exception ERR, String MESSAGE) {
        try {

            FirebaseCrash.log(ERR_ID + "-" + MESSAGE);
            FirebaseCrash.report(ERR);
            Log.e("ERR_" + ERR_ID, "" + ERR + " - " + MESSAGE);
        } catch (Exception ex) {
        }
    }

    public static void err(int ERR_ID, OutOfMemoryError ERR) {
        try {
            FirebaseCrash.log(ERR_ID + "");
            FirebaseCrash.report(ERR);
            Log.e("ERR_OOM_" + ERR_ID, "" + ERR);
        } catch (Exception ex) {
        }
    }

    public static void err(int ERR_ID, OutOfMemoryError ERR, String MESSAGE) {
        try {
            FirebaseCrash.log(ERR_ID + "-" + MESSAGE);
            FirebaseCrash.report(ERR);
            Log.e("ERR_OOM_" + ERR_ID, "" + ERR);
        } catch (Exception ex) {
        }
    }


    public static void info(int INFO_ID, String MESSAGE) {
        try {
            FirebaseCrash.log(INFO_ID + "-" + MESSAGE);
            Log.e("INFO_" + INFO_ID, MESSAGE + "");
        } catch (Exception ex) {
        }
    }
    //endregion

    //region SETTINGS [6, 9]
    //https://developer.android.com/guide/topics/data/data-storage.html#pref
    public static int settings(String TAG, int DEFAULT) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getInt(TAG, DEFAULT);
        } catch (Exception ex) {
            err(6, ex);
            return DEFAULT;
        }
    }

    public static String settings(String TAG, String DEFAULT) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString(TAG, DEFAULT);
        } catch (Exception ex) {
            err(7, ex);
            return DEFAULT;
        }
    }

    public static boolean setSettings(String TAG, int VALUE) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt(TAG, VALUE);
            editor.commit();

            return true;
        } catch (Exception ex) {
            err(8, ex);
            return false;
        }
    }

    public static boolean setSettings(String TAG, String VALUE) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString(TAG, VALUE);
            editor.commit();

            return true;
        } catch (Exception ex) {
            err(9, ex);
            return false;
        }
    }
    //endregion {
}
