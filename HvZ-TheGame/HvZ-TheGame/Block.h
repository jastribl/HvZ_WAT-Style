#pragma once

#include "Point.h"

class Block {

private:
	int type;
	Point location;
	sf::Sprite sprite;

public:
	Block();
	Block(int blockType, Point blockLocation, int level, const sf::Texture& texture);
	~Block();
	int getType();
	void setType(int blockType);
	Point getLocation() const;
	int getX() const;
	int getY() const;
	void setLocation(Point blockLocation);
	void draw(sf::RenderWindow& window);
};

