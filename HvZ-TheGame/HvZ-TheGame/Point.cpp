#include "stdafx.h"
#include "Point.h"
#include <iostream>

Point::Point() :x(0), y(0), z(0) {}
Point::Point(int x, int y, int z) : x(x), y(y), z(z) {}
Point::Point(const Point& p) : x(p.x), y(p.y), z(p.z) {}

bool Point::equals(const Point& p) const {
	return (x == p.x && y == p.y && z == p.z);
}

Point::~Point() {}

void Point::print() const {
	std::cout << "(" << x << "," << y << "," << z << ")";
}