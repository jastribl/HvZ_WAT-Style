#include "stdafx.h"
#include "Level.h"

#include <iostream>


Level::Level() {}

Level::~Level() {}

bool Level::isEmpty() const {
	return level.empty();
}

bool Level::blockExitsAt(const Point& point) const {
	return level.count(point) > 0;
}

BaseClass* Level::getBlockAt(const Point& point) {
	return level.find(point)->second;
}

void Level::addBlock(BaseClass* block) {
	level.insert({ block->gridLocation, block });
}

void Level::removeBlockAt(const Point& point) {
	level.erase(point);
}

void Level::draw(sf::RenderWindow& window) {
	for (auto iterator = level.begin(); iterator != level.end(); ++iterator){
		BaseClass* temp = iterator->second;
		if (!temp->gridLocation.equals(temp->gridDestination)){
			if (blockExitsAt(temp->gridDestination)) {
				temp->stop();
			}
			else{
				removeBlockAt(temp->gridLocation);
				temp->applyMove();
				addBlock(temp);
			}
		}
		iterator->second->draw(window);
	}
	for (auto iterator = level.begin(); iterator != level.end(); ++iterator){
		//BaseClass* temp = iterator->second;
		iterator->second->draw(window);
	}
}

int Level::size() const {
	return level.size();
}