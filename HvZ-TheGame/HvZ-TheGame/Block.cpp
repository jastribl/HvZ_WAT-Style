#include "stdafx.h"

#include "Block.h"
#include "Constants.h"
#include <iostream>

Block::Block(Point gridLocation, const sf::Texture& texture, int blockType, int level)
	:BaseClass(gridLocation, texture, level), type(blockType) {
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (level * HALF_BLOCK_SIZE));
}

Block::~Block() {}