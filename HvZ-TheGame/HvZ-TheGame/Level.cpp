#include "stdafx.h"
#include "Level.h"
#include <iostream>


Level::Level() {}

Level::~Level() {}

bool Level::isEmpty() const {
	return level.empty();
}

bool Level::existsAt(const Point& point) const {
	return true;
}

Block Level::getBlockAt(const Point& point) const {
	return Block();
}

void Level::addBlockAt(const Block& block) {
	level.insert({ block.getLocation(), block });
}


void Level::removeBlockAt(const Point& point) {
	level.erase(point);
}


void Level::draw(sf::RenderWindow& window) {
	for (map<Point, Block, ByLocation>::iterator iterator = level.begin(); iterator != level.end(); ++iterator){
		iterator->second.draw(window);
	}
}