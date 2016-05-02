using UnityEngine;
using System.Collections;

public class Level : MonoBehaviour {

	private int levelWidth;
	private int levelHeight;

	public Transform grassTile;
	public Transform stoneBrickTile;

	private Color[] tileColors;

	public Color grassColor;
	public Color stoneBrickColor;
	public Color spawnPointColor;

	public Texture2D levelTexture;

	public Entity player;

	void Start () {
		PlayerPrefs.DeleteAll();
		levelWidth = levelTexture.width;
		levelHeight = levelTexture.height;
		loadLevel();
	}

	void Update () {

	}

	void loadLevel() {
		tileColors = new Color[levelWidth * levelHeight];
		tileColors = levelTexture.GetPixels();

		for(int x = 0; x < levelWidth; x++) {
			for (int y = 0; y < levelHeight; y++) {
				if(tileColors[x + y * levelWidth] == grassColor) {
					Instantiate(grassTile, new Vector3(x, y), Quaternion.identity);
				}
				if(tileColors[x + y * levelWidth] == stoneBrickColor) {
					Instantiate(stoneBrickTile, new Vector3(x, y), Quaternion.identity);
				}
				if(tileColors[x + y * levelWidth] == spawnPointColor) {
					Instantiate(grassTile, new Vector3(x, y), Quaternion.identity);
					Vector2 pos = new Vector2(x, y);
					player.transform.position = pos;
				}
			}
		}
	}
}
