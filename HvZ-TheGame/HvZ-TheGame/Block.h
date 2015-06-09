#pragma once

#include "BaseClass.h"

class Block : public BaseClass {

public:
	const int type;

	Block(Point gridLocation, const sf::Texture& texture, int blockType, int level);
	~Block();
};