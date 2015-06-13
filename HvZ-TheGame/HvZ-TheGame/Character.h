#pragma once
#include "BaseClass.h"

class Character :public BaseClass {

public:
	Character(Point grid, const sf::Texture& texture);
	~Character();

	virtual void setStageLocation(Point& point);
	virtual void setStageLocation(int x, int y, int z);
	virtual	void stageShift(int x, int y, int z);
	virtual	void commitMove();
	virtual void cancelMove();
};