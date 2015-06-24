using UnityEngine;
using System.Collections;
using Movile;
using System;

public class OurSubscriptionController : MonoBehaviour {

	static string trimestral = "com.movile.android.oikids.trimestral";

	// Use this for initialization
	IEnumerator Start () {
		yield return new WaitForSeconds(10);
		Debug.Log ("TESTE - Subscription inicio");
		AtualizaTexto("TESTE - Subscription \ninicio");
		Subscription.InitWithApplicationId("");

		Debug.Log ("TESTE - Subscription inicializado");
		AtualizaTexto("TESTE - Subscription \ninicializado");
		Subscription.UpdateAllSubscriptions ();

		Debug.Log ("TESTE - Subscription atualizado");
		AtualizaTexto("TESTE - Subscription \natualizado");
		bool subscribed = Subscription.CheckActiveKiwiSubscription (trimestral);

		Debug.Log ("TESTE - Subscription fim: "+subscribed);
		AtualizaTexto("TESTE - Subscription \nfim: "+subscribed);
	}

	void AtualizaTexto(string texto){
		gameObject.GetComponent<TextMesh>().text = texto;
	}
}
