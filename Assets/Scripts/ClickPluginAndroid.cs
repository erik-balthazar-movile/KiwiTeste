using UnityEngine;
using System.Collections;

public class ClickPluginAndroid : MonoBehaviour {

	public void ClickUsePlugin() {

		AndroidJavaClass unity = new AndroidJavaClass ("com.gustavosilva.myplugin.MainActivity");
		AndroidJavaObject currentAct = unity.CallStatic<AndroidJavaObject> ("instance");
		currentAct.Call("runOnUIThread",new AndroidJavaRunnable(()=> {
			currentAct.Call ("shareText", "Titulo", "Corpo");}));
	}
}
