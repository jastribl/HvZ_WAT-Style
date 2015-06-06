#pragma once
#include "Point.h"
#include "Block.h"
#include <map>

class Level {

private:
	struct ByLocation : public std::binary_function < Block, Block, bool >
	{
		bool operator()(const Block& a, const Block& b) const
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
	map<Point, Block, ByLocation> level;

public:
	Level();
	~Level();
	bool isEmpty() const;
	bool existsAt(const Point& point) const;
	Block getBlockAt(const Point& point) const;
	void addBlockAt(Block block, const Point& point);
};