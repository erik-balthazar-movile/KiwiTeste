package com.movile.subscription;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.util.TimeUtils;
import com.movile.hermes.sdk.HermesSDKFactory;
import com.movile.hermes.sdk.IHermesSDK;
import com.movile.hermes.sdk.bean.scenario.ScenarioV2;
import com.movile.hermes.sdk.listener.InstallEventListener;
import com.movile.kiwi.sdk.api.model.Subscription;
import com.movile.kiwi.sdk.api.model.SyncSubscriptionListener;
import com.movile.subscription.utils.LogUtils;
import com.movile.subscription.utils.Utils;

import java.util.Properties;
import java.util.Set;

/**
 * Created by Rene Argento
 */
public class PluginInitializer {

    private static PluginInitializer instance;
    private static IHermesSDK hermesSDK = HermesSDKFactory.getHermesSDK();
    private static final String HERMES_APPLICATION_ID_RESOURCE = "hermes_application_id";
    private static final String SENDER_ID_RESOURCE = "sender_id";
    private static final String GOOGLE_PLAY_LICENSE_KEY_RESOURCE = "google_play_license_key";

    public static IHermesSDK getHermesSDK() {
        return hermesSDK;
    }

    public static PluginInitializer instance() {
        LogUtils.d(null, "Getting PluginInitializer instance");
        if (instance == null) {
            instance = new PluginInitializer();
        }
        return instance;
    }

    private PluginInitializer() {

    }

    public void initPlugins(final Context context) {
        LogUtils.d(context, "Initializing plugins");
//        Intent intent = new Intent(context.getApplicationContext(), PluginControllerActivity.class);
//        context.startActivity(intent);

       // LogUtils.d(null, "Initializing Google IAB plugin");
//        GoogleIABPlugin googleIabPlugin = GoogleIABPlugin.instance();
//        googleIabPlugin.activity = context;

        LogUtils.d(context, "Initializing Carrier IAB plugin");
        CarrierIABPlugin carrierIabPlugin = CarrierIABPlugin.instance();
        carrierIabPlugin.context = context;

        LogUtils.d(context, "Initializing Kiwi plugin");
        final KiwiPlugin kiwiPlugin = KiwiPlugin.instance();
        kiwiPlugin.context = context;

        LogUtils.d(context, "Initializing Kiwi SDK");
        kiwiPlugin.initSDK(context);

        //Synchronizing Kiwi subscriptions with SBS
        kiwiPlugin.syncSubscriptions(context);

        InstallEventListener installEventListener = new InstallEventListener() {

            @Override
            public int handleTry(int arg0, String arg1) {
                LogUtils.i(context, "[InstallEventListener - handleTry]");
                return 0;
            }

            @Override
            public void handleSuccess(ScenarioV2 scenario) {
                LogUtils.i(context, "[InstallEventListener - handleSuccess]");

                KiwiPlugin.instance().checkAndUpdateSubscriptions(context.getApplicationContext());
            }

            @Override
            public void handleError(int arg0, String arg1) {
                LogUtils.i(context, "[InstallEventListener - handleError]");
            }
        };

        Properties notificationIconProperty = new Properties();
        notificationIconProperty.setProperty("NOTIFICATION_ICON", String.valueOf(R.drawable.hermes_notification_icon));

        Resources resources = context.getResources();
        int hermesApplicationIdResourceId = resources.getIdentifier(HERMES_APPLICATION_ID_RESOURCE, "string", context.getPackageName());
        int senderIdResourceId = resources.getIdentifier(SENDER_ID_RESOURCE, "string", context.getPackageName());
        int googlePlayLicenseKeyResourceId = resources.getIdentifier(GOOGLE_PLAY_LICENSE_KEY_RESOURCE, "string", context.getPackageName());

        hermesSDK.init(context, notificationIconProperty, context.getString(hermesApplicationIdResourceId), context.getString(senderIdResourceId),
                false, null, null, null, installEventListener, true, context.getString(googlePlayLicenseKeyResourceId));

        KiwiPlugin.instance().checkAndUpdateSubscriptions(context.getApplicationContext());
    }

}
