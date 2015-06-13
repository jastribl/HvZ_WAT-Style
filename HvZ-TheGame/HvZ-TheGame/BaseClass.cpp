#include "stdafx.h"
#include "BaseClass.h"
#include "Point.h"

BaseClass::BaseClass(Point grid, const sf::Texture& texture)
	:gridLocation(grid), gridDestination(grid), sprite(texture) {}

BaseClass::~BaseClass() {}

void BaseClass::setStageLocation(Point& point) {}
void BaseClass::setStageLocation(int x, int y, int z){}
void BaseClass::stageShift(int x, int y, int z){}
void BaseClass::commitMove() {}
void BaseClass::cancelMove() {}

void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}