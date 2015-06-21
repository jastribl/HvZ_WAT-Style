#include "stdafx.h"
#include "BaseClass.h"
#include "Location.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point, int itemGroup)
	:world(world), sprite(texture), loc(grid, point), tempLoc(sf::Vector3i(), sf::Vector3f()), itemGroup(itemGroup) {
	world.add(this);
}

BaseClass::~BaseClass() {}

sf::FloatRect& BaseClass::hitBox() {
	return sprite.getGlobalBounds();
}

void BaseClass::fly() {}

void BaseClass::move(float x, float y, float z) {}

void BaseClass::applyMove() {
	loc.setGrid(tempLoc.getGrid());
	loc.setPoint(tempLoc.getPoint());
}

void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}