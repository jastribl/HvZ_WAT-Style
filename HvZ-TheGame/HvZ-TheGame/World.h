#pragma once
#include "BaseClass.h"
#include <vector>

class World {

private:
	struct ByLocation {
		bool operator()(const Point& a, const Point& b) const
		{
			if (a.y == b.y){
				if (a.x == b.x){
					return (a.z < b.z);
				}
				return (a.x < b.x);
			}
			return (a.y < b.y);
			//return (a.y == b.y ? (a.x == b.x ? (a.z < b.z) : (a.x < b.x)) : (a.y < b.y));
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