#include "stdafx.h"
#include "Block.h"
#include "Constants.h"

Block::Block(World& world, const sf::Texture& texture, Point gridLocation, int blockType)
	:BaseClass(world, texture, gridLocation), type(blockType) {
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (gridLocation.z * HALF_BLOCK_SIZE));
}

Block::~Block() {}

void Block::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}