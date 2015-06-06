#include "stdafx.h"
#include "Point.h"

#include <iostream>
#include <math.h>


Point::Point() {
	x = 0.0;
	y = 0.0;
}

Point::Point(double xGiven = 0.0, double yGiven = 0.0) {
	x = xGiven;
	y = yGiven;
}

double Point::getX() const {
	return x;
}

double Point::getY() const {
	return y;
}

bool Point::operator == (const Point & p) const {
	return (x == p.x && y == p.y);
}

Point Point::operator + (const Point & p) const {
	return Point(x + p.x, y + p.y);
}

Point Point::operator - (const Point & p) const {
	return Point(x - p.x, y - p.y);
}

void Point::operator += (const Point & p) {
	x += p.x;
	y += p.y;
}

void Point::operator -= (const Point & p) {
	x -= p.x;
	y -= p.y;
}

void Point::print() const {
	std::cout << "(" << x << "," << y << ")";
}