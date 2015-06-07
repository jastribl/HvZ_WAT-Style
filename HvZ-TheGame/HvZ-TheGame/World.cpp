#include "stdafx.h"
#include "World.h"
#include <iostream>

World::World() {}

World::~World() {}

void World::addLevel(const Level& level){
	world.push_back(level);
}

void World::removeLevel(int i){
	world.erase(world.begin() + i);
}

Level& World::getLevel(int index){
	return world.at(index);
}

void World::draw(sf::RenderWindow& window) {
	for (int i = 0; i < world.size(); i++){
		world.at(i).draw(window);
	}
}

int World::size() const {
	return world.size();
}