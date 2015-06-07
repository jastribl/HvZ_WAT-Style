#pragma once

#include "Point.h"

//File contains only static constant variables and methods

static int SCREEN_SIZE_X = 1366;
static int SCREEN_SIZE_Y = 768;

static int BLOCK_SIZE = 64;
static int HALF_BLOCK_SIZE = BLOCK_SIZE / 2;

static Point cartesianToIsometric(const int x, const int y){
	return Point(x - y, (x + y) / 2);
}

static Point cartesianToIsometric(const Point& p) {
	return cartesianToIsometric(p.getX(), p.getY());
}

static Point cartesianToIsometric(const sf::Vector2f& p) {
	return cartesianToIsometric(p.x, p.y);
}

static Point isometricToCartesian(const int x, const int y) {
	return Point((2 * y + x) / 2, (2 * y - x) / 2);
}

static Point isometricToCartesian(const Point& p) {
	return isometricToCartesian(p.getX(), p.getY());
}

static Point isometricToCartesian(const sf::Vector2f& p) {
	return isometricToCartesian(p.x, p.y);
}

static Point screenToGrid(const sf::Vector2f& p) {
	Point point = isometricToCartesian(p);
	return Point(point.getX() / HALF_BLOCK_SIZE, point.getY() / HALF_BLOCK_SIZE);
}