#include "stdafx.h"
#include "Item.h"
#include "Constants.h"
#include <iostream>

Item::Item() {
	initialized = false;
	active = false;
}

void Item::initalize(std::string file) {
	if (!texture.loadFromFile(file)) {
		std::cerr << "Error loading: " << file << std::endl;
		return;
	}
	setTexture(texture);
	initialized = true;
}

Item::~Item() {}

void Item::setActive() {
	active = true;
}

void Item::release() {
	active = false;
}

bool Item::isInitialized() {
	return initialized;
}

bool Item::isActive() {
	return active;
}
