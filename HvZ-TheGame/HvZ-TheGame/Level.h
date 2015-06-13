#pragma once
#include "Point.h"
#include "BaseClass.h"
#include <map>

class Level {

private:
	struct ByLocation {
		bool operator()(const Point& a, const Point& b) const
		{
			if (a.y == b.y) {
				return (a.x < b.x);
			}
			else {
				return (a.y <= b.y);
			}
		}
	};
	std::map<Point, BaseClass*, ByLocation> level;

public:
	Level();
	~Level();

	bool isEmpty() const;
	bool existsAt(const Point& point) const;
	BaseClass* getAt(const Point& point);
	void add(BaseClass* object);
	void removeAt(const Point& point);
	void draw(sf::RenderWindow& window);
	int size() const;
};