#pragma once
#include "Point.h"

class BaseClass {

public:
	Point gridLocation;
	Point gridDestination;
	sf::Sprite sprite;

	BaseClass(Point grid, const sf::Texture& texture);
	~BaseClass();

	virtual void setStageLocation(Point& point);
	virtual void setStageLocation(int x, int y, int z);
	virtual void stageShift(int x, int y, int z);
	virtual void commitMove();
	virtual void cancelMove();
	virtual void draw(sf::RenderWindow& window);
};