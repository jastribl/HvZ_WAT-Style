#pragma once
class BaseClass;
#include "Point.h"

class World {

private:
	struct ByLocation {
		bool operator()(const Point& a, const Point& b) const
		{
			return (a.y == b.y ? (a.x == b.x ? (a.z < b.z) : (a.x < b.x)) : (a.y < b.y));
		}
	};
	std::map<Point, BaseClass*, ByLocation> world;

public:
	World();
	~World();

	bool existsAt(const Point& point) const;
	BaseClass* getAt(const Point& point);
	void add(BaseClass* object);
	void removeAt(const Point& point);
	void draw(sf::RenderWindow& window);
	int size() const;
};