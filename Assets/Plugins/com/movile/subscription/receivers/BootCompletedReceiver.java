package com.movile.subscription.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import com.movile.subscription.KiwiPlugin;
import com.movile.subscription.utils.LogUtils;
import com.movile.subscription.utils.SharedPreferenceUtils;

import java.util.Calendar;

/**
 * @author Rene Argento (rene.argento@movile.com)
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(context, "[BootCompletedReceiver called]");

        //Verify last time check subscription was called
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        SharedPreferences prefs = SharedPreferenceUtils.getInstance().getPluginsSharedPreferences(context.getApplicationContext());
        long lastSubscriptionCheck = prefs.getLong(SharedPreferenceUtils.PREFS_LAST_SUBSCRIPTION_CHECK, 0);

        if((currentTime - lastSubscriptionCheck) > DateUtils.DAY_IN_MILLIS) {
            LogUtils.i(context, "[Checking subscriptions]");
            KiwiPlugin.instance().checkAndUpdateSubscriptions(context);
        }

        //Start alarm
        calendar.setTimeInMillis(System.currentTimeMillis());

        Intent alarmIntent = new Intent(context, com.movile.subscription.receivers.SubscriptionAlarmReceiver.class);
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmPendingIntent);
    }

}
