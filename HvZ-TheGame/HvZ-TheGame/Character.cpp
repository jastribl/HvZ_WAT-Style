#include "stdafx.h"
#include "Character.h"
#include "World.h"
#define _USE_MATH_DEFINES
#include <math.h>

Character::Character(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point)
	:BaseClass(world, texture, grid, point, CHARACTER) {
	sprite.scale((CHARACTER_WIDTH * NUMBER_OF_PLAYER_ROTATIONS * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
	sprite.setTextureRect(sf::IntRect(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT));
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
}

Character::~Character() {}

void Character::setDestination(const sf::Vector3f& dest) {
	destination = sf::Vector3i(dest);
}

void Character::fly() {
	sf::Vector3i charPoint = loc.getAbsoluteLocation() - sf::Vector3i(BLOCK_SIZE * 2, BLOCK_SIZE * 2, 0);
	if (destination != charPoint) {
		move((destination.x - charPoint.x) / 8, (destination.y - charPoint.y) / 8, 0);
		if (loc.getGrid() != tempLoc.getGrid()) {
			world.itemsToMove.push_back(this);
		} else if (loc.getPoint() != tempLoc.getPoint()) {
			loc.setPoint(tempLoc.getPoint());
		}
		sf::Vector3i p = cartesianToIsometric(loc.getAbsoluteLocation());
		sprite.setPosition(p.x, p.y - BLOCK_SIZE);
	}
}

void Character::move(const float x, const float y, const float z) {
	if (x == 0 && y == 0 && z == 0) {
		return;
	} else if ((std::pow(std::abs(x), 2) + std::pow(std::abs(y), 2) + std::pow(std::abs(z), 2)) > std::pow(MAX_MOVEMENT_CHECK_THRESHOLD, 2)) {
		move(x / 2, y / 2, z / 2);
		move(x / 2, y / 2, z / 2);
	} else {
		tempLoc.add(x, y, z);
		for (int i = tempLoc.getGrid().x - 1; i < tempLoc.getGrid().x + 1; ++i) {
			for (int j = tempLoc.getGrid().y - 1; j < tempLoc.getGrid().y + 1; j++) {
				std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> itemsAt = world.getItemsAtGridLocation(sf::Vector3i(i, j, tempLoc.getGrid().z));
				for (auto it = itemsAt.first; it != itemsAt.second; ++it) {
					if (hitDetect(it->second)) {
						return;
					}
				}
			}
		}
	}
}

bool Character::hitDetect(const BaseClass* test) {
	if (test->itemType == BLOCK) {
		if (std::abs((((test->loc.getGrid().x - tempLoc.getGrid().x) * BLOCK_SIZE) - tempLoc.getPoint().x)) <= BLOCK_SIZE || std::abs((((test->loc.getGrid().y - tempLoc.getGrid().y) * BLOCK_SIZE) - tempLoc.getPoint().y)) <= BLOCK_SIZE) {
			stop();
			return true;
		}
	}
	return false;
}

void Character::draw(sf::RenderWindow& window) {
	sf::Vector3f mousePosition = sf::Vector3f(isometricToCartesian(window.mapPixelToCoords(sf::Mouse::getPosition(window)), 0)); //no need to convert to rad
	sprite.setTextureRect(sf::IntRect((CHARACTER_WIDTH * ((int) std::floor(((std::atan2(loc.getAbsoluteLocationY() - mousePosition.y, loc.getAbsoluteLocationX() - mousePosition.x) * 180 / M_PI + 517.5) / 360) * NUMBER_OF_PLAYER_ROTATIONS) % 8)), 0, CHARACTER_WIDTH, CHARACTER_HEIGHT));
	BaseClass::draw(window);
}