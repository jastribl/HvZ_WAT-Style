#include "stdafx.h"
#include "Block.h"
#include "Constants.h"

Block::Block(World& world, const sf::Texture& texture, Point gridLocation, int blockType)
	:BaseClass(world, texture, gridLocation, Point(0, 0, 0), BLOCK), blockType(blockType) {
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x, p.y);
}

Block::~Block() {}