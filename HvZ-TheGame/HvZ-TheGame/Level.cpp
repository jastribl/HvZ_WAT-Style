#include "stdafx.h"
#include "Level.h"


Level::Level() {}

Level::~Level() {}

bool Level::isEmpty() const{
	return level.empty();
}

bool Level::existsAt(const Point& point) const{
	return true;
}

Block Level::getBlockAt(const Point& point) const{
	return Block();
}

void Level::addBlockAt(Block block, const Point& point) {
	//yet to come
}