#include "stdafx.h"
#include "Constants.h"
#include "Block.h"
#include "Point.h"
#include <iostream>

Block::Block(int blockType, Point blockLocation, int level, const sf::Texture& texture)
	:type(blockType), location(blockLocation) {
	sprite = sf::Sprite(texture);
	sprite.scale(BLOCK_SIZE / sprite.getLocalBounds().width, BLOCK_SIZE / sprite.getLocalBounds().height);
	Point p = cartesianToIsometric(location.x * HALF_BLOCK_SIZE, location.y * HALF_BLOCK_SIZE);
	sprite.setPosition(p.x - HALF_BLOCK_SIZE, p.y - (level * HALF_BLOCK_SIZE));
}

Block::~Block() {}

void Block::draw(sf::RenderWindow& window) {
	if (visible)
		window.draw(sprite);
}

void Block::toggleVisible() {
	visible = !visible;
}