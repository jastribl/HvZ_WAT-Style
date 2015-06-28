#include "stdafx.h"
#include "BaseClass.h"
#include "Location.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point, int itemGroup)
	:world(world), sprite(texture), loc(grid, point), tempLoc(loc), itemType(itemGroup) {
	world.add(this);
}

BaseClass::~BaseClass() {}

sf::FloatRect& BaseClass::screenHitBox() {
	return sprite.getGlobalBounds();
}

void BaseClass::updateSprite() {}

void BaseClass::fly() {}

void BaseClass::applyMove() {
	loc = Location(tempLoc);
}

void BaseClass::stop() {
	tempLoc = Location(loc);
}

void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}