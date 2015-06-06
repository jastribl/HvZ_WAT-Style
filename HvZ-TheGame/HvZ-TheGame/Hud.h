#pragma once

class Hud : public sf::RenderTexture {
private:
	sf::Sprite drawingSprite;
	sf::CircleShape shape;

public:
	Hud();
	~Hud();
	const sf::Sprite drawHud();
	void drawToWindow(sf::RenderWindow& window);
};