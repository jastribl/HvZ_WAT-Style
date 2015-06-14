#pragma once
class BaseClass;
#include "Constants.h"
#include "Point.h"
#include <string>

class World {

private:
	struct ByLocation {
		bool operator()(const Point& a, const Point& b) const
		{
			return (a.x + a.y == b.x + b.y) ? std::min(a.x, a.y) == std::min(b.x, b.y) ? a.y == b.y ? a.z < b.z : a.y > b.y : std::min(a.x, a.y) < std::min(b.x, b.y) : a.x + a.y < b.x + b.y;
			//if (a.x + a.y == b.x + b.y){
			//	if (std::min(a.x, a.y) == std::min(b.x, b.y)){
			//		if (a.y == b.y){
			//			return a.z < b.z;
			//		}
			//		return a.y > b.y;
			//	}
			//	return std::min(a.x, a.y) < std::min(b.x, b.y);
			//}
			//return a.x + a.y < b.x + b.y;
		}
	};
	std::map<Point, BaseClass*, ByLocation> world;

public:
	std::string name;

	World();
	~World();

	bool existsAt(const Point& point) const;
	BaseClass* getAt(const Point& point);
	void add(BaseClass* object);
	void removeAt(const Point& point);
	void draw(sf::RenderWindow& window);
	int size() const;
};