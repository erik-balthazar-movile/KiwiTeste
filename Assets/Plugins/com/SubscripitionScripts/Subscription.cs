using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using System.Text;

namespace Movile
{
	
	public class Subscription 
	{

		public enum CarrierCode
		{
			Vivo = 1,
			Claro = 2,
			Oi = 4,
			Tim = 5
		};
		
#if UNITY_IPHONE
        [DllImport("__Internal")]
        private static extern void initWithApplicationId(string applicationId);

		[DllImport("__Internal")]
		private static extern void initWithSharedSecretKey(string secretKey);	
		
		[DllImport("__Internal")]
		private static extern void updateAllSubscriptions();
		
		[DllImport("__Internal")]
		private static extern void configurePurchaseDisabled(bool disabled);	

		[DllImport("__Internal")]
		private static extern bool isPurchaseDisabled();	

		[DllImport("__Internal")]
		private static extern bool checkActiveAppStoreSubscription(string appStoreId);	

		[DllImport("__Internal")]
		private static extern void purchaseProduct(string productId, string listener);	
		
		[DllImport("__Internal")]
		private static extern void restoreAppStoreSubscription(string listener);	

		[DllImport("__Internal")]
		private static extern bool checkActiveKiwiSubscription(string subscriptionId);	

		[DllImport("__Internal")]
		private static extern void verifyNumber(string numberString, int carrier, string sku, string listener);	

		[DllImport("__Internal")]
		private static extern void restoreCarrierSubscription(string pinCode, string sku, string listener);	
		
#endif

#if UNITY_ANDROID

        public static string[] productsId = new string[] {"com.movile.android.oikids.trimestral" ,
                                                          "com.movile.android.oikids.mensal",
                                                          "com.movile.android.oikids.semanal"
                                                          };

        static string ProductsId
        {
            get
            {
                string result = "";
                for(int i=0; i < productsId.Length; i++)
                {
                    result += productsId[i];
                    if(i< productsId.Length-1)
                    {
                        result += ",";
                    }
                }
                return result;
            }
        }

        static AndroidJavaObject _kiwiPlugin;
        static AndroidJavaClass _unityPlayer;
        static AndroidJavaObject _context;
        static AndroidJavaObject _carrierIABPlugin;
        static AndroidJavaObject _googleIABPlugin;

        static AndroidJavaObject KiwiPlugin
        {
            get
            {
                //Get the Kiwi Plugin instance
                if (_kiwiPlugin == null)
                {
                    using (var pluginClass = new AndroidJavaClass("com.movile.subscription.KiwiPlugin"))
                    {
                        _kiwiPlugin = pluginClass.CallStatic<AndroidJavaObject>("instance");
                    }
                }
                return _kiwiPlugin;
            }
        }

        static AndroidJavaClass UnityPlayer
        {
            get
            {
                //Get the Kiwi Plugin instance
                if (_unityPlayer == null)
                {
                    _unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
                }
                return _unityPlayer;
            }
        }

        static AndroidJavaObject Context
        {
            get
            {
                //Get the Kiwi Plugin instance
                if (_context == null)
                {
                    _context = UnityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
                }
                return _context;
            }
        }

        static AndroidJavaObject CarrierIABPlugin
        {
            get
            {
                //Get the Kiwi Plugin instance
                if (_carrierIABPlugin == null)
                {
                    using (var pluginClass = new AndroidJavaClass("com.movile.subscription.CarrierIABPlugin"))
                    {
                        _carrierIABPlugin = pluginClass.CallStatic<AndroidJavaObject>("instance");
                    }
                }
                return _carrierIABPlugin;
            }
        }

        static AndroidJavaObject GoogleIABPlugin
        {
            get
            {
                //Get the Kiwi Plugin instance
                if (_googleIABPlugin == null)
                {
                    using (var pluginClass = new AndroidJavaClass("com.movile.subscription.GoogleIABPlugin"))
                    {
                        _googleIABPlugin = pluginClass.CallStatic<AndroidJavaObject>("instance");
                    }
                }
                return _googleIABPlugin;
            }
        }
#endif

        public static void InitWithApplicationId(string appId)    
        {
#if UNITY_IPHONE
            Debug.Log ("cs initWithApplicationId " + appId);
            initWithApplicationId(appId);
#endif

#if UNITY_ANDROID
           //Get the Kiwi Plugin instance
            using (var pluginClass = new AndroidJavaClass("com.movile.subscription.PluginInitializer"))
            {
				Debug.Log("Kiwi - Entrou plugin initialize");
                AndroidJavaObject _pluginInitializer = pluginClass.CallStatic<AndroidJavaObject>("instance");
				
				Debug.Log("Kiwi - Vai chamar plugin initialize");
                _pluginInitializer.Call("initPlugins", new object[] { Context });
                GoogleIABPlugin.Call("init", new object[] { "EnableLog" });
				
				Debug.Log("Kiwi - Saiu plugin initialize");
            }
#endif
        }
			
		public static void InitWithSharedSecretKey(string secretKey)	
		{
#if UNITY_IPHONE
			Debug.Log ("cs initWithSharedSecretKey " + secretKey);
			initWithSharedSecretKey(secretKey);
#endif
		}	

		public static void UpdateAllSubscriptions()	
		{
#if UNITY_IPHONE
			Debug.Log ("cs UpdateAllSubscriptions");
			updateAllSubscriptions();
#endif
#if UNITY_ANDROID
            AndroidJavaClass unityPlayer = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
            AndroidJavaObject _context = unityPlayer.GetStatic<AndroidJavaObject>("currentActivity");
            _kiwiPlugin.Call("checkAndUpdateSubscriptions", _context);
#endif
		}

		public static void ConfigurePurchaseDisabled(bool disabled)	
		{
#if UNITY_IPHONE
			Debug.Log ("cs configurePurchaseDisabled");
			configurePurchaseDisabled(disabled);
#endif
		}

		public static bool IsPurchaseDisabled()	
		{
#if UNITY_IPHONE
			Debug.Log ("cs isPurchaseDisabled");
			return isPurchaseDisabled();
#else
			return false;
#endif
		}

		public static bool CheckActiveAppStoreSubscription(string appStoreId) 
		{
#if UNITY_IPHONE
			Debug.Log ("cs checkActiveAppStoreSubscription");
			return checkActiveAppStoreSubscription(appStoreId);
#else
			return false;
#endif
		}

		public static void PurchaseProduct(string productId, string listener) 
		{
#if UNITY_IPHONE
			Debug.Log ("cs purchaseProduct");
			purchaseProduct(productId, listener);
#endif
#if UNITY_ANDROID
            GoogleIABPlugin.Call("purchaseProduct", new object[] { productId, "", listener });
#endif
		}

		public static void RestoreAppStoreSubscription(string listener) 
		{
#if UNITY_IPHONE
			Debug.Log ("cs restoreAppStoreSubscription");
			restoreAppStoreSubscription(listener);
#endif
#if UNITY_ANDROID
            GoogleIABPlugin.Call("restoreGooglePlaySubscription", new object[] { ProductsId, listener });
#endif
		}
		
		public static bool CheckActiveKiwiSubscription(string subscriptionId) 
		{
            bool result = false;
#if UNITY_IPHONE
			Debug.Log ("cs checkActiveKiwiSubscription");
			result = checkActiveKiwiSubscription(subscriptionId);
#elif UNITY_ANDROID
            result = KiwiPlugin.Call<bool>("checkActivateSubscription", new object[] { subscriptionId });
#endif
            return false;
		}

		public static void VerifyNumber(string numberString, CarrierCode carrier, string sku, string listener) 
		{
#if UNITY_IPHONE
			Debug.Log ("cs verifyNumber");
			verifyNumber(numberString, (int)carrier, sku, listener);
#elif UNITY_ANDROID
            CarrierIABPlugin.Call("verifyNumber", new object[] { numberString, (long)carrier, listener });
#endif
		}

        public static void PurchaseCarrierProduct(string msisdn, CarrierCode carrier, string sku, string listener)
        {
#if UNITY_IPHONE
			Debug.Log ("cs purchaseProduct");
			purchaseProduct(productId, listener);
#endif
#if UNITY_ANDROID
            CarrierIABPlugin.Call("subscribe", new object[] { msisdn, (int)carrier, sku, listener });
#endif
        }

        public static void PurchaseCarrierProductWithPinCode(string pinCode, string msisdn, CarrierCode carrier, string sku, string listener)
        {
#if UNITY_IPHONE
			Debug.Log ("cs purchaseProduct");
			purchaseProduct(productId, listener);
#endif
#if UNITY_ANDROID
            CarrierIABPlugin.Call("subscribeSubmitPinCode", new object[] { pinCode, msisdn, (int)carrier, sku, listener });
#endif
        }

		public static void RestoreCarrierSubscription(string sku, string listener) 
		{
#if UNITY_IPHONE
			Debug.Log ("cs RestoreCarrierSubscription");
			restoreCarrierSubscription(pinCode, sku, listener);
#endif
#if UNITY_ANDROID
            CarrierIABPlugin.Call("restoreSubscriptionCompleteFlow", new object[] {sku, listener });
#endif
		}

        public static void RestoreCarrierSubscriptionWithPinCode(string pinCode, string sku, string listener)
        {
#if UNITY_IPHONE
			Debug.Log ("cs RestoreCarrierSubscription");
			restoreCarrierSubscription(pinCode, sku, listener);
#endif
#if UNITY_ANDROID
            CarrierIABPlugin.Call("restoreWithPinCode", new object[] { pinCode, sku, listener });
#endif
        }

        public static string TryToGetPhoneNumber()
        {
            string result = null;
#if UNITY_ANDROID
            result = CarrierIABPlugin.Call<string>("tryToGetPhoneNumber");
#endif
            return result;
        }

        public static void SendPinCode(string msisdn, string sku)
        {
#if UNITY_ANDROID
            CarrierIABPlugin.Call("sendPincode", new object[] { msisdn, sku });
#endif
        }
	}
}