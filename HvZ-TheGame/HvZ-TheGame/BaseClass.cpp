#include "stdafx.h"
#include "BaseClass.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, Point grid)
	:world(world), sprite(texture), gridLocation(grid)  {
	world.add(this);
}

BaseClass::~BaseClass() {}

void BaseClass::move(int x, int y, int z){}
void BaseClass::draw(sf::RenderWindow& window) {}