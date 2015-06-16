#pragma once
#include "BaseClass.h"
class World;

class Character :public BaseClass {

public:
	sf::Vector3i gridDestination;
	sf::Vector3f pointDestination;

	Character(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3f pointLocation);
	~Character();

	virtual	void move(int x, int y, int z);
	virtual void draw(sf::RenderWindow& window);
	virtual void hitDetect(BaseClass& test);
};