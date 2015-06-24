package com.movile.subscription.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Rene Argento (rene.argento@movile.com)
 */
public class SharedPreferenceUtils {

    public static final String PREFS_FILE_NAME = "plugins";
    public static final String PREFS_LAST_SUBSCRIPTION_CHECK = "last_subscription_check";
    private static SharedPreferenceUtils instance;

    public static SharedPreferenceUtils getInstance() {
        if (instance == null) {
            instance = new SharedPreferenceUtils();
        }
        return instance;
    }

    private SharedPreferenceUtils() {
    }

    public SharedPreferences getPluginsSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }


}
