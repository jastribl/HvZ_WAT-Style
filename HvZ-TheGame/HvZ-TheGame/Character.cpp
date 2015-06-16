#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"
#include "Point.h"
#include <iostream>

Character::Character(World& world, const sf::Texture& texture, Point gridLocation, Point pointLocation)
	:BaseClass(world, texture, gridLocation, pointLocation, CHARACTER), gridDestination(gridLocation), pointDestination(pointLocation) {
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sprite.scale((CHARACTER_WIDTH * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
}

Character::~Character() {}

void Character::move(int x, int y, int z) {
	pointDestination = Point(pointLocation);
	pointDestination.x += x;
	pointDestination.y += y;
	pointDestination.z += z;
	gridDestination = Point(gridLocation);
	while (pointDestination.x >= BLOCK_SIZE) {
		pointDestination.x -= BLOCK_SIZE;
		gridDestination.x++;
	}
	while (pointDestination.x < 0) {
		pointDestination.x += BLOCK_SIZE;
		gridDestination.x--;
	}
	while (pointDestination.y >= BLOCK_SIZE) {
		pointDestination.y -= BLOCK_SIZE;
		gridDestination.y++;
	}
	while (pointDestination.y < 0) {
		pointDestination.y += BLOCK_SIZE;
		gridDestination.y--;
	}
	while (pointDestination.z >= BLOCK_SIZE) {
		pointDestination.z -= BLOCK_SIZE;
		gridDestination.z++;
	}
	while (pointDestination.z < 0) {
		pointDestination.z += BLOCK_SIZE;
		gridDestination.z--;
	}
	for (int x = gridDestination.x - 1; x <= gridDestination.x + 1; x++) {
		for (int y = gridDestination.y - 1; y <= gridDestination.y + 1; y++) {
			std::pair <std::multimap<Point, BaseClass*, ByLocation>::iterator, std::multimap<Point, BaseClass*, ByLocation>::iterator> itemsAt = world.getItemsAt(Point(x, y, gridDestination.z));
			for (auto it = itemsAt.first; it != itemsAt.second; ++it) {
				hitDetect(*it->second);
			}
		}

	}
	if (!gridLocation.equals(gridDestination)) {
		world.removeFromMap(gridLocation, pointLocation);
		pointLocation = pointDestination;
		gridLocation = gridDestination;
		world.add(this);
	} else if (!pointLocation.equals(pointDestination)) {
		pointLocation = pointDestination;
	}
}

void Character::draw(sf::RenderWindow& window) {
	Point p = cartesianToIsometric((gridLocation.x * BLOCK_SIZE) + pointLocation.x, (gridLocation.y * BLOCK_SIZE) + pointLocation.y, (gridLocation.z * BLOCK_SIZE) + pointLocation.z);
	sprite.setPosition(p.x, p.y - BLOCK_SIZE);
	BaseClass::draw(window);
}

void Character::hitDetect(BaseClass& test) {
	if (test.itemGroup == BLOCK) {
		int xDiff = ((test.gridLocation.x * BLOCK_SIZE) + test.pointLocation.x) - ((gridDestination.x * BLOCK_SIZE) + pointDestination.x);
		int yDiff = ((test.gridLocation.y * BLOCK_SIZE) + test.pointLocation.y) - ((gridDestination.y * BLOCK_SIZE) + pointDestination.y);
		if (std::abs(xDiff) >= BLOCK_SIZE || std::abs(yDiff) >= BLOCK_SIZE) {
			gridDestination = Point(gridLocation);
			pointDestination = Point(pointLocation);
			std::cout << "hit" << std::endl;
		}
	} else if (test.itemGroup == CHARACTER) {
		return;
	}
}