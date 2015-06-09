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

BaseClass& Level::getBlockAt(const Point& point) {
	return level.find(point)->second;
}

void Level::addBlock(const BaseClass& block) {
	level.insert({ block.gridLocation, block });
}

void Level::removeBlockAt(const Point& point) {
	level.erase(point);
}

void Level::draw(sf::RenderWindow& window) {
	for (std::map<Point, BaseClass, ByLocation>::iterator iterator = level.begin(); iterator != level.end(); ++iterator){
		iterator->second.applyMove();
		iterator->second.draw(window);
	}
}

int Level::size() const {
	return level.size();
}