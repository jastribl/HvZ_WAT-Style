#pragma once
#include "Point.h"
#include "Block.h"
#include <map>

class Level {

private:
	struct ByLocation {
		bool operator()(const Point& a, const Point& b) const
		{
			if (a.getY() == b.getY()) {
				if (a.getX() == b.getX()) {
					return false;
				}
				else {
					return (a.getX() <= b.getX());
				}
			}
			else {
				return (a.getY() <= b.getY());
			}
		}
	};
	std::map<Point, Block, ByLocation> level;

public:
	Level();
	~Level();
	bool isEmpty() const;
	bool existsAt(const Point& point) const;
	Block getBlockAt(const Point& point) const;
	void addBlockAt(const Block& block);
	void removeBlockAt(const Point& point);
	void draw(sf::RenderWindow& window);
	int size();
};