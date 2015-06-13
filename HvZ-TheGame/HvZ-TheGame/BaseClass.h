#pragma once
class World;
#include "Point.h"

class BaseClass {

public:
	World& world;
	Point gridLocation;
	sf::Sprite sprite;

	BaseClass(World& world, const sf::Texture& texture, Point grid);
	~BaseClass();

	virtual void move(int x, int y, int z);
	virtual void draw(sf::RenderWindow& window);
};