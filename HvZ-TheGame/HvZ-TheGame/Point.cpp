#include "stdafx.h"
#include "Point.h"

#include <iostream>
#include <math.h>

using namespace std;

Point::Point() {}

Point::Point(double xGiven = 0.0, double yGiven = 0.0) {
	x = xGiven;
	y = yGiven;
}

bool Point::operator == (const Point & p) const
{
	return (x == p.x && y == p.y);
}

double Point::getX() const {
	return x;
}
double Point::getY() const {
	return y;
}

Point Point::operator + (const Point & p) const
{
	return Point(x + p.x, y + p.y);
}

Point Point::operator - (const Point & p) const
{
	return Point(x - p.x, y - p.y);
}

void Point::operator += (const Point & p){
	x += p.x;
	y += p.y;
}

void Point::operator -= (const Point & p){
	x -= p.x;
	y -= p.y;
}

void Point::print() const
{
	cout << "(" << x << "," << y << ")";
}