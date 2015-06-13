#include "stdafx.h"
#include "Point.h"

#include <iostream>

Point::Point(int x = 0, int y = 0, int z = 0) :x(x), y(y), z(z) {}

bool Point::equals(const Point& p) const {
	return (x == p.x && y == p.y && z == p.z);
}

Point::~Point() {}

void Point::print() const {
	std::cout << "(" << x << "," << y << "," << z << ")";
}