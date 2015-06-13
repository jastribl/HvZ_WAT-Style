#include "stdafx.h"
#include "Level.h"

Level::Level() {}

Level::~Level() {}

bool Level::isEmpty() const {
	return level.empty();
}

bool Level::existsAt(const Point& point) const {
	return level.count(point) > 0;
}

BaseClass* Level::getAt(const Point& point) {
	return level.find(point)->second;
}

void Level::add(BaseClass* object) {
	level.insert({ object->gridLocation, object });
}

void Level::removeAt(const Point& point) {
	level.erase(point);
}

void Level::draw(sf::RenderWindow& window) {
	for (auto iterator = level.begin(); iterator != level.end(); ++iterator){
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
	for (auto iterator = level.begin(); iterator != level.end(); ++iterator){
		iterator->second->draw(window);
	}
}

int Level::size() const {
	return level.size();
}