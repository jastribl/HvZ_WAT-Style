#pragma once

#include "Point.h"

//File contains only static constant variables and methods

static int SCREEN_SIZE_X = 1920;
static int SCREEN_SIZE_Y = 1080;

static int BLOCK_SIZE = 64;
static int HALF_BLOCK_SIZE = BLOCK_SIZE / 2;

static int CHARACTER_WIDTH = BLOCK_SIZE, CHARACTER_HEIGHT = BLOCK_SIZE * 2;

enum BLOCK_TYPE{ BLOCK, SPECIAL, CHARACTER };

static Point& cartesianToIsometric(const int x, const int y, const int z){
	return Point(x - y, (x + y) / 2, z);
}

static Point& isometricToCartesian(const int x, const int y, const int z) {
	return Point((2 * y + x) / 2, (2 * y - x) / 2, z);
}

static Point& screenToGrid(const sf::Vector2f& p, const int z) {
	Point& point = isometricToCartesian(p.x, p.y, z);
	return Point(point.x / HALF_BLOCK_SIZE, point.y / HALF_BLOCK_SIZE, point.z);
}