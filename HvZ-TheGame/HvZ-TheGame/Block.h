#pragma once

#include "Point.h"

class Block {

private:
	int type;
	Point location;

public:
	Block();
	Block(int blockType, Point blockLocation);
	~Block();
	int getType();
	void setType(int blockType);
	Point getLocation() const;
	int getX() const;
	int getY() const;
	void setLocation(Point blockLocation);
};

