#include "stdafx.h"
#include "BaseClass.h"
#include "World.h"

BaseClass::BaseClass(World& world, const sf::Texture& texture, const sf::Vector3i& gridLoc, const sf::Vector3f& pointLoc, int itemGroup)
	:world(world), sprite(texture), gridLoc(gridLoc), pointLoc(pointLoc), itemGroup(itemGroup) {
	world.add(this);
}

BaseClass::~BaseClass() {}

sf::FloatRect& BaseClass::hitBox() {
	return sprite.getGlobalBounds();
}
void BaseClass::fly() {}
void BaseClass::move(float x, float y, float z) {}
void BaseClass::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}