#include "stdafx.h"
#include "Location.h"
#include "Constants.h"

Location::Location() :grid(sf::Vector3i()), point(sf::Vector3f()) {}

Location::Location(sf::Vector3i grid, sf::Vector3f point) : grid(grid), point(point) {}

Location::~Location() {}

const sf::Vector3i& Location::getGrid() const {
	return grid;
}

const sf::Vector3f& Location::getPoint() const {
	return point;
}

void Location::setGrid(const sf::Vector3i& grid) {
	this->grid = sf::Vector3i(grid);
}

void Location::setPoint(const sf::Vector3f& point) {
	this->point = sf::Vector3f(point);
}

void Location::add(float x, float y, float z) {
	point.x += x;
	point.y += y;
	point.z += z;
	if (point.x >= BLOCK_SIZE) {
		point.x -= BLOCK_SIZE;
		grid.x++;
	} else if (point.x < 0) {
		point.x += BLOCK_SIZE;
		grid.x--;
	}
	if (point.y >= BLOCK_SIZE) {
		point.y -= BLOCK_SIZE;
		grid.y++;
	} else if (point.y < 0) {
		point.y += BLOCK_SIZE;
		grid.y--;
	}
	if (point.z >= BLOCK_SIZE) {
		point.z -= BLOCK_SIZE;
		grid.z++;
	} else if (point.z < 0) {
		point.z += BLOCK_SIZE;
		grid.z--;
	}
}