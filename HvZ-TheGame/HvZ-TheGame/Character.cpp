#include "stdafx.h"
#include "Character.h"

#include "Constants.h"

Character::Character(Point grid, const sf::Texture& texture)
	:BaseClass(grid, texture) {
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, (BLOCK_SIZE * 2) / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE) - BLOCK_SIZE);
}

Character::~Character() {}

void Character::move(int x, int y) {
	gridDestination.x += x;
	gridDestination.y += y;
}

void Character::applyMove() {
	gridLocation = gridDestination;
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE));
}

void Character::stop(){
	gridDestination = gridLocation;
}