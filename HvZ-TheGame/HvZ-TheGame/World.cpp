#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"

World::World(std::string name) : name(name) {}

World::~World() {}

bool World::itemsExistAtGridLocation(const sf::Vector3i& grid) const {
	return world.find(grid) != world.end();
}

WorldMapRange World::getItemsAtGridLocation(const sf::Vector3i& grid) {
	return world.equal_range(grid);
}

void World::add(BaseClass* object) {
	world.insert(std::make_pair(object->loc.getGrid(), object));
}

void World::removeItemFromWorld(const WorldMapIterator& object) {
	world.erase(object);
}

void World::updateAndDraw(sf::RenderWindow& window) {
	sf::FloatRect windowRec(window.getView().getCenter() - sf::Vector2f(window.getView().getSize().x / 2, window.getView().getSize().y / 2), window.getView().getSize());
	for (auto& it = world.begin(); it != world.end();) {
		BaseClass* item = it->second;
		if (item->needsToBeDeleted) {
			it = world.erase(it);
			delete item;
		} else {
			if (windowRec.intersects(item->screenHitBox())) {
				item->draw(window);
			}
			if (item->itemType == CHARACTER || item->itemType == BULLET) {
				if (item->fly()) {
					itemsToMove.push_back(it);
				}
			}
			++it;
		}
	}
	for (int i = 0; i < itemsToMove.size(); ++i) {
		BaseClass* itemTomove = itemsToMove[i]->second;
		removeItemFromWorld(itemsToMove[i]);
		itemTomove->applyMove();
		add(itemTomove);
	}
	itemsToMove.clear();
}