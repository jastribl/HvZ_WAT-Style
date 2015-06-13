#pragma once

class Point {

public:
	int x, y, z;

	Point(int x, int y, int z);
	Point(const Point& p);
	~Point();

	bool equals(const Point& p) const;
	void print() const;
};