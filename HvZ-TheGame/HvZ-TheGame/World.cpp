#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"

World::World(std::string name) : name(name) {}

World::~World() {}

bool World::itemsExistAtGridLocation(const sf::Vector3i& point) const {
	return world.find(point) != world.end();
}

WorldMapRange World::getItemsAtGridLocation(const sf::Vector3i& point) {
	return world.equal_range(point);
}

void World::add(BaseClass* object) {
	world.insert(std::make_pair(object->loc.getGrid(), object));
}

void World::removeItemFromWorld(const BaseClass* object) {
	WorldMapRange items = getItemsAtGridLocation(object->loc.getGrid());
	for (auto& it = items.first; it != items.second; ++it) {
		if (it->second->loc.getPoint() == object->loc.getPoint()) {
			world.erase(it);
			return;
		}
	}
}

void World::updateAndDraw(sf::RenderWindow& window) {
	sf::FloatRect windowRec(window.getView().getCenter() - sf::Vector2f(window.getView().getSize().x / 2, window.getView().getSize().y / 2), window.getView().getSize());
	auto& it = world.begin();
	while (it != world.end()) {
		BaseClass* item = it->second;
		if (item->needsToBeDeleted) {
			it = world.erase(it);
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
}