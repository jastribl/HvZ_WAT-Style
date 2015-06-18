#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"
#include "Constants.h"
#include <iostream>

World::World() {}

World::~World() {}

bool World::itemsExistAt(const sf::Vector3i& point) const {
	return world.count(point) > 0;
}

std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> World::getItemsAt(const sf::Vector3i& point) {
	return world.equal_range(point);
}

void World::add(BaseClass* object) {
	world.insert({object->gridLoc, object});
}

void World::removeFromMap(const sf::Vector3i& grid, const sf::Vector3f& point) {
	std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> items = getItemsAt(grid);
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->pointLoc == point) {
			world.erase(it);
			return;
		}
	}
}

void World::deleteItem(const sf::Vector3i& grid, const sf::Vector3f& point) {
	std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> items = getItemsAt(grid);
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->pointLoc == point) {
			deletedItems.push_back(it->second);
			world.erase(it);
			return;
		}
	}
}

void World::clearDeletedItems() {
	for (auto it = deletedItems.begin(); it != deletedItems.end(); ++it) {
		delete *it;
	}
	deletedItems.clear();
}

void World::updateAndDraw(sf::RenderWindow& window) {
	for (auto it = world.begin(); it != world.end(); ++it) {
		it->second->draw(window);
		it->second->fly();
	}
	for (auto it = itemsToMove.begin(); it != itemsToMove.end(); ++it) {
		removeFromMap((*it)->gridLoc, (*it)->pointLoc);
		(*it)->pointLoc = (*it)->pointTemp;
		(*it)->gridLoc = (*it)->gridTemp;
		add(*it);
	}
	itemsToMove.clear();
	clearDeletedItems();
}

int World::size() const {
	return world.size();
}