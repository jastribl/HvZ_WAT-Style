#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"
#include "Constants.h"
#include <iostream>


World::World(std::string name) :name(name) {}

World::~World() {}

bool World::itemsExistAtGridLocation(const sf::Vector3i& point) const {
	return world.find(point) != world.end();
}

std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> World::getItemsAtGridLocation(const sf::Vector3i& point) {
	return world.equal_range(point);
}

void World::add(BaseClass* object) {
	world.insert({object->loc.getGrid(), object});
}

void World::removeItemFromWorld(const BaseClass* object) {
	std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> items = getItemsAtGridLocation(object->loc.getGrid());
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->loc.getPoint() == object->loc.getPoint()) {
			world.erase(it);
			return;
		}
	}
}

void World::deleteItemFromWorld(const BaseClass* object) {
	std::pair <std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator, std::multimap<sf::Vector3i, BaseClass*, ByLocation>::iterator> items = getItemsAtGridLocation(object->loc.getGrid());
	for (auto it = items.first; it != items.second; ++it) {
		if (it->second->loc.getPoint() == object->loc.getPoint() && it->second->itemType == object->itemType) {
			it->second->needToDelete = true;
			return;
		}
	}
}

void World::updateAndDraw(sf::RenderWindow& window) {
	sf::FloatRect windowRec(window.getView().getCenter() - sf::Vector2f(window.getView().getSize().x / 2, window.getView().getSize().y / 2), window.getView().getSize());
	for (auto it = world.begin(); it != world.end();) {
		if (it->second->needToDelete) {
			delete it->second;
			it = world.erase(it);
		} else {
			BaseClass* item = it->second;
			if (windowRec.intersects(item->screenHitBox())) {
				item->draw(window);
			}
			if (item->itemType == CHARACTER || item->itemType == BULLET) {
				item->fly();
			}
			++it;
		}
	}
	for (int i = 0; i < itemsToMove.size(); ++i) {
		if (itemsToMove[i]) {
			removeItemFromWorld(itemsToMove[i]);
			itemsToMove[i]->applyMove();
			add(itemsToMove[i]);
		}
	}
	itemsToMove.clear();
}