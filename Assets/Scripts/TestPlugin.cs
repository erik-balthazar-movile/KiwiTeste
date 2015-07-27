using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using UnityEngine.UI;

public class TestPlugin : MonoBehaviour {

	[DllImport ("__Internal")]
	private static extern string _helloWorld();
	[DllImport ("__Internal")]
	private static extern void openOurViewController();

	static string HelloWorld(){
		string str = "";

		if(Application.platform == RuntimePlatform.IPhonePlayer){
			str = _helloWorld();
		}

		return str;
	}

	public void GetHelloWorld(){
		gameObject.GetComponent<Text>().text = HelloWorld();
	}

	static void OpenOurViewController() {
		if(Application.platform == RuntimePlatform.IPhonePlayer){
			openOurViewController();
		}
	}

	public void OpenView() {
		OpenOurViewController ();
	}
}
