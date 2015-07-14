#include "stdafx.h"
#include "Character.h"
#include "World.h"
#define _USE_MATH_DEFINES
#include <math.h>
#include <queue>
#include <set>
#include <vector>

Character::Character(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point)
	:BaseClass(world, texture, grid, point, CHARACTER) {
	sprite.scale((CHARACTER_WIDTH * NUMBER_OF_PLAYER_ROTATIONS * 2) / sprite.getLocalBounds().width, (CHARACTER_HEIGHT * 2) / sprite.getLocalBounds().height);
	sprite.setTextureRect(sf::IntRect(0, 0, CHARACTER_WIDTH, CHARACTER_HEIGHT));
	sprite.setOrigin(sprite.getLocalBounds().width / 2, sprite.getLocalBounds().height);
}

Character::~Character() {}

void Character::updateSprite() {
	sf::Vector3i p = cartesianToIsometric(loc.getAbsoluteLocation());
	sprite.setPosition(p.x, p.y - BLOCK_SIZE);
}

std::vector<Location> Character::getNeighbors(Location me) {
	std::vector<Location> locations;
	for (int i = -1; i <= 1; ++i) {
		for (int j = -1; j <= 1; j++) {
			if (std::abs(i + j) == 1) {
				if (!world.itemsExistAtGridLocation(me.getGrid())) {
					locations.push_back(Location(sf::Vector3i(me.getGrid().x + i, me.getGrid().y + j, me.getGrid().z), me.getPoint()));
				}
			}
		}
	}
	return locations;
}


struct ByLocation2 {
	bool operator()(const Location& a, const Location& b) const {
		return (a.getGrid().y > b.getGrid().y) ? true : a.getGrid().x > b.getGrid().x;
	}
};

std::vector<Location> Character::pathTo(Location start, Location end) {
	print(start.toString());
	start = Location(sf::Vector3i(0, 0, 1), sf::Vector3f(0, 0, 0));
	end = Location(sf::Vector3i(25, 36, 1), sf::Vector3f(0, 0, 0));
	std::queue<Location> frontier;
	frontier.push(start);
	std::map<Location, Location, ByLocation2> came_from;
	came_from.insert(std::make_pair(start, start));
	while (!frontier.empty()) {
		Location current = frontier.front();
		frontier.pop();
		if (current.getGrid() == end.getGrid()) {
			break;
		}
		for (Location next : getNeighbors(current)) {
			if (came_from.find(next) == came_from.end()) {
				frontier.push(next);
				came_from[next] = current;
			}
		}
	}
	Location current = end;
	std::vector<Location> path;
	while (current.getGrid() != start.getGrid()) {
		current = came_from[current];
		path.push_back(current);
	}
	return path;
}

void Character::setDestination(sf::Vector3i& location) {
	stop();
	std::vector<Location> things = pathTo(loc, loc);
	for (int i = 0; i < things.size(); ++i) {
		print(things[i].toString());
		destinationList.push(things[i]);
	}
	//destinationList.push(location);
}

bool Character::fly() {
	bool needsToBeMoved = false;
	if (destinationList.size() > 0) {
		sf::Vector3i charPoint = loc.getAbsoluteLocation() - sf::Vector3i(BLOCK_SIZE * 2, BLOCK_SIZE * 2, 64);
		sf::Vector3i destinationLocation = destinationList.front().getAbsoluteLocation();
		if (destinationList.size() >= 2 && (std::abs(charPoint.x - destinationLocation.x) < 8) && (std::abs(charPoint.y - destinationLocation.y) < 8) && (std::abs(charPoint.z - destinationLocation.z) < 8)) {
			destinationList.pop();
			destinationLocation = destinationList.front().getAbsoluteLocation();
		}
		if ((std::abs(charPoint.x - destinationLocation.x) >= 8) || (std::abs(charPoint.y - destinationLocation.y) >= 8) || (std::abs(charPoint.z - destinationLocation.z) >= 8)) {
			move((destinationLocation.x - charPoint.x) / 8, (destinationLocation.y - charPoint.y) / 8, 0);
			if (loc.getGrid() != tempLoc.getGrid()) {
				needsToBeMoved = true;
			} else if (loc.getPoint() != tempLoc.getPoint()) {
				loc.setPoint(tempLoc.getPoint());
				updateSprite();
			}
		}
	}
	return needsToBeMoved;
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
				WorldMapRange items = world.getItemsAtGridLocation(sf::Vector3i(i, j, tempLoc.getGrid().z));
				for (auto& it = items.first; it != items.second; ++it) {
					if (hitDetect(it->second)) {
						return;
					}
				}
			}
		}
	}
}

void Character::applyMove() {
	BaseClass::applyMove();
	updateSprite();
}

void Character::stop() {
	print("stoping");
	BaseClass::stop();
	while (destinationList.size() != 0) {
		destinationList.pop();
	}
	print("character stoped");
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