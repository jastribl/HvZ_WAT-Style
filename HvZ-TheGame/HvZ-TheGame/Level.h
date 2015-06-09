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
	std::map<Point, BaseClass, ByLocation> level;

public:
	Level();
	~Level();
	bool isEmpty() const;
	bool blockExitsAt(const Point& point) const;
	BaseClass& getBlockAt(const Point& point);
	void addBlock(const BaseClass& block);
	void removeBlockAt(const Point& point);
	void draw(sf::RenderWindow& window);
	int size() const;
};