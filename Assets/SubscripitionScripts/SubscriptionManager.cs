using UnityEngine;
using System;
using System.Collections;
using System.Collections.Generic;

namespace Movile 
{
	public class SubscriptionManager : MonoBehaviour 
	{
        #if UNITY_IPHONE || UNITY_ANDROID

		public static Action purchaseSucceededEvent;
		public static Action<int> purchaseFailedEvent;

		/*
		 * restoreAppStoreSucceededEvent return a array of active subscriptions
		 */
		public static Action<string[]> restoreAppStoreSucceededEvent;
		public static Action<int> restoreAppStoreFailedEvent;
		public static Action verifyNumberSucceededEvent;
		public static Action<int> verifyNumberFailedEvent;

		/*
		 * restoreCarrierSucceededEvent parameters codes (restore status, subscription status)
		 * 
		 * Restore Status (first parameter)
		 * KiwiProviderPurchaseRestoreOperationStatusUnknown = 0,
		 * KiwiProviderPurchaseRestoreOperationStatusRestored = 1,
		 * KiwiProviderPurchaseRestoreOperationStatusInvalidSubscription = 2,
		 * KiwiProviderPurchaseRestoreOperationStatusAuthCodeInvalid = 3,
		 * KiwiProviderPurchaseRestoreOperationStatusAuthCodeNotFound = 4
		 * 
		 * Subscription Status (second parameter)
		 * KiwiSubscriptionStatusActive = 1,
		 * KiwiSubscriptionStatusExpired = 2,
		 * KiwiSubscriptionStatusCancelled = 3,
		 * KiwiSubscriptionStatusTrial = 4,
		 * KiwiSubscriptionStatusUnknown = 5
		 */ 
		public static Action<int, int> restoreCarrierSucceededEvent;
		public static Action<int> restoreCarrierFailedEvent;

		void Awake()
		{
			DontDestroyOnLoad(this);
		}

		public void PurchaseProductSuccess() 
		{
			if (purchaseSucceededEvent != null)
			{
				purchaseSucceededEvent.Invoke();
			}
		}

		public void PurchaseProductFailure(string errorCode)
		{
			if (purchaseFailedEvent != null)
			{
				int errorCodeConverted = Int32.Parse(errorCode);
				purchaseFailedEvent.Invoke(errorCodeConverted);
			}
		}

		public void RestoreAppStoreSubscriptionSuccess(string subscriptions)
		{
			if (restoreAppStoreSucceededEvent != null ) 
			{
				string[] subscriptionsList = null;

				if (!string.IsNullOrEmpty (subscriptions)) 
				{
					subscriptionsList = subscriptions.Split (',');			
				}

				restoreAppStoreSucceededEvent.Invoke(subscriptionsList);
			}
		}

		public void RestoreAppStoreSubscriptionFailure(string errorCode)
		{
			if (restoreAppStoreFailedEvent != null)
			{
				restoreAppStoreFailedEvent.Invoke(Int32.Parse(errorCode));
			}
		}

		public void VerifyNumberSuccess() 
		{
			Debug.Log("tentando rodar VerifyNumberSuccess");
			if (verifyNumberSucceededEvent != null)
			{
				verifyNumberSucceededEvent.Invoke();
			}
		}

		public void VerifyNumberFailure(string errorCode)
		{
			Debug.Log("tentando rodar VerifyNumberFailure");
			if (verifyNumberFailedEvent != null)
			{
				verifyNumberFailedEvent.Invoke(Int32.Parse(errorCode));
			}
		}

		public void RestoreCarrierSubscriptionSuccess(string subscriptionCodes)
		{
			if (restoreCarrierSucceededEvent != null)
			{
				string[] subscriptionCodesList = subscriptionCodes.Split (',');
				restoreCarrierSucceededEvent.Invoke(Int32.Parse(subscriptionCodesList[0]), Int32.Parse(subscriptionCodesList[1]));
			}
		}

		public void RestoreCarrierSubscriptionFailure(string errorCode)
		{
			if (restoreCarrierFailedEvent != null)
			{
				restoreCarrierFailedEvent.Invoke(Int32.Parse(errorCode));
			}
		}

        public static Action carrierBillingSubscriptionSucceededEvent;
        public static Action<string> carrierBillingSubscriptionFailedEvent;
        public static Action carrierBillingSubscriptionTimedOut;

        public void CarrierBillingSubscriptionSucceeded()
        {
            if (carrierBillingSubscriptionSucceededEvent != null)
            {
                carrierBillingSubscriptionSucceededEvent.Invoke();
            }
        }

        public void CarrierBillingSubscriptionFailed(string errorCode)
        {
            if (carrierBillingSubscriptionFailedEvent != null)
            {
                carrierBillingSubscriptionFailedEvent.Invoke(errorCode);
            }
        }

        public void CarrierBillingSubscriptionTimedOut()
        {
            if (carrierBillingSubscriptionTimedOut != null)
            {
                carrierBillingSubscriptionTimedOut.Invoke();
            }
        }

        public static Action restoreCarrierSubscriptionSucceededEvent;
        public static Action restoreCarrierSubscriptionTimedOutEvent;

        public void RestoreCarrierSubscriptionSuccess()
        {
            if (restoreCarrierSubscriptionSucceededEvent != null)
            {
                restoreCarrierSubscriptionSucceededEvent.Invoke();
            }
        }

        public void RestoreCarrierSubscriptionTimeout()
        {
            if (restoreCarrierSubscriptionTimedOutEvent != null)
            {
                restoreCarrierSubscriptionTimedOutEvent.Invoke();
            }
        }

        public static Action sendPincodeSucceededEvent;
        public static Action sendPincodeFailedEvent;

        public void SendPincodeSuccess()
        {
            if (sendPincodeSucceededEvent != null)
            {
                sendPincodeSucceededEvent.Invoke();
            }
        }

        public void SendPincodeFailure()
        {
            if (sendPincodeFailedEvent != null)
            {
                sendPincodeFailedEvent.Invoke();
            }
        }

        public static Action<int> googlePlayBillingFailedEvent;

        public void GooglePlayBillingNotSupported(string errorCode)
        {
            if (sendPincodeFailedEvent != null)
            {
                int errorCodeConverted = Int32.Parse(errorCode);
                googlePlayBillingFailedEvent.Invoke(errorCodeConverted);
            }
        }
        #endif
    }
}
