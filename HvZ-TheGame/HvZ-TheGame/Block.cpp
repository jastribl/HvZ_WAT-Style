#include "stdafx.h"
#include "Constants.h"
#include "Block.h"
#include "Point.h"
#include <iostream>

Block::Block() {}

Block::Block(int blockType, Point blockLocation, int level, const sf::Texture& texture) {
	type = blockType;
	location = blockLocation;
	sprite = sf::Sprite(texture);
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(location.getX() * HALF_BLOCK_SIZE, location.getY() * HALF_BLOCK_SIZE);
	sprite.setPosition(p.getX() - HALF_BLOCK_SIZE, p.getY() - (level * HALF_BLOCK_SIZE));
}


Block::~Block() {}

int Block::getType(){
	return type;
}

void Block::setType(int blockType) {
	type = blockType;
}

Point Block::getLocation() const {
	return location;
}

int Block::getX() const {
	return location.getX();
}

int Block::getY() const {
	return location.getY();
}

void Block::setLocation(Point blockLocation) {
	location = blockLocation;
}

void Block::draw(sf::RenderWindow& window) {
	if (visible)
		window.draw(sprite);
}

void Block::toggleVisible(){
	visible = !visible;
}