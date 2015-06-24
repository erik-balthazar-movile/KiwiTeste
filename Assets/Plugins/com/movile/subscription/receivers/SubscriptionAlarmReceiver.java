package com.movile.subscription.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.movile.subscription.KiwiPlugin;
import com.movile.subscription.utils.LogUtils;

/**
 * @author Rene Argento (rene.argento@movile.com)
 */
public class SubscriptionAlarmReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.i(context, "[SubscriptionAlarmReceiver called]");
        LogUtils.i(context, "[Synchronizing and checking subscriptions]");

        KiwiPlugin.instance().syncSubscriptions(context);
        KiwiPlugin.instance().checkAndUpdateSubscriptions(context);
    }
}
