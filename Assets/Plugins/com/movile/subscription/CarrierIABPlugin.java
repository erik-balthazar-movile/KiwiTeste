package com.movile.subscription;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import com.google.gson.Gson;
import com.movile.hermes.sdk.bean.response.CarrierSubscriptionCompleteFlowResponse;
import com.movile.hermes.sdk.bean.response.GenericResponse;
import com.movile.hermes.sdk.bean.response.ParsePhoneResponse;
import com.movile.hermes.sdk.enums.CarrierBillingCompleteFlowResultEnum;
import com.movile.subscription.bean.Phone;
import com.movile.subscription.utils.LogUtils;
import com.movile.subscription.utils.SharedPreferenceUtils;
import com.movile.subscription.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * Adapted from PlayKids project
 */

public class CarrierIABPlugin {

    private static CarrierIABPlugin instance;

    public Context context;
    private Observer subscriptionObserver;
    private Observer restoreObserver;

    private String mRestoreListener;
    private String mSubscriptionListener;
    private String mPhone;
    private String mSku;
    private long mCarrierId;

    public static final String PREFS_CARRIER_SKU = "PREFS_CARRIER_SKU";
    private static final String PREFS_CARRIER_PHONE = "PREFS_CARRIER_PHONE";
    private static final String PREFS_CARRIER_RESTORE = "PREFS_CARRIER_RESTORE";

    private CarrierIABPlugin() {
        LogUtils.i(context, "[CarrierIABPlugin] - Start");

        subscriptionObserver = new Observer() {
            @Override
            public void update(Observable observable, Object hermesResponse) {
                final CarrierSubscriptionCompleteFlowResponse response = (CarrierSubscriptionCompleteFlowResponse) hermesResponse;

                if (response == null || response.getResult() == null || response.getResult().equals(CarrierBillingCompleteFlowResultEnum.FAILURE)) {
                    int messageCode = 0;

                    boolean isConnected = Utils.isConnected(context);

                    if (isConnected) {
                        if (response != null && response.getStatusCode() != null) {
                            messageCode = response.getStatusCode();
                        }
                    } else {
                        messageCode = 9;
                    }

                    Utils.SendMessageToUnity(mSubscriptionListener, "CarrierBillingSubscriptionFailed", String.valueOf(messageCode));
                } else if (response.getResult().equals(CarrierBillingCompleteFlowResultEnum.SUCCESS)
                        && response.getSubscription() != null && response.getSubscription().getSubscriptionStatus().equalsIgnoreCase("ACTIVE")) {
                    LogUtils.i(context, "[CarrierIABPlugin - Subscription success]");
                    LogUtils.i(context, "response: "+response);

                    //Add subscription
                    KiwiPlugin.instance().addSubscription(mSku);

                    Utils.SendMessageToUnity(mSubscriptionListener, "CarrierBillingSubscriptionSucceeded", "");
                } else if (response.getResult().equals(CarrierBillingCompleteFlowResultEnum.TIMEOUT)) {
                    LogUtils.i(context, "SubscribePinCodeCaptureTimedOut");
                    Utils.SendMessageToUnity(mSubscriptionListener, "CarrierBillingSubscriptionTimedOut", "");
                } else if (response != null) {
                    Utils.SendMessageToUnity(mSubscriptionListener, "CarrierBillingSubscriptionFailed", String.valueOf(response.getStatusCode()));
                }
            }
        };

        restoreObserver = new Observer() {
            @Override
            public void update(Observable observable, Object hermesResponse) {
                final CarrierSubscriptionCompleteFlowResponse response = (CarrierSubscriptionCompleteFlowResponse) hermesResponse;

                if (response == null || response.getResult() == null || response.getResult().equals(CarrierBillingCompleteFlowResultEnum.FAILURE)) {
                    int messageCode = -1;

                    boolean isConnected = Utils.isConnected(context);

                    if (isConnected) {
                        if (response != null && response.getStatusCode() != null) {
                            messageCode = response.getStatusCode();
                        }
                    } else {
                        messageCode = 9;
                    }

                    Utils.SendMessageToUnity(mRestoreListener, "RestoreCarrierSubscriptionFailure", String.valueOf(messageCode));
                } else if (response.getResult().equals(CarrierBillingCompleteFlowResultEnum.SUCCESS) && response.getSubscription() != null && response.getSubscription().getSubscriptionStatus().equalsIgnoreCase("ACTIVE")) {
                    LogUtils.i(context, "[CarrierIABPlugin - Restore success]");
                    LogUtils.i(context, "response: " + response);

                    //Add subscription
                    KiwiPlugin.instance().addSubscription(mSku);

                    Utils.SendMessageToUnity(mRestoreListener, "RestoreCarrierSubscriptionSuccess", String.valueOf(response.getStatusCode()));
                } else if (response.getResult().equals(CarrierBillingCompleteFlowResultEnum.TIMEOUT)) {
                    LogUtils.i(context, "RestorePinCodeCaptureTimedOut");
                    Utils.SendMessageToUnity(mRestoreListener, "RestoreCarrierSubscriptionTimeout", "");
                } else {
                    Utils.SendMessageToUnity(mRestoreListener, "RestoreCarrierSubscriptionFailure",  response.getStatusMessage());
                }
            }
        };
    }

    public static CarrierIABPlugin instance() {
        if (instance == null) {
            instance = new CarrierIABPlugin();
        }

        return instance;
    }

    public void subscribe(final String msisdn, final long carrierId, final String sku, String listener) {
        mSubscriptionListener = listener;
        mSku = sku;

        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(context, "[CarrierIABPlugin - subscribe] - MSISDN: " + msisdn + ", SKU: " + sku);
                PluginInitializer.getHermesSDK().carrierSubscriptionCompleteFlow(context, subscriptionObserver, msisdn, carrierId, sku, 0, true);
            }
        }).start();
    }

    public void subscribeSubmitPinCode(final String pinCode, final String msisdn, final long carrierId, final String sku,
                                       final String listener) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LogUtils.i(context, "[CarrierIABPlugin - subscribe] - MSISDN: " + msisdn + ", SKU: " + sku);

                CarrierSubscriptionCompleteFlowResponse response = PluginInitializer.getHermesSDK().subscribeByPincode(context, pinCode, msisdn, carrierId, sku, 0);

               if (response != null && CarrierBillingCompleteFlowResultEnum.SUCCESS.equals(response.getResult())) {
                   LogUtils.i(context, "[CarrierIABPlugin - Subscription Success]");
                   //Add subscription
                   KiwiPlugin.instance().addSubscription(sku);

                    Utils.SendMessageToUnity(listener, "CarrierBillingSubscriptionSucceeded", "");
                } else {
                    Utils.SendMessageToUnity(listener, "CarrierBillingSubscriptionFailed",
                            response != null ? String.valueOf(response.getStatusCode()) : "");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void restoreSubscriptionCompleteFlow(final String sku, String listener) {
        mSku = sku;
        mRestoreListener = listener;
        LogUtils.i(context, "[CarrierIABPlugin - restoreSubscription] - SKU: " + mSku);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                mCarrierId = getCarrierId(mPhone);

                PluginInitializer.getHermesSDK().restoreCarrierSubscriptionCompleteFlow(context, restoreObserver, mPhone, mCarrierId, mSku, true);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void restoreWithPinCode(final String pinCode, String sku, final String listener) {
        mSku = sku;

        SharedPreferences prefs = SharedPreferenceUtils.getInstance().getPluginsSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(PREFS_CARRIER_PHONE, mPhone);
        edit.putString(PREFS_CARRIER_SKU, mSku);
        edit.putBoolean(PREFS_CARRIER_RESTORE, true);
        edit.apply();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                CarrierSubscriptionCompleteFlowResponse response = PluginInitializer.getHermesSDK().restoreCarrierSubscription(context, pinCode, mPhone, mCarrierId, mSku);
                if (response != null && CarrierBillingCompleteFlowResultEnum.SUCCESS.equals(response.getResult())) {
                    //Add subscription
                    KiwiPlugin.instance().addSubscription(mSku);

                    Utils.SendMessageToUnity(listener, "RestoreCarrierSubscriptionSuccess", String.valueOf(response.getStatusCode()));
                } else {
                    Utils.SendMessageToUnity(listener, "RestoreCarrierSubscriptionFailure", response != null ? String.valueOf(response.getStatusCode()) : "0");
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void sendPincode(final String msisdn, final String sku) {
        mCarrierId = getCarrierIdFromJson(msisdn);
        mSku = sku;
        KiwiPlugin.instance().setHermesSKU(sku);
        LogUtils.d(context, "sku: " + sku);
        LogUtils.d(context, "carrierId: : " + mCarrierId);
        LogUtils.d(context, "phone: " + mPhone);

        new Thread(new Runnable() {

            @Override
            public void run() {
                LogUtils.i(context, "[CarrierIABPlugin - sendNewPincode] - , PHONE: " + msisdn + ", SKU: " + sku + ", CARRIER_ID: " + mCarrierId);

                GenericResponse genericResponse = PluginInitializer.getHermesSDK().sendNewPincode(mPhone, mCarrierId, sku, false);

                if (genericResponse != null && genericResponse.getSuccess()) {
                    Utils.SendMessageToUnity("SubscriptionManager", "SendPincodeSuccess", "");
                } else {
                    Utils.SendMessageToUnity("SubscriptionManager", "SendPincodeError", "");
                }
            }
        }).start();
    }

    private long getCarrierIdFromJson(String json) {
        if (StringUtils.isNotBlank(json)) {
            Phone phone = new Gson().fromJson(json, Phone.class);

            if (phone != null) {
                return phone.getCarrierId();
            }
        }

        return 0;
    }

    public String tryToGetPhoneNumber() {
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String msisdn = tMgr.getLine1Number();

        if (StringUtils.isEmpty(msisdn)) {
            msisdn = null;
        }

        return msisdn;
    }

    public void verifyNumber(String msisdn, long carrier, String listener) {
        mPhone = msisdn;
       if (getCarrierId(msisdn) == carrier) {
            Utils.SendMessageToUnity(listener, "VerifyNumberSuccess", "");
        } else {
            int verificationError = 0;
            Utils.SendMessageToUnity(listener, "VerifyNumberFailure", String.valueOf(verificationError));
        }
    }

    private long getCarrierId(String msisdn) {
        ParsePhoneResponse parsePhoneResponse = PluginInitializer.getHermesSDK().findCarrier(msisdn);
        LogUtils.i(context, "[findCarrier ] ");

        if (parsePhoneResponse != null && parsePhoneResponse.getCarrierId() != null) {
            mCarrierId = parsePhoneResponse.getCarrierId();
        }

        return mCarrierId;
    }
}
