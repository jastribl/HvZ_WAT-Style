#pragma once

class Hud{
private:
	sf::Font font;
	float hp = 100;
	float hpmax = 100;
	float mp = 100;
	float mpmax = 100;
public:
	Hud();
	~Hud();
	const sf::Sprite drawHud();
	void drawToWindow(sf::RenderWindow& window);
	void setHP(float newhp);
	void setMP(float newmp);
};