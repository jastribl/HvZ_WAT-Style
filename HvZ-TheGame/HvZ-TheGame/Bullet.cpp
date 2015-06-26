#include "stdafx.h"
#include "Bullet.h"
#include "BaseClass.h"
#include "Constants.h"
#include "World.h"
#define _USE_MATH_DEFINES
#include <math.h>
#include <iostream>


Bullet::Bullet(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, const sf::Vector3f& pointLocation, float speed, float angle)
	:BaseClass(world, texture, gridLocation, pointLocation, BULLET) {
	velocity = sf::Vector2f(speed * std::cos(angle), speed * std::sin(angle));
	sprite.scale((BULLET_SIZE * 2) / sprite.getLocalBounds().width, (BULLET_SIZE * 2) / sprite.getLocalBounds().height);
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
}

Bullet::~Bullet() {}


void Bullet::fly() {
	this->move(velocity.x, velocity.y, 0);
}

void Bullet::move(const float x, const float y, const float z) {
	if (x == 0 && y == 0 && z == 0) {
		return;
	} else if ((std::pow(std::abs(x), 2) + std::pow(std::abs(y), 2) + std::pow(std::abs(z), 2)) > std::pow(MAX_MOVEMENT_CHECK_THRESHOLD, 2)) {
		this->move(x / 2, y / 2, z / 2);
		this->move(x / 2, y / 2, z / 2);
	} else {
		tempLoc = Location(loc);
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
		if (loc.getGrid() != tempLoc.getGrid()) {
			world.itemsToMove.push_back(this);
		} else if (loc.getPoint() != tempLoc.getPoint()) {
			loc.setPoint(tempLoc.getPoint());
		}
		sf::Vector3i p = cartesianToIsometric((loc.getGrid().x * BLOCK_SIZE) + loc.getPoint().x, (loc.getGrid().y * BLOCK_SIZE) + loc.getPoint().y, (loc.getGrid().z * BLOCK_SIZE) + loc.getPoint().z);
		sprite.setPosition(p.x, p.y - BLOCK_SIZE);
	}
}

bool Bullet::hitDetect(const BaseClass* test) {
	if (test->itemType == BLOCK) {
		if (std::abs((((test->loc.getGrid().x - tempLoc.getGrid().x) * BLOCK_SIZE) - tempLoc.getPoint().x)) <= BLOCK_SIZE || std::abs((((test->loc.getGrid().y - tempLoc.getGrid().y) * BLOCK_SIZE) - tempLoc.getPoint().y)) <= BLOCK_SIZE) {
			BaseClass* thing = this;
			this->world.deleteItemFromWorld(thing);
			return true;
		}
		//else if (test->itemType == BULLET) {
		//	//not sure, kill both?
		//} else if (test->itemType == CHARACTER) {
		//	//maybe nothing? (need to have a bullet owner)
		//}
	}
	return false;
}