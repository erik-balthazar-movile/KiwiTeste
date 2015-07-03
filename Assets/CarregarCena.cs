using UnityEngine;
using System.Collections;

public class CarregarCena : MonoBehaviour {

	public string nomeCena;

	public void LoadScene() {
		Application.LoadLevel(nomeCena);
	}
}
