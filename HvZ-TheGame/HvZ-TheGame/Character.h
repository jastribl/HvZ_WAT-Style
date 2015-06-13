#pragma once

#include "BaseClass.h"

class Character :public BaseClass {

public:
	Character(Point grid, const sf::Texture& texture);
	~Character();

	virtual	void move(int x, int y);
	virtual	void applyMove();
	virtual void stop();
};