#include "stdafx.h"
#include "BaseClass.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, sf::Vector3i gridLocation, sf::Vector3f pointLocation, int itemGroup)
	:world(world), sprite(texture), gridLocation(gridLocation), pointLocation(pointLocation), itemGroup(itemGroup) {
	world.add(this);
}

BaseClass::~BaseClass() {}

void BaseClass::move(float x, float y, float z) {}
void BaseClass::hitDetect(BaseClass& test) {}
void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}