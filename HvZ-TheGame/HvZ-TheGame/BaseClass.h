#pragma once
class World;
#include "Point.h"

class BaseClass {

public:
	World& world;
	Point gridLocation;
	Point gridDestination;
	sf::Sprite sprite;

	BaseClass(World& world, const sf::Texture& texture, Point grid);
	~BaseClass();

	virtual void setStageLocation(Point& point);
	virtual void setStageLocation(int x, int y, int z);
	virtual void stageShift(int x, int y, int z);
	virtual void commitMove();
	virtual void cancelMove();
	virtual void draw(sf::RenderWindow& window);
};