#pragma once
#include "stdafx.h"
class Hud : public sf::RenderTexture
{
private:
	sf::Sprite* drawingSprite;
	sf::CircleShape shape;
public:
	
	Hud();
	~Hud();
	const sf::Sprite* drawHud();
};