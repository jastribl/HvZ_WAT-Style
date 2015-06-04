#ifndef POINT_H
#define POINT_H

using namespace std;

class Point {

private:
	double x, y;

public:
	Point(double, double);
	double getX() {
		return x;
	}
	double getY() {
		return y;
	}
	bool operator == (const Point &) const;
	Point operator + (const Point &) const;
	Point operator - (const Point &) const;
	void operator += (const Point &);
	void operator -= (const Point &);
	void print() const;
};

#endif