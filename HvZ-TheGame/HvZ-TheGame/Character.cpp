#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"

Character::Character(World& world, const sf::Texture& texture, Point grid)
	:BaseClass(world, texture, grid) {
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, (BLOCK_SIZE * 2) / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE) - BLOCK_SIZE);
}

Character::~Character() {}

void Character::stageShift(int x, int y, int z) {
	gridDestination.x += x;
	gridDestination.y += y;
	gridDestination.z += z;
}


void Character::commitMove() {
	if (!gridLocation.equals(gridDestination)){
		if (world.existsAt(gridDestination)) {
			cancelMove();
		}
		else{
			world.removeAt(gridLocation);
			gridLocation = gridDestination;
			world.add(this);
		}
	}
}

void Character::cancelMove(){
	gridDestination = gridLocation;
}

void Character::draw(sf::RenderWindow& window) {
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE) - BLOCK_SIZE);
	window.draw(sprite);
}