#pragma once

#include "Point.h"

//File contains only static constant variables and methods

static int SCREEN_SIZE_X = 1920;
static int SCREEN_SIZE_Y = 1080;

static int BLOCK_SIZE = 64;
static int HALF_BLOCK_SIZE = BLOCK_SIZE / 2;

static int BLOCK = 0, SPECIAL = 1, CHARACTER = 2;

static Point cartesianToIsometric(const int x, const int y, const int z){
	return Point(x - y, (x + y) / 2, z);
}

static Point cartesianToIsometric(const Point& p) {
	return cartesianToIsometric(p.x, p.y, p.z);
}

static Point cartesianToIsometric(const sf::Vector2f& p, int z) {
	return cartesianToIsometric(p.x, p.y, z);
}

static Point isometricToCartesian(const int x, const int y, const int z) {
	return Point((2 * y + x) / 2, (2 * y - x) / 2, z);
}

static Point isometricToCartesian(const Point& p) {
	return isometricToCartesian(p.x, p.y, p.z);
}

static Point isometricToCartesian(const sf::Vector2f& p, int z) {
	return isometricToCartesian(p.x, p.y, z);
}

static Point screenToGrid(const sf::Vector2f& p, int z) {
	Point point = isometricToCartesian(p, z);
	return Point(point.x / HALF_BLOCK_SIZE, point.y / HALF_BLOCK_SIZE, point.z);
}