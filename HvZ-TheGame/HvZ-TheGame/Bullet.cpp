#include "stdafx.h"
#include "Bullet.h"
#include "BaseClass.h"
#include "World.h"
#define _USE_MATH_DEFINES
#include <math.h>


Bullet::Bullet(World& world, const sf::Texture& texture, const sf::Vector3i& gridLocation, const sf::Vector3f& pointLocation, float speed, float angle)
	:BaseClass(world, texture, gridLocation, pointLocation, BULLET) {
	velocity = sf::Vector2f(speed * std::cos(angle), speed * std::sin(angle));
	sprite.scale((BULLET_SIZE * 2) / sprite.getLocalBounds().width, (BULLET_SIZE * 2) / sprite.getLocalBounds().height);
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
	sf::Vector3i p = cartesianToIsometric(loc.getAbsoluteLocation());
	sprite.setPosition(p.x, p.y - BLOCK_SIZE);
}

Bullet::~Bullet() {}


void Bullet::fly() {
	move(velocity.x, velocity.y, 0);
	if (loc.getGrid() != tempLoc.getGrid()) {
		world.itemsToMove.push_back(this);
	} else if (loc.getPoint() != tempLoc.getPoint()) {
		loc.setPoint(tempLoc.getPoint());
	}
	sf::Vector3i p = cartesianToIsometric(loc.getAbsoluteLocation());
	sprite.setPosition(p.x, p.y - BLOCK_SIZE);
}

void Bullet::move(const float x, const float y, const float z) {
	if (x == 0 && y == 0 && z == 0) {
		return;
	} else if ((std::pow(std::abs(x), 2) + std::pow(std::abs(y), 2) + std::pow(std::abs(z), 2)) > std::pow(MAX_MOVEMENT_CHECK_THRESHOLD, 2)) {
		move(x / 2, y / 2, z / 2);
		move(x / 2, y / 2, z / 2);
	} else {
		tempLoc.add(x, y, z);
		for (int i = tempLoc.getGrid().x - 1; i < tempLoc.getGrid().x + 1; ++i) {
			for (int j = tempLoc.getGrid().y - 1; j < tempLoc.getGrid().y + 1; j++) {
				WorldMapRange itemsAt = world.getItemsAtGridLocation(sf::Vector3i(i, j, tempLoc.getGrid().z));
				for (auto& it = itemsAt.first; it != itemsAt.second; ++it) {
					if (hitDetect(it->second)) {
						return;
					}
				}
			}
		}
	}
}

bool Bullet::hitDetect(const BaseClass* test) {
	if (test->itemType == BLOCK) {
		if (std::abs(test->loc.getAbsoluteLocationX() - tempLoc.getAbsoluteLocationX()) <= BLOCK_SIZE && std::abs(test->loc.getAbsoluteLocationY() - tempLoc.getAbsoluteLocationY()) <= BLOCK_SIZE) {
			needsToBeDeleted = true;
			return true;
		}
	}
	//else if (test->itemType == BULLET) {
	//	needsToBeDeleted = true;
	//	test->needsToBeDeleted = true;
	//}
	//else if (test->itemType == CHARACTER) {
	//	//maybe nothing? (need to have a bullet owner)
	//}
	return false;
}