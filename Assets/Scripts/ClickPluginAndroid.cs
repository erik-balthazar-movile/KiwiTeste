using UnityEngine;
using System.Collections;

public class ClickPluginAndroid : MonoBehaviour {

	void ClickUsePlugin() {
		AndroidJavaClass unity = new AndroidJavaClass ("com.unity3d.player.UnityPlayer");
		AndroidJavaObject currentAct = unity.CallStatic<AndroidJavaObject> ("currentActivity");
		currentAct.Call ("shareText", "Titulo", "Corpo");
	}
}
