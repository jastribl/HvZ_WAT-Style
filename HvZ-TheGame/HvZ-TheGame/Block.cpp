#include "stdafx.h"
#include "Block.h"
#include "Point.h"
#include <iostream>

Block::Block() {
}

Block::Block(int blockType, Point blockLocation, sf::Texture texture) {
	type = blockType;
	location = blockLocation;
	sprite.setTexture(texture);
	sprite.setPosition(200, 200);
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
	window.draw(sprite);
	cout << "draw" << endl;
}