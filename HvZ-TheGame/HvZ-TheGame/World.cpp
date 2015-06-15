#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"
#include <iostream>

World::World() {}

World::~World() {}

bool World::itemsExistAt(const Point& point) const {
	return world.count(point) > 0;
}

std::pair <std::multimap<Point, BaseClass*, ByLocation>::iterator, std::multimap<Point, BaseClass*, ByLocation>::iterator> World::getItemsAt(const Point& point) {
	return world.equal_range(point);
}

void World::add(BaseClass* object) {
	world.insert({object->gridLocation, object});
}

void World::removeFromMap(const Point& grid, const Point& point) {
	std::pair <std::multimap<Point, BaseClass*, ByLocation>::iterator, std::multimap<Point, BaseClass*, ByLocation>::iterator> items = getItemsAt(grid);
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->pointLocation.equals(point)) {
			world.erase(it);
			return;
		}
	}
}

void World::deleteItem(const Point& grid, const Point& point) {
	std::pair <std::multimap<Point, BaseClass*, ByLocation>::iterator, std::multimap<Point, BaseClass*, ByLocation>::iterator> items = getItemsAt(grid);
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->pointLocation.equals(point)) {
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

void World::draw(sf::RenderWindow& window) {
	for (auto it = world.begin(); it != world.end(); ++it) {
		it->second->draw(window);
	}
	clearDeletedItems();
}

int World::size() const {
	return world.size();
}