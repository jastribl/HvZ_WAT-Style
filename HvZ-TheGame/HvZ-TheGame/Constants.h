#pragma once

#include "Point.h"

//File contains only static constant variables and methods

static int SCREEN_SIZE_X = 1920;
static int SCREEN_SIZE_Y = 1080;

//Sidebar
static int SIDEBAR_ORIGIN_X = 1620;
static int SIDEBAR_ORIGIN_Y = 0;
static int SIDEBAR_LENGTH = SCREEN_SIZE_X - SIDEBAR_ORIGIN_X;

//HP and MP Bar
static int HP_BORDER = 3;
static int HP_LENGTH = 299;
static int HP_WIDTH = 150;
static int HP_ORIGIN_X = SCREEN_SIZE_X - HP_LENGTH;
static int HP_ORIGIN_Y = SCREEN_SIZE_Y - HP_WIDTH;
static int HP_TEXT_X = 20;
static int HP_TEXT_Y = 15;
static int HP_MPTEXT_X = 20;
static int HP_MPTEXT_Y = 70;
static int HP_BAR_X = 20;
static int HP_BAR_Y = 45;
static int HP_BAR_LENGTH = 260;
static int HP_BAR_WIDTH = 20;
static int HP_MPBAR_X = 20;
static int HP_MPBAR_Y = 100;

//Inventory Boxes
static int BOX_LENGTH = 73;
static int BOX_WIDTH = 73;
static int BOX_BORDER = 3;
static int BOX_LGBORDER = 2;
static int BOX_MOVEMENT_X = 70;
static int BOX_MOVEMENT_Y = 70;
static int BOX_PERROW = 4;
static int BOX_NUM = 28;
static int BOX_SIZE_LENGTH = BOX_PERROW*BOX_LENGTH - BOX_BORDER*(BOX_PERROW - 1);
static int BOX_SIZE_WIDTH = ((BOX_NUM - 1) / BOX_PERROW + 1)*BOX_WIDTH - BOX_BORDER*((BOX_NUM - 1) / BOX_PERROW);
static int BOX_ORIGIN_X = SIDEBAR_ORIGIN_X + BOX_LGBORDER + 6;
static int BOX_ORIGIN_Y = 425 + BOX_LGBORDER;

//World blocks
static int BLOCK_SIZE = 64;
static int HALF_BLOCK_SIZE = BLOCK_SIZE / 2;

static int CHARACTER_WIDTH = BLOCK_SIZE, CHARACTER_HEIGHT = BLOCK_SIZE * 2;

enum ITEM_GROUP{ BLOCK, SPECIAL, CHARACTER };

static Point& cartesianToIsometric(const int x, const int y, const int z){
	return Point(x - y, ((x + y) / 2) - z, z);
}

static Point& isometricToCartesian(const int x, const int y, const int z) {
	return Point((2 * y + x) / 2, (2 * y - x) / 2, z);
}

static Point& screenToGrid(const sf::Vector2f& p, const int z) {
	Point& point = isometricToCartesian(p.x, p.y, z);
	return Point(point.x / HALF_BLOCK_SIZE, point.y / HALF_BLOCK_SIZE, point.z);
}

static Point& screenToPoint(const sf::Vector2f& p, const int z) {
	Point& point = isometricToCartesian(p.x, p.y, z);
	float xDiff = (point.x / HALF_BLOCK_SIZE) - point.x;
	float yDiff = (point.y / HALF_BLOCK_SIZE) - point.y;
	return Point(xDiff, yDiff, point.z);
}

struct ByLocation {
	bool operator()(const Point& a, const Point& b) const
	{
		return (a.x + a.y == b.x + b.y) ? std::min(a.x, a.y) == std::min(b.x, b.y) ? a.y == b.y ? a.z < b.z : a.y > b.y : std::min(a.x, a.y) < std::min(b.x, b.y) : a.x + a.y < b.x + b.y;
	}
};
