#pragma once

class Point {

private:
	double x, y;

public:
	Point();
	Point(double, double);
	double getX() const;
	double getY() const;
	bool operator == (const Point &) const;
	Point operator + (const Point &) const;
	Point operator - (const Point &) const;
	void operator += (const Point &);
	void operator -= (const Point &);
	void print() const;
};