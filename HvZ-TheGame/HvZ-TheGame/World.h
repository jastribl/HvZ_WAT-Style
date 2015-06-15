#pragma once
class BaseClass;
#include "Constants.h"
#include "Point.h"
#include <string>

class World {

private:
	std::multimap<Point, BaseClass*, ByLocation> world;
	std::vector<BaseClass*> deletedItems;

public:
	std::string name;

	World();
	~World();

	bool itemsExistAt(const Point& point) const;
	std::pair <std::multimap<Point, BaseClass*, ByLocation>::iterator, std::multimap<Point, BaseClass*, ByLocation>::iterator> getItemsAt(const Point& point);
	void add(BaseClass* object);
	void removeFromMap(const Point& grid, const Point& point);
	void deleteItem(const Point& grid, const Point& point);
	void clearDeletedItems();
	void draw(sf::RenderWindow& window);
	int size() const;
};