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

void Character::move(int x, int y, int z) {
	Point gridDestination = Point(gridLocation);
	gridDestination.x += x;
	gridDestination.y += y;
	gridDestination.z += z;
	if (!gridLocation.equals(gridDestination)){
		if (!world.existsAt(gridDestination)) {
			world.removeAt(gridLocation);
			gridLocation = gridDestination;
			world.add(this);
		}
	}
}

void Character::draw(sf::RenderWindow& window) {
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE) - BLOCK_SIZE);
	window.draw(sprite);
}