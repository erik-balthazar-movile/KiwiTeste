package com.movile.subscription;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.movile.kiwi.sdk.api.KiwiSDK;
import com.movile.kiwi.sdk.api.KiwiSDKBuilder;
import com.movile.kiwi.sdk.api.model.*;
import com.movile.subscription.utils.LogUtils;
import com.movile.subscription.utils.SharedPreferenceUtils;
import com.movile.subscription.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Adapted from PlayKids project
 */
public class KiwiPlugin {

    private KiwiSDK mKiwiSDK = null;
    public Context context;
    private static KiwiPlugin instance;

    private Set<String> mSubscriptions = new HashSet<String>(1);

    public static final String SUBSCRIPTIONS = "subscriptions";

    public KiwiSDK getKiwiSDK() {
        return mKiwiSDK;
    }

    public static KiwiPlugin instance() {
        if (instance == null) {
            instance = new KiwiPlugin();
        }
        return instance;
    }

    public void initSDK(Context context) {
        if (mKiwiSDK == null) {
            mKiwiSDK = KiwiSDKBuilder.create().withContext(context).build();
        }

        if (mKiwiSDK == null) {
            LogUtils.d(null, "Error initializing Kiwi SDK");
        }
    }

    public void startSession(Activity activity) {
        if (mKiwiSDK != null) {
            mKiwiSDK.analytics().startSession(activity);
        }
    }

    public void stopSession(Activity activity) {
        if (mKiwiSDK != null) {
            mKiwiSDK.analytics().stopSession(activity);
        }
    }

    public void logEvent(String event, String parameterId, String parameterValue) {
        if (mKiwiSDK != null) {
            mKiwiSDK.analytics().track(Event.create(event).addToPayload(parameterId, parameterValue));
        }
    }

    public void logEvent(String eventId, String[] parameterValue) {
        if (mKiwiSDK != null) {

            try {
                Event newEvent = Event.create(eventId);
                for (int i = 0; i < parameterValue.length; i += 2) {
                    newEvent.addToPayload(parameterValue[i], parameterValue[i + 1]);
                }
                mKiwiSDK.analytics().track(newEvent);
            }
            catch (Exception e){
                LogUtils.e(context, "[KiwiPlugin] logEvent error", e);
            }
        }
    }

    public void logEvent(String eventId, HashMap<String, String> map) {
        if (mKiwiSDK != null) {
            if (StringUtils.isNotBlank(eventId) && map != null) {
                try {
                    Event newEvent = Event.create(eventId);
                    for (String key : map.keySet()) {
                        newEvent.addToPayload(key, map.get(key));
                    }
                    mKiwiSDK.analytics().track(newEvent);
                }
                catch (Exception e){
                    LogUtils.e(context, "[KiwiPlugin] logEvent error", e);
                }
            }
        }
    }

    public void setHermesSKU(String sku){
        SharedPreferences prefs = SharedPreferenceUtils.getInstance().getPluginsSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor edit = prefs.edit();

        edit.putString(CarrierIABPlugin.PREFS_CARRIER_SKU, sku);
        LogUtils.i(context, "[KiwiManager save current sku: ]"+sku);

        edit.apply();
    }

    public void checkAndUpdateSubscriptions(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(context, "[checkAndUpdateSubscriptions]");

                //Optimization to first check shared preferences
                String skusJson = SharedPreferenceUtils.getInstance()
                        .getPluginsSharedPreferences(context.getApplicationContext()).getString(SUBSCRIPTIONS, null);
                if(skusJson != null) {
                    Gson gson = new Gson();
                    mSubscriptions = gson.fromJson(skusJson, new TypeToken<Set<String>>() {}.getType());
                }

                long seconds = 25;

                for (long i = 0l; !mKiwiSDK.user().isInstalled() && i < seconds; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }

                if(!mKiwiSDK.user().isInstalled()) {
                    return;
                }

                try {
                    UserInfo userInfo = mKiwiSDK.user().retrieveLocalInformation().get();

                    if (userInfo != null) {
                        mKiwiSDK.subscription().fetchSubscriptions(new FetchSubscriptionsListener() {
                            @Override
                            public void onSuccess(Set<Subscription> subscriptions) {
                                LogUtils.i(context, "[checkAndUpdateSubscriptions] - onSuccess");

                                LogUtils.i(context, "[checkAndUpdateSubscriptions] - Number of subscriptions: " + subscriptions.size());

                                Set<String> skuSet = new HashSet<String>(1);
                                for (Subscription subscription : subscriptions) {
                                    LogUtils.i(context, "Subscription status: " + subscription.getSubscriptionStatus());
                                    if (subscription.getSubscriptionStatus() == SubscriptionStatus.ACTIVE
                                            || subscription.getSubscriptionStatus() == SubscriptionStatus.TRIAL) {
                                        skuSet.add(subscription.getSku());
                                    }
                                }

                                updateSubscriptions(context, skuSet);
                            }

                            @Override
                            public void onError() {
                                LogUtils.i(context, "[checkAndUpdateSubscriptions] - onError");
                            }
                        });
                    }
                } catch (Exception e) {
                    LogUtils.e(context, "Error on checkAndUpdateSubscriptions", e);
                }
            }
        }).start();
    }

    public void syncSubscriptions(final Context context) {
        LogUtils.d(context, "Sync subscription begin");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Set<Subscription> subscriptions = mKiwiSDK.subscription().getLocalSubscriptions();
                LogUtils.d(context, "Subscription set size: " + subscriptions.size());

                for (final Subscription s : subscriptions) {
                    LogUtils.d(context, "Starting sync for SKU: " + s.getSku());

                    mKiwiSDK.subscription().syncSubscription(s, new SyncSubscriptionListener() {
                        @Override
                        public void onSuccess(final Subscription subscription) {
                            LogUtils.d(context, "Sync success for SKU: " + s.getSku());
                        }

                        @Override
                        public void onNetworkUnavailable() {
                        }

                        @Override
                        public void onError() {
                            LogUtils.d(context, "Sync error for SKU: " + s.getSku());
                        }
                    });
                }
            }
        }).start();
        LogUtils.d(context, "Sync subscription end");
    }

    private void updateSubscriptions(Context context, Set<String> skus) {
        if (skus != null) {
            //update shared preferences
            if (context != null) {
                updateSharedPreferencesSubscriptions(skus);
            }

            //update memory variable
            mSubscriptions = skus;

            updateSubscriptionsCheckTime(context);
        }
    }

    private void updateSubscriptionsCheckTime(Context context) {
        //Set time of checking
        Calendar calendar = Calendar.getInstance();
        SharedPreferences prefs = SharedPreferenceUtils.getInstance().getPluginsSharedPreferences(context.getApplicationContext());
        prefs.edit().putLong(SharedPreferenceUtils.PREFS_LAST_SUBSCRIPTION_CHECK, calendar.getTimeInMillis()).apply();
    }

    private void updateSharedPreferencesSubscriptions(Set<String> skus){
        Gson gson = new Gson();
        String skuSetJson = gson.toJson(skus);
        SharedPreferenceUtils.getInstance().getPluginsSharedPreferences(context.getApplicationContext()).edit()
                .putString(SUBSCRIPTIONS, skuSetJson).apply();
    }

    public String getSubscriptions() {
        StringBuilder skuList = new StringBuilder();

        int i = 0;
        for (String sku : mSubscriptions) {
            if (i > 0) {
                skuList.append(",");
            }
            skuList.append(sku);
            i++;
        }

        return skuList.toString();
    }

    public boolean checkActivateSubscription(String sku) {
        //LogUtils.d(context, "Checking subscription " + sku + ". Contains: " + mSubscriptions.contains(sku));
        return mSubscriptions.contains(sku);
    }

    public void addSubscription(String sku) {
        //update shared preferences
        String skusJson = SharedPreferenceUtils.getInstance()
                .getPluginsSharedPreferences(context.getApplicationContext()).getString(SUBSCRIPTIONS, null);

        Set<String> skus;
        if(skusJson != null) {
            Gson gson = new Gson();
            skus = gson.fromJson(skusJson, new TypeToken<Set<String>>() {}.getType());
        } else {
            skus = new HashSet<String>();
        }
        skus.add(sku);

        updateSharedPreferencesSubscriptions(skus);

        LogUtils.d(context, "Adding subscription " + sku);
        //update memory variable
        mSubscriptions.add(sku);
    }

}
