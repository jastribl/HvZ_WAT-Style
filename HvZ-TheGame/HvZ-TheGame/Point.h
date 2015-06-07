#pragma once

class Point {

private:
	double x, y;

public:
	Point();
	Point(int, int);
	int getX() const;
	int getY() const;
	void Point::setX(int xG);
	void Point::setY(int yG);
	bool operator == (const Point&) const;
	Point operator + (const Point&) const;
	Point operator - (const Point&) const;
	void operator += (const Point&);
	void operator -= (const Point&);
	void print() const;
};