#pragma once

class Hud {

private:
	sf::Font font;
	float hp = 100;
	float hpmax = 100;
	float mp = 100;
	float mpmax = 100;

public:
	Hud();
	~Hud();
	void setHP(float newhp);
	void setMP(float newmp);
	void drawToWindow(sf::RenderWindow& window);
};