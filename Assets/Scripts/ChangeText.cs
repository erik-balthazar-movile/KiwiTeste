using UnityEngine;
using System.Collections;
using UnityEngine.UI;


[RequireComponent (typeof (Text))]
public class ChangeText : MonoBehaviour {
	
	public string Androidtext;
	public string iOStext;

	// Use this for initialization
	void Start () {
#if UNITY_ANDROID 
		gameObject.GetComponent<Text>().text = Androidtext;

#elif UNITY_IOS 
		gameObject.GetComponent<Text>().text = iOStext;
#endif
	}

}
