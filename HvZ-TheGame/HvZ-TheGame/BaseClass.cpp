#include "stdafx.h"

#include "BaseClass.h"
#include "Point.h"

BaseClass::BaseClass(Point grid, const sf::Texture& texture, int level)
	:gridLocation(grid), gridDestination(grid), sprite(texture), level(level) {}

BaseClass::~BaseClass() {}

void BaseClass::move(int x, int y){}
void BaseClass::applyMove() {}
void BaseClass::stop() {}

void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}