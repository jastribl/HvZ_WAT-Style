#include "stdafx.h"
#include "Constants.h"
#include "Character.h"

Character::Character(Point grid, const sf::Texture& texture, int level)
	:BaseClass(grid, texture, level) {
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (level * HALF_BLOCK_SIZE));
}

Character::~Character() {}

void Character::move(int x, int y) {
	gridDestination.x += x;
	gridDestination.y += y;
}

void Character::stop() {
	gridDestination = gridLocation;
}

void Character::applyMove() {
	gridLocation = gridDestination;
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (level * HALF_BLOCK_SIZE));
}