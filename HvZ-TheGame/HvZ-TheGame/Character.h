#pragma once
#include "BaseClass.h"
class World;

class Character :public BaseClass {

public:
	Character(World& world, const sf::Texture& texture, Point grid);
	~Character();

	virtual	void move(int x, int y, int z);
	virtual void draw(sf::RenderWindow& window);
};