#include "stdafx.h"
#include "BaseClass.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, Point gridLocation, Point pointLocation, int itemGroup)
	:world(world), sprite(texture), gridLocation(gridLocation), pointLocation(pointLocation), itemGroup(itemGroup){
	world.add(this);
}

BaseClass::~BaseClass() {}

void BaseClass::move(int x, int y, int z){}
void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}