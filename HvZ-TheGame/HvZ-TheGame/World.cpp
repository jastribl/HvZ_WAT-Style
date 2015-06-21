#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"
#include "Constants.h"
#include <iostream>


World::World(std::string name) :name(name) {}

World::~World() {}

bool World::itemsExistAtGridLocation(const sf::Vector3i& point) const {
	return world.count(point) > 0;
}

std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> World::getItemsAtGridLocation(const sf::Vector3i& point) {
	return world.equal_range(point);
}

void World::add(BaseClass* object) {
	world.insert({object->loc.getGrid(), object});
}

void World::removeItemFromWorld(const sf::Vector3i& grid, const sf::Vector3f& point) {
	std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> items = getItemsAtGridLocation(grid);
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->loc.getPoint() == point) {
			world.erase(it);
			return;
		}
	}
}
void World::deleteItemFromWorld(const sf::Vector3i& grid, const sf::Vector3f& point) {
	std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> items = getItemsAtGridLocation(grid);
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->loc.getPoint() == point) {
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
	sf::FloatRect windowRec(window.getView().getCenter() - sf::Vector2f(window.getView().getSize().x / 2, window.getView().getSize().y / 2), window.getView().getSize());
	for (auto it = world.begin(); it != world.end(); ++it) {
		BaseClass* item = it->second;
		if (windowRec.intersects(item->hitBox())) {
			it->second->draw(window);
		}
		if (it->second->itemGroup == CHARACTER) {
			it->second->fly();
		}
	}
	for (int i = 0; i < itemsToMove.size(); i++) {
		removeItemFromWorld(itemsToMove[i]->loc.getGrid(), itemsToMove[i]->loc.getPoint());
		itemsToMove[i]->applyMove();
		add(itemsToMove[i]);
	}
	itemsToMove.clear();
	clearDeletedItems();
}