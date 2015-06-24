package com.movile.subscription;

import android.content.Context;
import android.content.res.Resources;
import com.google.gson.Gson;
import com.movile.hermes.billing.*;
import com.movile.hermes.sdk.impl.controller.BillingIabHelperController;
import com.movile.subscription.utils.LogUtils;
import com.movile.subscription.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapted from PlayKids project
 */

public class GoogleIABPlugin {

    private static GoogleIABPlugin instance;
    public Context context;
    private IabHelper.OnIabSetupFinishedListener onIabSetupFinishedListener;
    private IabHelper.OnIabPurchaseFinishedListener onIabPurchaseFinishedListener;
    private static final String GOOGLE_PLAY_LICENSE_KEY_RESOURCE = "google_play_license_key";

    private String mSku;

    private String mListener;

    private GoogleIABPlugin() {
        onIabSetupFinishedListener = new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Utils.SendMessageToUnity("SubscriptionManager", "GooglePlayBillingNotSupported", String.valueOf(result.getResponse()));
                }
            }
        };
        onIabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                if (result.isSuccess()) {
                    //Add subscription
                    KiwiPlugin.instance().addSubscription(mSku);

                    Utils.SendMessageToUnity(mListener, "PurchaseProductSuccess", new Gson().toJson(purchase));
                } else {
                    Utils.SendMessageToUnity(mListener, "PurchaseProductFailure", String.valueOf(result.getResponse()));
                }
            }
        };
    }

    public static GoogleIABPlugin instance() {
        if (instance == null)
            instance = new GoogleIABPlugin();
        return instance;
    }

    public void init(boolean shouldEnableLogging) {
        Resources resources = context.getResources();
        int googlePlayLicenseKeyResourceId = resources.getIdentifier(GOOGLE_PLAY_LICENSE_KEY_RESOURCE, "string", context.getPackageName());

        PluginInitializer.getHermesSDK().billingStartSetup(context, context.getString(googlePlayLicenseKeyResourceId), onIabSetupFinishedListener);

        if (shouldEnableLogging) {
            PluginInitializer.getHermesSDK().billingEnableDebugLogging(shouldEnableLogging, "Unity");
        }
    }

    public void purchaseProduct(String sku, String developerPayload, String listener) {
        mSku = sku;
        mListener = listener;
        //TODO ACTIVITY
       // PluginInitializer.getHermesSDK().billingLaunchPurchaseFlow(context, sku, BillingIabHelperController.RC_GOOGLE_PLAY_REQUEST, onIabPurchaseFinishedListener,
           //     developerPayload, null);
    }

    public void restoreGooglePlaySubscription(String skus, String listener) {
        restoreGooglePlaySubscription(skus.split(","), listener);
    }

    public void restoreGooglePlaySubscription(final String[] skus, final String listener) {
        PluginInitializer.getHermesSDK().billingQueryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isSuccess()) {
                    @SuppressWarnings("rawtypes")
                    Map<String, List> map = new HashMap<String, List>();
                    List<Purchase> purchases = new ArrayList<Purchase>();
                    List<SkuDetails> skusResult = new ArrayList<SkuDetails>();

                    List<String> skuSubscriptions = new ArrayList<String>(1);

                    for (String sku : skus) {
                        if (inventory.hasPurchase(sku)) {
                            purchases.add(inventory.getPurchase(sku));

                            //Add subscription
                            KiwiPlugin.instance().addSubscription(sku);

                            skuSubscriptions.add(sku);
                        }

                        if (inventory.hasDetails(sku)) {
                            skusResult.add(inventory.getSkuDetails(sku));
                        }
                    }
                    map.put("purchases", purchases);
                    map.put("skus", skusResult);

                    Utils.SendMessageToUnity(listener, "RestoreStoreSubscriptionSuccess", formatSkusForUnity(skuSubscriptions));
                } else {
                    Utils.SendMessageToUnity(listener, "RestoreStoreSubscriptionFailure", String.valueOf(result.getResponse()));
                }
            }
        });
    }

    private String formatSkusForUnity(List<String> skus) {
        StringBuilder skuList = new StringBuilder();

        for (int i = 0; i < skuList.length(); i++) {
            if (i > 0) {
                skuList.append(",");
            }
            skuList.append(skus.get(i));
        }

        return skuList.toString();
    }

}
