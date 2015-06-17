#pragma once
class TextureManager;
#include "Inventory.h"

class Hud {

private:
	sf::Font hpFont;
	sf::Font timeFont;
	float hp = 100;
	float hpmax = 100;
	float mp = 100;
	float mpmax = 100;
	sf::CircleShape minimap;

	Inventory inventory;

public:
	Hud(TextureManager& textureManager);
	~Hud();
	void click(int x, int y);
	void release(int x, int y);
	void setHP(float newhp);
	void setMP(float newmp);
	void drawToWindow(sf::RenderWindow& window);
};