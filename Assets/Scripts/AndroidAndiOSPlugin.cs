using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using UnityEngine.UI;

public class AndroidAndiOSPlugin : MonoBehaviour {

	public Text textField;
	[DllImport ("__Internal")]
	public static extern void openOurViewController();

	public void OnClick(){
#if UNITY_IOS
		if(Application.platform == RuntimePlatform.IPhonePlayer){
			openOurViewController();
		}

#elif UNITY_ANDROID
		AndroidJavaClass unity = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
		AndroidJavaObject currentAct = unity.GetStatic<AndroidJavaObject> ("currentActivity");
		textField.text = currentAct.Call<string> ("DoIt");
		currentAct.Call ("shareText", "Palmeiras", "Campeao");
#endif
	}
}
