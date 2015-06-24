using UnityEngine;
using System.Collections;

public class CarregarCena : MonoBehaviour {

	public string nomeCena;

	void OnMouseUp() {
		Application.LoadLevel(nomeCena);
		print ("asdsadsasd");
	}
}
