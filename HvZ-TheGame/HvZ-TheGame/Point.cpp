#include "stdafx.h"
#include "Point.h"
#include <iostream>
#include <math.h>

Point::Point(int xx = 0, int yy = 0) :x(xx), y(yy) {}

void Point::print() const {
	std::cout << "(" << x << "," << y << ")";
}