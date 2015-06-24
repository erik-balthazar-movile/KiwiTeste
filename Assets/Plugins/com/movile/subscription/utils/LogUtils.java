package com.movile.subscription.utils;

import android.content.Context;
import android.util.Log;

public class LogUtils {

    private static final String DEFAULT_TAG = "UnitySubscription";
    private static final String DEBUG_MODE_RESOURCE = "debug_mode";

    public static void i(Context context, String message) {
        if (context != null && isDebugMode(context)) {
            Log.i(DEFAULT_TAG, message);
        }
    }

    public static void d(Context context, String message) {
        if (context != null && isDebugMode(context)) {
            Log.d(DEFAULT_TAG, message);
        }

        if (context == null) {
            Log.d(DEFAULT_TAG, message);
        }
    }

    public static void e(Context context, String message, Throwable throwable) {
        if (context != null && isDebugMode(context)) {
            Log.e(DEFAULT_TAG, message, throwable);
        }
    }

    private static boolean isDebugMode(Context context) {
        boolean isDebug = false;

        if (context != null && context.getResources() != null) {
            int debugModeResourceId = context.getResources().getIdentifier(DEBUG_MODE_RESOURCE, "bool", context.getPackageName());
            isDebug =  context.getResources().getBoolean(debugModeResourceId);
        }

        return isDebug;
    }

}
