#pragma once

static const int SCREEN_SIZE_X = 1920;
static const int SCREEN_SIZE_Y = 1080;

//Sidebar
static const int SIDEBAR_ORIGIN_X = 1620;
static const int SIDEBAR_ORIGIN_Y = 0;
static const int SIDEBAR_LENGTH = SCREEN_SIZE_X - SIDEBAR_ORIGIN_X;

//HP and MP Bar
static const int HP_BORDER = 3;
static const int HP_LENGTH = 299;
static const int HP_WIDTH = 150;
static const int HP_ORIGIN_X = SCREEN_SIZE_X - HP_LENGTH;
static const int HP_ORIGIN_Y = SCREEN_SIZE_Y - HP_WIDTH;
static const int HP_TEXT_X = 20;
static const int HP_TEXT_Y = 15;
static const int HP_MPTEXT_X = 20;
static const int HP_MPTEXT_Y = 70;
static const int HP_BAR_X = 20;
static const int HP_BAR_Y = 45;
static const int HP_BAR_LENGTH = 260;
static const int HP_BAR_WIDTH = 20;
static const int HP_MPBAR_X = 20;
static const int HP_MPBAR_Y = 100;

//Inventory Boxes
static const int BOX_LENGTH = 73;
static const int BOX_WIDTH = 73;
static const int BOX_BORDER = 3;
static const int BOX_LGBORDER = 2;
static const int BOX_MOVEMENT_X = 70;
static const int BOX_MOVEMENT_Y = 70;
static const int BOX_PERROW = 4;
static const int BOX_NUM = 28;
static const int BOX_SIZE_LENGTH = BOX_PERROW*BOX_LENGTH - BOX_BORDER*(BOX_PERROW - 1);
static const int BOX_SIZE_WIDTH = ((BOX_NUM - 1) / BOX_PERROW + 1)*BOX_WIDTH - BOX_BORDER*((BOX_NUM - 1) / BOX_PERROW);
static const int BOX_ORIGIN_X = SIDEBAR_ORIGIN_X + BOX_LGBORDER + 6;
static const int BOX_ORIGIN_Y = 425 + BOX_LGBORDER;

//World blocks
static const int BLOCK_SIZE = 64;
static const int HALF_BLOCK_SIZE = BLOCK_SIZE / 2;

//Character
static const int CHARACTER_WIDTH = BLOCK_SIZE, CHARACTER_HEIGHT = BLOCK_SIZE * 2;
static const int NUMBER_OF_PLAYER_ROTATIONS = 8;

//movement settings
static const float MAX_MOVEMENT_CHECK_THRESHOLD = 10;   //must be less than block size

//Item groups
static const int NUMBER_OF_BLOCK_TYPES = 9, NUMBER_OF_SPECIAL_TYPES = 6, NUMBER_OF_INVENTORY_ITEMS = 7;
static const enum ITEM_GROUP { BLOCK, SPECIAL, CHARACTER, INVENTORY_ITEM };

//common algorithms
static sf::Vector3i cartesianToIsometric(const int x, const int y, const int z) {
	return sf::Vector3i(x - y, ((x + y) / 2) - z, z);
}

static sf::Vector3i isometricToCartesian(const int x, const int y, const int z) {
	return sf::Vector3i((2 * y + x) / 2, (2 * y - x) / 2, z);
}

template <typename T>
static sf::Vector3i& isometricToCartesian(T const& p, const int z) {
	return isometricToCartesian(p.x, p.y, z);
}

template <typename T>
static sf::Vector3i& screenToGrid(T const& p, const int z) {
	sf::Vector3i& point = isometricToCartesian(p.x, p.y, z);
	return sf::Vector3i(point.x / BLOCK_SIZE, point.y / BLOCK_SIZE, point.z);
}

template <typename T>
static void PrintVector(T const& v) {
	std::cout << "(" << v.x << "," << v.y << ","v.z")" << std << enbdl;
}

struct ByLocation {
	bool operator()(const sf::Vector3i& a, const sf::Vector3i& b) const {
		return (a.x + a.y == b.x + b.y) ? std::min(a.x, a.y) == std::min(b.x, b.y) ? a.y == b.y ? a.z < b.z : a.y > b.y : std::min(a.x, a.y) < std::min(b.x, b.y) : a.x + a.y < b.x + b.y;
		//if (a.x + a.y == b.x + b.y) {
		//	if (std::min(a.x, a.y) == std::min(b.x, b.y)) {
		//		if (a.y == b.y) {
		//			return (a.z < b.z);
		//		} else {
		//			return (a.y > b.y);
		//		}
		//	} else {
		//		return (std::min(a.x, a.y) < std::min(b.x, b.y));
		//	}
		//} else {
		//	return (a.x + a.y < b.x + b.y);
		//}
	}
};
