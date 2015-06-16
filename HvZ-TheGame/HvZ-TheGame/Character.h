#pragma once
#include "BaseClass.h"
class World;

class Character :public BaseClass {

public:
	Point gridDestination;
	Point pointDestination;

	Character(World& world, const sf::Texture& texture, Point gridLocation, Point pointLocation);
	~Character();

	virtual	void move(int x, int y, int z);
	virtual void draw(sf::RenderWindow& window);
	virtual void hitDetect(BaseClass& test);
};