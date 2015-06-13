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

void Character::setStageLocation(Point& point) {
	gridDestination = point;
}

void Character::setStageLocation(int x, int y, int z){
	gridDestination = Point(x, y, z);
}

void Character::stageShift(int x, int y, int z) {
	gridDestination.x += x;
	gridDestination.y += y;
	gridDestination.z += z;
}


void Character::commitMove() {
	gridLocation = gridDestination;
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE) - BLOCK_SIZE);
}

void Character::cancelMove(){
	gridDestination = gridLocation;
}