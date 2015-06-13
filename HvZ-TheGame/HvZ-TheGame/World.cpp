#include "stdafx.h"
#include "World.h"
#include "BaseClass.h"
#include <iostream>

World::World() {}

World::~World() {}

bool World::existsAt(const Point& point) const {
	return world.count(point) > 0;
}

BaseClass* World::getAt(const Point& point) {
	return world.find(point)->second;
}

void World::add(BaseClass* object) {
	world.insert({ object->gridLocation, object });
}

void World::removeAt(const Point& point) {
	world.erase(point);
}

void World::draw(sf::RenderWindow& window) {
	for (auto it = world.begin(); it != world.end(); ++it){
		it->second->draw(window);
	}
}

int World::size() const {
	return world.size();
}