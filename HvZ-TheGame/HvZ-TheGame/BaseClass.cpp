#include "stdafx.h"
#include "BaseClass.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, Point grid)
	:world(world), sprite(texture), gridLocation(grid), gridDestination(grid)  {
	world.add(this);
}

BaseClass::~BaseClass() {}

void BaseClass::setStageLocation(Point& point) {}
void BaseClass::setStageLocation(int x, int y, int z){}
void BaseClass::stageShift(int x, int y, int z){}
void BaseClass::commitMove() {}
void BaseClass::cancelMove() {}

void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}