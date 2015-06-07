#include "stdafx.h"
#include "Point.h"
#include <iostream>
#include <math.h>

Point::Point() {
	x = 0.0;
	y = 0.0;
}

Point::Point(int xGiven = 0, int yGiven = 0) {
	x = xGiven;
	y = yGiven;
}

int Point::getX() const {
	return x;
}

int Point::getY() const {
	return y;
}

void Point::setX(int xG) {
	x = xG;
}

void Point::setY(int yG) {
	y = yG;
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