#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"
#include <iostream>

Character::Character(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3f pointLocation)
	:BaseClass(world, texture, gridLocation, pointLocation, CHARACTER), gridDestination(gridLocation), pointDestination(pointLocation) {
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sprite.scale((CHARACTER_WIDTH * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
}

Character::~Character() {}

void Character::move(float x, float y, float z) {
	if (x == 0 && y == 0 && z == 0) {
		return;
	} else if (std::abs(x) > MAX_MOVEMENT_CHECK_THRESHOLD || std::abs(y) > MAX_MOVEMENT_CHECK_THRESHOLD || std::abs(z) > MAX_MOVEMENT_CHECK_THRESHOLD) {
		move(x / 2, y / 2, z / 2);
		move(x / 2, y / 2, z / 2);
	} else {
		pointDestination = sf::Vector3f(pointLocation.x + x, pointLocation.y + y, pointLocation.z + z);
		gridDestination = sf::Vector3i(gridLocation);
		if (pointDestination.x >= BLOCK_SIZE) {
			pointDestination.x -= BLOCK_SIZE;
			gridDestination.x++;
		} else if (pointDestination.x < 0) {
			pointDestination.x += BLOCK_SIZE;
			gridDestination.x--;
		}
		if (pointDestination.y >= BLOCK_SIZE) {
			pointDestination.y -= BLOCK_SIZE;
			gridDestination.y++;
		} else if (pointDestination.y < 0) {
			pointDestination.y += BLOCK_SIZE;
			gridDestination.y--;
		}
		if (pointDestination.z >= BLOCK_SIZE) {
			pointDestination.z -= BLOCK_SIZE;
			gridDestination.z++;
		} else if (pointDestination.z < 0) {
			pointDestination.z += BLOCK_SIZE;
			gridDestination.z--;
		}
		for (int i = gridDestination.x - 1; i < gridDestination.x + 1; i++) {
			for (int j = gridDestination.y - 1; j < gridDestination.y + 1; j++) {
				std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> itemsAt = world.getItemsAt(sf::Vector3i(i, j, gridDestination.z));
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
			pointDestination = sf::Vector3f(pointLocation);
		}
	}
}