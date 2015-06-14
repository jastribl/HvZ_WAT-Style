#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"

Character::Character(World& world, const sf::Texture& texture, Point gridLocation, Point pointLocation)
	:BaseClass(world, texture, gridLocation, pointLocation) {
	sprite.scale(CHARACTER_WIDTH / sprite.getLocalBounds().width, CHARACTER_HEIGHT / sprite.getLocalBounds().height);
}

Character::~Character() {}

void Character::move(int x, int y, int z) {
	Point pointDestination = Point(pointLocation);
	pointDestination.x += x;
	pointDestination.y += y;
	pointDestination.z += z;
	Point gridDestination = Point(gridLocation);
	while (pointDestination.x > HALF_BLOCK_SIZE){
		pointDestination.x -= HALF_BLOCK_SIZE;
		gridDestination.x++;
	}
	while (pointDestination.x < 0){
		pointDestination.x += HALF_BLOCK_SIZE;
		gridDestination.x--;
	}
	while (pointDestination.y > HALF_BLOCK_SIZE){
		pointDestination.y -= HALF_BLOCK_SIZE;
		gridDestination.y++;
	}
	while (pointDestination.y < 0){
		pointDestination.y += HALF_BLOCK_SIZE;
		gridDestination.y--;
	}
	while (pointDestination.z > HALF_BLOCK_SIZE){
		pointDestination.z -= HALF_BLOCK_SIZE;
		gridDestination.z++;
	}
	while (pointDestination.z < 0){
		pointDestination.z += HALF_BLOCK_SIZE;
		gridDestination.z--;
	}
	if (!gridLocation.equals(gridDestination)){
		if (!world.existsAt(gridDestination)) {
			world.removeAt(gridLocation);
			pointLocation = pointDestination;
			gridLocation = gridDestination;
			world.add(this);
		}
	}
	else if (!pointLocation.equals(pointDestination)){
		pointLocation = pointDestination;
	}
}

void Character::draw(sf::RenderWindow& window) {
	Point p = cartesianToIsometric((gridLocation.x * HALF_BLOCK_SIZE) + pointLocation.x, (gridLocation.y * HALF_BLOCK_SIZE) + pointLocation.y, gridLocation.z);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (p.z * HALF_BLOCK_SIZE) - BLOCK_SIZE);
	BaseClass::draw(window);
}