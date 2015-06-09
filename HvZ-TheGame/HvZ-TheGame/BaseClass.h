#pragma once

#include "Point.h"

class BaseClass {

public:
	Point gridLocation;
	Point gridDestination;
	int level;
	sf::Sprite sprite;

	BaseClass(Point grid, const sf::Texture& texture, int level);
	virtual ~BaseClass();

	virtual void move(int x, int y);
	virtual void applyMove();
	virtual void stop();

	void draw(sf::RenderWindow& window);
};