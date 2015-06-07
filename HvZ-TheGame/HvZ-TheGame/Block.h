#pragma once

#include "Point.h"

class Block {

private:
	sf::Sprite sprite;
	bool visible = true;

public:
	const int type;
	const Point location;

	Block(int blockType, Point blockLocation, int level, const sf::Texture& texture);
	~Block();
	void draw(sf::RenderWindow& window);
	void toggleVisible();
};