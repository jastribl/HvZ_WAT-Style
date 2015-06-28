#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"
#include "Constants.h"
#include <iostream>
#include <vector>


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
	for (auto& it = items.first; it != items.second; ++it) {
		if (it->second->loc.getPoint() == object->loc.getPoint()) {
			world.erase(it);
			return;
		}
	}
}

void World::updateAndDraw(sf::RenderWindow& window) {
	std::vector <BaseClass*> things;
	sf::FloatRect windowRec(window.getView().getCenter() - sf::Vector2f(window.getView().getSize().x / 2, window.getView().getSize().y / 2), window.getView().getSize());
	auto& it = world.begin();
	while (it != world.end()) {
		BaseClass* item = it->second;
		if (item->needsToBeDeleted) {
			it = world.erase(it);
			things.push_back(item);
			delete item;
		} else {
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
		removeItemFromWorld(itemsToMove[i]);
		itemsToMove[i]->applyMove();
		add(itemsToMove[i]);
	}
	itemsToMove.clear();
	std::cout << things.size() << std::endl;
}