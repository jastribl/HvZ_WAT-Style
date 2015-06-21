#include "stdafx.h"
#include "Location.h"

Location::Location(sf::Vector3i grid, sf::Vector3f point) :grid(grid), point(point) {}

Location::~Location() {}

const sf::Vector3i& Location::getGridLocation() const {
	return grid;
}

const sf::Vector3f& Location::getPointLocation() const {
	return point;
}

void Location::setGridLocation(const sf::Vector3i& grid) {
	this->grid = sf::Vector3i(grid);
}

void Location::setPointLocaton(const sf::Vector3f& point) {
	this->point = sf::Vector3f(point);
}