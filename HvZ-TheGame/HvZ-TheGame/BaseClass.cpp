#include "stdafx.h"
#include "BaseClass.h"
#include "Location.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& grid, const sf::Vector3f& point, int itemGroup)
	:world(world), sprite(texture), location (grid, point), itemGroup(itemGroup) {
	world.add(this);
}

BaseClass::~BaseClass() {}

sf::FloatRect& BaseClass::hitBox() {
	return sprite.getGlobalBounds();
}

const sf::Vector3i& BaseClass::getGridLocation() const {
	return location.getGridLocation();
}

const sf::Vector3f& BaseClass::getPointLocation() const {
	return location.getPointLocation();;
}

void BaseClass::setGridLocation(const sf::Vector3i& grid) {
	this->location.setGridLocation(grid);
}

void BaseClass::setPointLocaton(const sf::Vector3f& point) {
	this->location.setPointLocaton(point);
}

void BaseClass::fly() {}

void BaseClass::move(float x, float y, float z) {}

void BaseClass::applyMove() {
	setGridLocation(gridTemp);
	setPointLocaton(pointTemp);
}

void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}