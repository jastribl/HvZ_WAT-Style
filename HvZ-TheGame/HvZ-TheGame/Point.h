#pragma once

class Point {

public:
	int x, y, z;

	Point(int x, int y, int z);
	~Point();

	bool equals(const Point& p) const;
	void print() const;
};