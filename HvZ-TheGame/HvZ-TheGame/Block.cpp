#include "stdafx.h"
#include "Block.h"
#include "Constants.h"

Block::Block(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, int blockType)
	:BaseClass(world, texture, gridLocation, sf::Vector3f(0, 0, 0), BLOCK), blockType(blockType) {
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sprite.scale((BLOCK_SIZE * 2) / sprite.getLocalBounds().width, (BLOCK_SIZE * 2) / sprite.getLocalBounds().height);
	sf::Vector3i p = cartesianToIsometric(gridLocation.x * BLOCK_SIZE, gridLocation.y * BLOCK_SIZE, gridLocation.z * BLOCK_SIZE);
	sprite.setPosition(p.x, p.y);
}

Block::~Block() {}