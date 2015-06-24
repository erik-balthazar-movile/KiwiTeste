using UnityEngine;
using System.Collections;
using Movile;
using System;

public class OurSubscriptionController : MonoBehaviour {

	static string trimestral = "com.movile.android.oikids.trimestral";

	// Use this for initialization
	void Start () {
		Debug.Log ("TESTE - Subscription inicio");
		Subscription.InitWithApplicationId("");
		Debug.Log ("TESTE - Subscription inicializado");
		Subscription.UpdateAllSubscriptions ();
		Debug.Log ("TESTE - Subscription atualizado");
		bool subscribed = Subscription.CheckActiveKiwiSubscription (trimestral);
		Debug.Log ("TESTE - Subscription fim: "+subscribed);
	}
}
