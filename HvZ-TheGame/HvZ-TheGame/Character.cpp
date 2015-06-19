#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"
#include <iostream>

Character::Character(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, const sf::Vector3f& pointLocation)
	:BaseClass(world, texture, gridLocation, pointLocation, CHARACTER) {
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sprite.scale((CHARACTER_WIDTH * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
	this->stop();
}

Character::~Character() {}

void Character::setDestination(const sf::Vector3f& dest) {
	destLoca = sf::Vector3f(dest);
}

void Character::fly() {
	sf::Vector3f charPoint = sf::Vector3f(gridLoc * BLOCK_SIZE) + sf::Vector3f(pointLoc.x - (BLOCK_SIZE * 2), pointLoc.y - (BLOCK_SIZE * 2), 0);
	if (destLoca != charPoint) {
		sf::Vector3i& p = sf::Vector3i(destLoca.x - charPoint.x, destLoca.y - charPoint.y, 0);
		this->move(p.x / 8, p.y / 8, p.z / 8);
	}
}

void Character::move(const float x, const float y, const float z) {
	if (x == 0 && y == 0 && z == 0) {
		return;
	} else if ((std::pow(std::abs(x), 2) + std::pow(std::abs(y), 2) + std::pow(std::abs(z), 2)) > std::pow(MAX_MOVEMENT_CHECK_THRESHOLD, 2)) {
		this->move(x / 2, y / 2, z / 2);
		this->move(x / 2, y / 2, z / 2);
	} else {
		pointTemp = sf::Vector3f(pointLoc.x + x, pointLoc.y + y, pointLoc.z + z);
		gridTemp = sf::Vector3i(gridLoc);
		if (pointTemp.x >= BLOCK_SIZE) {
			pointTemp.x -= BLOCK_SIZE;
			gridTemp.x++;
		} else if (pointTemp.x < 0) {
			pointTemp.x += BLOCK_SIZE;
			gridTemp.x--;
		}
		if (pointTemp.y >= BLOCK_SIZE) {
			pointTemp.y -= BLOCK_SIZE;
			gridTemp.y++;
		} else if (pointTemp.y < 0) {
			pointTemp.y += BLOCK_SIZE;
			gridTemp.y--;
		}
		if (pointTemp.z >= BLOCK_SIZE) {
			pointTemp.z -= BLOCK_SIZE;
			gridTemp.z++;
		} else if (pointTemp.z < 0) {
			pointTemp.z += BLOCK_SIZE;
			gridTemp.z--;
		}
		for (int i = gridTemp.x - 1; i < gridTemp.x + 1; i++) {
			for (int j = gridTemp.y - 1; j < gridTemp.y + 1; j++) {
				std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> itemsAt = world.getItemsAt(sf::Vector3i(i, j, gridTemp.z));
				for (auto it = itemsAt.first; it != itemsAt.second; ++it) {
					if (hitDetect(*it->second)) {
						return;
					}
				}
			}
		}
		if (gridLoc != gridTemp) {
			world.itemsToMove.push_back(this);
		} else if (pointLoc != pointTemp) {
			pointLoc = pointTemp;
		}
		sf::Vector3i p = cartesianToIsometric((gridLoc.x * BLOCK_SIZE) + pointLoc.x, (gridLoc.y * BLOCK_SIZE) + pointLoc.y, (gridLoc.z * BLOCK_SIZE) + pointLoc.z);
		sprite.setPosition(p.x, p.y - BLOCK_SIZE);
	}
}

bool Character::hitDetect(const BaseClass& test) {
	if (test.itemGroup == BLOCK) {
		if (std::abs((((test.gridLoc.x - gridTemp.x) * BLOCK_SIZE) - pointTemp.x)) <= BLOCK_SIZE || std::abs((((test.gridLoc.y - gridTemp.y) * BLOCK_SIZE) - pointTemp.y)) <= BLOCK_SIZE) {
			return true;
		}
	}
	return false;
}

void Character::stop() {
	gridTemp = sf::Vector3i(gridLoc);
	pointTemp = sf::Vector3f(pointLoc);
}

void Character::draw(sf::RenderWindow& window) {
	BaseClass::draw(window);
}