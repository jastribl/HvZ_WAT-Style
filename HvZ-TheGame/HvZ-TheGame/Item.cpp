#include "stdafx.h"
#include "Item.h"
#include "Constants.h"
#include <iostream>

Item::Item(const sf::Texture& texture)
	:sprite(texture) {
	sprite.setScale(((float) (BOX_WIDTH - 2 * BOX_BORDER) / sprite.getLocalBounds().width), ((float) (BOX_LENGTH - 2 * BOX_BORDER) / sprite.getLocalBounds().height));
	active = false;
}

Item::~Item() {}

void Item::setActive() {
	active = true;
}

void Item::release() {
	active = false;
}

bool Item::isActive() {
	return active;
}

void Item::draw(sf::RenderWindow& window) {
	window.draw(sprite);
}