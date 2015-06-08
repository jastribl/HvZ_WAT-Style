#pragma once

class Point {

public:
	int x, y;

	Point(int, int);

	bool equals(const Point& p) const;

	void print() const;
};