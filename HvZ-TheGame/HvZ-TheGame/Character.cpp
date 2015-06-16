#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"
#include <iostream>

Character::Character(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3i pointLocation)
	:BaseClass(world, texture, gridLocation, pointLocation, CHARACTER), gridDestination(gridLocation), pointDestination(pointLocation) {
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sprite.scale((CHARACTER_WIDTH * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
}

Character::~Character() {}

void Character::move(int x, int y, int z) {
	pointDestination = sf::Vector3i(pointLocation);
	pointDestination.x += ((x > 0) - (x < 0)) * std::min(std::abs(x), HALF_BLOCK_SIZE);
	pointDestination.y += ((y > 0) - (y < 0)) * std::min(std::abs(y), HALF_BLOCK_SIZE);
	pointDestination.z += ((z > 0) - (z < 0)) * std::min(std::abs(z), HALF_BLOCK_SIZE);
	gridDestination = sf::Vector3i(gridLocation);
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
	for (int x = gridDestination.x - 1; x < gridDestination.x + 1; x++) {
		for (int y = gridDestination.y - 1; y < gridDestination.y + 1; y++) {
			std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> itemsAt = world.getItemsAt(sf::Vector3i(x, y, gridDestination.z));
			for (auto it = itemsAt.first; it != itemsAt.second; ++it) {
				hitDetect(*it->second);
			}
		}
	}
	if (!VectorsAreEqual(gridLocation, gridDestination)) {
		world.removeFromMap(gridLocation, pointLocation);
		pointLocation = pointDestination;
		gridLocation = gridDestination;
		world.add(this);
	} else if (!VectorsAreEqual(pointLocation, pointDestination)) {
		pointLocation = pointDestination;
	}
}

void Character::draw(sf::RenderWindow& window) {
	sf::Vector3i p = cartesianToIsometric((gridLocation.x * BLOCK_SIZE) + pointLocation.x, (gridLocation.y * BLOCK_SIZE) + pointLocation.y, (gridLocation.z * BLOCK_SIZE) + pointLocation.z);
	sprite.setPosition(p.x, p.y - BLOCK_SIZE);
	BaseClass::draw(window);
}

void Character::hitDetect(BaseClass& test) {
	if (test.itemGroup == BLOCK) {
		if (std::abs((((test.gridLocation.x - gridDestination.x) * BLOCK_SIZE) - pointDestination.x)) <= BLOCK_SIZE || std::abs((((test.gridLocation.y - gridDestination.y) * BLOCK_SIZE) - pointDestination.y)) <= BLOCK_SIZE) {
			gridDestination = sf::Vector3i(gridLocation);
			pointDestination = sf::Vector3i(pointLocation);
		}
	}
}