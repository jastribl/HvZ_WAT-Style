#pragma once
class World;
#include "Point.h"

class BaseClass {

protected:
	sf::Sprite sprite;

public:
	World& world;
	Point gridLocation;
	Point pointLocation;

	BaseClass(World& world, const sf::Texture& texture, Point gridLocation, Point pointLocation);
	~BaseClass();

	virtual void move(int x, int y, int z);
	virtual void draw(sf::RenderWindow& window);
};