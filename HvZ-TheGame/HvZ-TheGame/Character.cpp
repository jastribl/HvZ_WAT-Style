#include "stdafx.h"
#include "Constants.h"
#include "Character.h"

Character::Character(Point grid, Point point, int level, const sf::Texture& texture)
	:gridLocation(grid), gridDestination(grid), characterLevel(level){
	sprite = sf::Sprite(texture);
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (characterLevel * HALF_BLOCK_SIZE));
}

Character::~Character() {
}

Point Character::getGridLocation() const {
	return gridLocation;
}

void Character::setGridLocation(Point point) {
	gridLocation = point;
}

Point Character::getGridDestination() const {
	return gridDestination;
}

void Character::setGridDestination(Point point) {
	gridDestination = point;
}

void Character::move(int x, int y) {
	gridDestination.x += x;
	gridDestination.y += y;
}

void Character::stop() {
	gridDestination = gridLocation;
}

void Character::draw(sf::RenderWindow& window) {
	gridLocation = gridDestination;
	Point p = cartesianToIsometric(gridLocation.x * HALF_BLOCK_SIZE, gridLocation.y * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (characterLevel * HALF_BLOCK_SIZE));
	window.draw(sprite);
}