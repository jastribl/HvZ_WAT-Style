#include "stdafx.h"
#include "World.h"

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
	for (auto iterator = world.begin(); iterator != world.end(); ++iterator){
		BaseClass* temp = iterator->second;
		if (!temp->gridLocation.equals(temp->gridDestination)){
			if (existsAt(temp->gridDestination)) {
				temp->stop();
			}
			else{
				removeAt(temp->gridLocation);
				temp->applyMove();
				add(temp);
			}
		}
		iterator->second->draw(window);
	}
	for (auto iterator = world.begin(); iterator != world.end(); ++iterator){
		iterator->second->draw(window);
	}
}

int World::size() const {
	return world.size();
}