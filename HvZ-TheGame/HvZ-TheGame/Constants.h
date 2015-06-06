#pragma once

#include "Point.h"

//File contains only static constant variables and methods

static int SCREEN_SIZE_X = 1366;
static int SCREEN_SIZE_Y = 768;

static int BLOCK_SIZE = 32;
static int HALF_BLOCK_SIZE = BLOCK_SIZE / 2;
static int LEVEL_OFFSET = BLOCK_SIZE / 4;

static Point cartesianToIsometric(const int x, const int y){
	return Point(x - y, (x + y) / 2);
}

static Point cartesianToIsometric(const Point& point) {
	return Point(point.getX() - point.getY(), (point.getX() + point.getY()) / 2);
}

static Point isometricToCartesian(const int x, const int y) {
	return Point((2 * y + x) / 2, (2 * y - x) / 2);
}

static Point isometricToCartesian(const Point& point) {
	return Point((2 * point.getY() + point.getX()) / 2, (2 * point.getY() - point.getX()) / 2);
}