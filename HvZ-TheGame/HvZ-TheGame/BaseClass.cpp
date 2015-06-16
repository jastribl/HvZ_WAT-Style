#include "stdafx.h"
#include "BaseClass.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3i pointLocation, int itemGroup)
	:world(world), sprite(texture), gridLocation(gridLocation), pointLocation(pointLocation), itemGroup(itemGroup) {
	world.add(this);
}

BaseClass::~BaseClass() {}

void BaseClass::move(int x, int y, int z) {}
void BaseClass::hitDetect(BaseClass& test) {}
void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}