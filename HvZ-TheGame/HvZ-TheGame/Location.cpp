#include "stdafx.h"
#include "Location.h"

Location::Location(sf::Vector3i grid, sf::Vector3f point) :grid(grid), point(point) {}

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

void Location::addGridX(int x) {
	this->grid.x += x;
}

void Location::addGridY(int y) {
	this->grid.y += y;
}

void Location::addGridZ(int z) {
	this->grid.z += z;
}

void Location::addPointX(int x) {
	this->point.x += x;
}

void Location::addPointY(int y) {
	this->point.y += y;
}

void Location::addPointZ(int z) {
	this->point.z += z;
}