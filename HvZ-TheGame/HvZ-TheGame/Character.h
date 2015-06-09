#pragma once

#include "BaseClass.h"

class Character :public BaseClass {

public:
	Character(Point grid, const sf::Texture& texture, int level);
	~Character();

	void move(int x, int y);
	void stop();
	void applyMove();
};