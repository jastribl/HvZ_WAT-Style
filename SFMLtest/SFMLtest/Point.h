#ifndef POINT_H
#define POINT_H
struct Point
{
	float x;
	float y;
	bool operator==(Point& right)
	{
		if ((x == right.x) && (y == right.y))
			return true;
		return false;
	}
};

#endif