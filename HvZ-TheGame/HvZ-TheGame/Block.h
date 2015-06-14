#pragma once
#include "BaseClass.h"
class World;

class Block : public BaseClass {

public:
	const int blockType;

	Block(World& world, const sf::Texture& texture, Point gridLocation, int blockType);
	~Block();
};