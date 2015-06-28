#pragma once
#include "BaseClass.h"
class World;

class Block : public BaseClass {

public:
	const int blockType;

	Block(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, int blockType);
	virtual ~Block();
};