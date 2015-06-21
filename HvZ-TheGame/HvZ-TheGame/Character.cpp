#include "stdafx.h"
#include "Character.h"
#include "Constants.h"
#include "World.h"
#include <iostream>
#define _USE_MATH_DEFINES
#include <math.h>

Character::Character(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, const sf::Vector3f& pointLocation)
	:BaseClass(world, texture, gridLocation, pointLocation, CHARACTER) {
	sprite.scale((CHARACTER_WIDTH * NUMBER_OF_PLAYER_ROTATIONS * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
	sprite.setTextureRect(sf::IntRect(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT));
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	this->stop();
}

Character::~Character() {}

void Character::setDestination(const sf::Vector3f& dest) {
	destLoca = sf::Vector3f(dest);
}

void Character::fly() {
	sf::Vector3f charPoint = sf::Vector3f(loc.getGrid() * BLOCK_SIZE) + sf::Vector3f(loc.getPoint().x - (BLOCK_SIZE * 2), loc.getPoint().y - (BLOCK_SIZE * 2), 0);
	if (destLoca != charPoint) {
		this->move((destLoca.x - charPoint.x) / 8, (destLoca.y - charPoint.y) / 8, 0);
	}
}

void Character::move(const float x, const float y, const float z) {
	if (x == 0 && y == 0 && z == 0) {
		return;
	} else if ((std::pow(std::abs(x), 2) + std::pow(std::abs(y), 2) + std::pow(std::abs(z), 2)) > std::pow(MAX_MOVEMENT_CHECK_THRESHOLD, 2)) {
		this->move(x / 2, y / 2, z / 2);
		this->move(x / 2, y / 2, z / 2);
	} else {
		tempLoc = Location(loc);
		tempLoc.add(x, y, z);
		for (int i = tempLoc.getGrid().x - 1; i < tempLoc.getGrid().x + 1; i++) {
			for (int j = tempLoc.getGrid().y - 1; j < tempLoc.getGrid().y + 1; j++) {
				std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> itemsAt = world.getItemsAtGridLocation(sf::Vector3i(i, j, tempLoc.getGrid().z));
				for (auto it = itemsAt.first; it != itemsAt.second; ++it) {
					if (hitDetect(*it->second)) {
						return;
					}
				}
			}
		}
		if (loc.getGrid() != tempLoc.getGrid()) {
			world.itemsToMove.push_back(this);
		} else if (loc.getPoint() != tempLoc.getPoint()) {
			loc.setPoint(tempLoc.getPoint());
		}
		sf::Vector3i p = cartesianToIsometric((loc.getGrid().x * BLOCK_SIZE) + loc.getPoint().x, (loc.getGrid().y * BLOCK_SIZE) + loc.getPoint().y, (loc.getGrid().z * BLOCK_SIZE) + loc.getPoint().z);
		sprite.setPosition(p.x, p.y - BLOCK_SIZE);
	}
}

bool Character::hitDetect(const BaseClass& test) {
	if (test.itemType == BLOCK) {
		if (std::abs((((test.loc.getGrid().x - tempLoc.getGrid().x) * BLOCK_SIZE) - tempLoc.getPoint().x)) <= BLOCK_SIZE || std::abs((((test.loc.getGrid().y - tempLoc.getGrid().y) * BLOCK_SIZE) - tempLoc.getPoint().y)) <= BLOCK_SIZE) {
			return true;
		}
	}
	return false;
}

void Character::stop() {
	tempLoc.setGrid(loc.getGrid());
	tempLoc.setPoint(loc.getPoint());
}

void Character::draw(sf::RenderWindow& window) {
	sf::Vector3f mousePosition = sf::Vector3f(isometricToCartesian(window.mapPixelToCoords(sf::Mouse::getPosition(window)), 0));
	sprite.setTextureRect(sf::IntRect((CHARACTER_WIDTH * ((int) std::floor(((std::atan2(loc.getGrid().y * BLOCK_SIZE - mousePosition.y, loc.getGrid().x * BLOCK_SIZE - mousePosition.x) * 180 / M_PI + 517.5) / 360) * NUMBER_OF_PLAYER_ROTATIONS) % 8)), 0, CHARACTER_WIDTH, CHARACTER_HEIGHT));
	BaseClass::draw(window);
}