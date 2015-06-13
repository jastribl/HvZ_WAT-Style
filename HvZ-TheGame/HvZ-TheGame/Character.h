#pragma once
#include "BaseClass.h"
class World;

class Character :public BaseClass {

public:
	Character(World& world, const sf::Texture& texture, Point grid);
	~Character();

	virtual void setStageLocation(Point& point);
	virtual void setStageLocation(int x, int y, int z);
	virtual	void stageShift(int x, int y, int z);
	virtual	void commitMove();
	virtual void cancelMove();
};