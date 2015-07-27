using UnityEngine;
using System.Collections;
using UnityEngine.UI;

public class ClickPluginAndroid : MonoBehaviour {

	public Text textField;

	public void ClickUsePlugin() {
		/*
		AndroidJavaClass unity = new AndroidJavaClass ("com.gustavosilva.myplugin.MainActivity");
		AndroidJavaObject currentAct = unity.CallStatic<AndroidJavaObject> ("instance");
		currentAct.Call("runOnUIThread",new AndroidJavaRunnable(()=> {
			currentAct.Call ("shareText", "Titulo", "Corpo");}));
			*/
	#if UNITY_ANDROID 
		AndroidJavaClass unity = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
		AndroidJavaObject currentAct = unity.GetStatic<AndroidJavaObject> ("currentActivity");
		textField.text = currentAct.Call<string> ("DoIt");
		currentAct.Call ("shareText", "Palmeiras", "Campeao");
	#endif
	}
}
