#pragma once

#include "Point.h"

class BaseClass {

public:
	Point gridLocation;
	Point gridDestination;
	sf::Sprite sprite;

	BaseClass(Point grid, const sf::Texture& texture);
	~BaseClass();

	virtual void move(int x, int y);
	virtual void applyMove();
	virtual void stop();
	virtual void draw(sf::RenderWindow& window);
};